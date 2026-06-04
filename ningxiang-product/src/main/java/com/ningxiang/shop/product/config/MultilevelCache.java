package com.ningxiang.shop.product.config;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 自定义多级缓存实现类，整合 Caffeine 本地缓存与 Redis 远程缓存
 *
 * @author Ningxiang
 */
public class MultilevelCache implements org.springframework.cache.Cache {

    private static final Logger logger = LoggerFactory.getLogger(MultilevelCache.class);

    private final String name;
    private final com.github.benmanes.caffeine.cache.Cache<Object, Object> localCache;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final RocketMQTemplate rocketMQTemplate;
    private final long expireTimeSeconds;

    public MultilevelCache(String name,
                           com.github.benmanes.caffeine.cache.Cache<Object, Object> localCache,
                           RedisTemplate<Object, Object> redisTemplate,
                           RocketMQTemplate rocketMQTemplate,
                           long expireTimeSeconds) {
        this.name = name;
        this.localCache = localCache;
        this.redisTemplate = redisTemplate;
        this.rocketMQTemplate = rocketMQTemplate;
        this.expireTimeSeconds = expireTimeSeconds;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        // 1. 尝试从一级本地缓存 (Caffeine) 读取
        Object value = localCache.getIfPresent(key);
        if (value != null) {
            logger.debug("--- [Caffeine] 命中一级缓存, Key: {}", key);
            return new SimpleValueWrapper(value);
        }

        // 2. 本地缓存未命中，尝试从二级缓存 (Redis) 读取
        String redisKey = getRedisKey(key);
        value = redisTemplate.opsForValue().get(redisKey);
        if (value != null) {
            logger.debug("+++ [Redis] 命中二级缓存, Key: {}", key);
            // 写入本地缓存，方便下次访问
            localCache.put(key, value);
            return new SimpleValueWrapper(value);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper wrapper = get(key);
        return wrapper == null ? null : (T) wrapper.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper wrapper = get(key);
        if (wrapper != null) {
            return (T) wrapper.get();
        }
        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        if (value == null) {
            return;
        }
        // 1. 写入本地缓存
        localCache.put(key, value);

        // 2. 写入 Redis 分布式缓存（并设置过期时间）
        String redisKey = getRedisKey(key);
        redisTemplate.opsForValue().set(redisKey, value, expireTimeSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void evict(Object key) {
        // 1. 清除 Redis 缓存
        String redisKey = getRedisKey(key);
        redisTemplate.delete(redisKey);

        // 2. 清除当前节点本地缓存
        localCache.invalidate(key);

        // 3. 广播 RocketMQ 消息，通知集群中的其它微服务实例清除本地缓存
        try {
            String syncMessage = this.name + ":" + key.toString();
            // 广播至 PRODUCT_CACHE_SYNC_TOPIC 频道
            rocketMQTemplate.convertAndSend("PRODUCT_CACHE_SYNC_TOPIC", syncMessage);
            logger.info("已向 RocketMQ 广播缓存清除消息，Key: {}", syncMessage);
        } catch (Exception e) {
            logger.error("广播缓存清除消息失败", e);
        }
    }

    /**
     * 仅清理本地缓存（供监听器收到广播消息后调用，避免循环广播）
     */
    public void clearLocal(Object key) {
        localCache.invalidate(key);
        logger.info("Caffeine 本地缓存已成功被清除，Key: {}", key);
    }

    @Override
    public void clear() {
        // 清理所有（通常在开发调试或后台做完全重建时调用）
        localCache.invalidateAll();
        // 这里不建议直接清除 Redis 所有数据，防止误删其它非本域下的缓存
    }

    private String getRedisKey(Object key) {
        return this.name + ":" + key.toString();
    }
}
