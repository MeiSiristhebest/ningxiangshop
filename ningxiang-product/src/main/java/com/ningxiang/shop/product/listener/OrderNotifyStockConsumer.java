package com.ningxiang.shop.product.listener;

import com.ningxiang.shop.common.order.bo.PayNotifyBO;
import com.ningxiang.shop.common.rocketmq.config.RocketMqConstant;
import com.ningxiang.shop.common.security.annotation.Idempotent;
import com.ningxiang.shop.product.service.SkuStockLockService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 解锁库存的监听
 * @author FrozenWatermelon
 */
@Component
@RocketMQMessageListener(topic = RocketMqConstant.ORDER_NOTIFY_STOCK_TOPIC,consumerGroup = RocketMqConstant.ORDER_NOTIFY_STOCK_TOPIC)
public class OrderNotifyStockConsumer implements RocketMQListener<PayNotifyBO> {

    @Autowired
    private SkuStockLockService skuStockLockService;

    /**
     * 订单支付成功锁定库存
     */
    @Override
    @Idempotent(key = "'orderNotifyStock:' + #message.orderIds.get(0)", expireTime = 600, message = "订单支付库存通知处理中，请勿重复投递")
    public void onMessage(PayNotifyBO message) {
        skuStockLockService.markerStockUse(message.getOrderIds());
    }
}
