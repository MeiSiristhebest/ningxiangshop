package com.ningxiang.shop.product.config;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 宁享购多级缓存自动化配置中心
 * 开启缓存支持，并注入自定义的二级缓存管理器
 *
 * @author Ningxiang
 */
@Configuration
@EnableCaching // 开启基于注解的 Spring Cache 支持
public class MultilevelCacheConfig {

    /**
     * 注册多级缓存管理器，并标记为优先使用
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate, RocketMQTemplate rocketMQTemplate) {
        return new MultilevelCacheManager(redisTemplate, rocketMQTemplate);
    }
}
