package com.ningxiang.shop.search.listener;

import cn.throwx.canal.gule.model.CanalBinLogEvent;
import cn.throwx.canal.gule.model.CanalBinLogResult;
import cn.throwx.canal.gule.support.processor.BaseCanalBinlogEventProcessor;
import cn.throwx.canal.gule.support.processor.ExceptionHandler;
import com.ningxiang.shop.api.order.bo.EsOrderBO;
import com.ningxiang.shop.api.order.feign.OrderFeignClient;
import com.ningxiang.shop.common.exception.NingxiangException;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.util.Json;
import com.ningxiang.shop.search.bo.OrderBO;
import com.ningxiang.shop.search.constant.EsIndexEnum;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 订单的监听
 * @author FrozenWatermelon
 * @date 2021/02/03
 */
@Component
public class OrderCanalListener extends BaseCanalBinlogEventProcessor<OrderBO> {

    private static final Logger log = LoggerFactory.getLogger(OrderCanalListener.class);

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 插入订单，此时插入es
     */
    @Override
    protected void processInsertInternal(CanalBinLogResult<OrderBO> result) {
        Long orderId = result.getPrimaryKey();

        ServerResponseEntity<EsOrderBO> esOrderResponse = orderFeignClient.getEsOrder(orderId);
        IndexRequest request = new IndexRequest(EsIndexEnum.ORDER.value());
        request.id(String.valueOf(orderId));
        request.source(Json.toJsonString(esOrderResponse.getData()), XContentType.JSON);
        try {
            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.info(indexResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
            throw new NingxiangException("保存es信息异常", e);
        }
    }

    /**
     * 更新订单，删除订单索引，再重新构建一个
     */
    @Override
    protected void processUpdateInternal(CanalBinLogResult<OrderBO> result) {
        Long orderId = result.getPrimaryKey();
        ServerResponseEntity<EsOrderBO> esOrderResponse = orderFeignClient.getEsOrder(orderId);
        UpdateRequest request = new UpdateRequest(EsIndexEnum.ORDER.value(), String.valueOf(orderId));
        request.doc(Json.toJsonString(esOrderResponse.getData()), XContentType.JSON);
        request.docAsUpsert(true);
        try {
            UpdateResponse updateResponse = restHighLevelClient.update(request, RequestOptions.DEFAULT);
            log.info(updateResponse.toString());
        } catch (IOException e) {
            log.error(e.toString());
            throw new NingxiangException("更新订单es信息异常",e);
        }
    }

    @Override
    protected ExceptionHandler exceptionHandler() {
        return (CanalBinLogEvent event, Throwable throwable) -> {
            throw new NingxiangException("创建索引异常",throwable);
        };
    }
}
