package com.ningxiang.shop.product.listener;

import com.ningxiang.shop.product.config.MultilevelCache;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * 宁享购缓存同步监听器 (RocketMQ 广播模式)
 * 接收到广播通知后，同步清理当前节点的本地 JVM (Caffeine) 缓存，以保证集群缓存一致性。
 *
 * @author Ningxiang
 */
@Component
@RocketMQMessageListener(
        topic = "PRODUCT_CACHE_SYNC_TOPIC",
        consumerGroup = "CID_PRODUCT_CACHE_SYNC",
        messageModel = MessageModel.BROADCASTING // 采用广播模式，集群下每个商品微服务实例节点都会收到此通知
)
public class ProductCacheSyncListener implements RocketMQListener<String> {

    private static final Logger logger = LoggerFactory.getLogger(ProductCacheSyncListener.class);

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void onMessage(String msg) {
        logger.info("收到清除本地 Caffeine 缓存广播消息: {}", msg);
        try {
            // 消息体格式为 "cacheName:key"
            String[] parts = msg.split(":", 2);
            if (parts.length == 2) {
                String cacheName = parts[0];
                String cacheKey = parts[1];

                Cache cache = cacheManager.getCache(cacheName);
                if (cache instanceof MultilevelCache) {
                    // 执行本地缓存的精准驱逐
                    ((MultilevelCache) cache).clearLocal(cacheKey);
                }
            }
        } catch (Exception e) {
            logger.error("清理本地 Caffeine 缓存失败, 原始消息: " + msg, e);
        }
    }
}
