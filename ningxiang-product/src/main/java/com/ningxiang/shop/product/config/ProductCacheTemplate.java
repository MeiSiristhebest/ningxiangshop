package com.ningxiang.shop.product.config;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 宁享购多级缓存（Caffeine + Redis）操作模板
 * 封装了一级本地缓存、二级分布式缓存查询，以及数据库回源和多端一致性清除逻辑。
 *
 * @author Ningxiang
 */
@Component
public class ProductCacheTemplate {

    @Autowired
    @Qualifier("productLocalCache")
    private Cache<String, Object> productLocalCache;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 多级缓存统一获取方法
     *
     * @param key        缓存的完整 Key
     * @param clazz      期望返回的类型 Class
     * @param dbLoader   若两级缓存皆未命中的数据库回源加载器
     * @param expireTime 二级 Redis 缓存的有效期（单位：秒）
     * @return 缓存或数据库中获取的最新值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz, Supplier<T> dbLoader, long expireTime) {
        // 1. 尝试从一级本地缓存 (Caffeine) 中读取
        Object val = productLocalCache.getIfPresent(key);
        if (val != null) {
            return (T) val;
        }

        // 2. 本地缓存未命中，尝试从二级分布式缓存 (Redis) 中读取
        val = redisTemplate.opsForValue().get(key);
        if (val != null) {
            // 将从 Redis 中取出的数据存入本地缓存，方便下次快速访问
            productLocalCache.put(key, val);
            return (T) val;
        }

        // 3. 两级缓存均未命中，执行数据库回源（有防并发穿透保护作用）
        T dbVal = dbLoader.get();
        if (dbVal != null) {
            // 分别回写至二级缓存 (Redis) 和一级缓存 (Caffeine)
            redisTemplate.opsForValue().set(key, dbVal, expireTime, TimeUnit.SECONDS);
            productLocalCache.put(key, dbVal);
        }
        return dbVal;
    }

    /**
     * 强行清除指定 Key 的两级缓存 (常在商品数据被修改时触发)
     *
     * @param key 缓存的 Key
     */
    public void evict(String key) {
        // 1. 删除分布式缓存
        redisTemplate.delete(key);
        // 2. 清理当前节点的本地缓存
        productLocalCache.invalidate(key);
    }

    /**
     * 仅清理本地缓存（常由微服务集群接收到 MQ 广播后，清理除自身以外其他节点的本地缓存）
     *
     * @param key 缓存的 Key
     */
    public void evictLocal(String key) {
        productLocalCache.invalidate(key);
    }
}
