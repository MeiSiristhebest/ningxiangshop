package com.ningxiang.shop.product.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 宁享购多级缓存管理器
 * 用于动态创建并维护各缓存域的 MultilevelCache 实例。
 *
 * @author Ningxiang
 */
public class MultilevelCacheManager implements CacheManager {

    private final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    private final RedisTemplate<Object, Object> redisTemplate;
    private final RocketMQTemplate rocketMQTemplate;

    public MultilevelCacheManager(RedisTemplate<Object, Object> redisTemplate, RocketMQTemplate rocketMQTemplate) {
        this.redisTemplate = redisTemplate;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, k -> {
            // 构建本地一级缓存实例
            com.github.benmanes.caffeine.cache.Cache<Object, Object> localCache = Caffeine.newBuilder()
                    .initialCapacity(128)
                    .maximumSize(2000) // 限制最大大小，防止 JVM OOM
                    .expireAfterWrite(10, TimeUnit.MINUTES) // 本地缓存默认写入后 10 分钟过期
                    .build();

            // 整合二级分布式缓存 Redis，设置其过期时间为 2 小时
            return new MultilevelCache(name, localCache, redisTemplate, rocketMQTemplate, 7200);
        });
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(cacheMap.keySet());
    }
}
