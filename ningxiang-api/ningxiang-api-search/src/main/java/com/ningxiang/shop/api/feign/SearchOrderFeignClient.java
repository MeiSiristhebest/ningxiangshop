package com.ningxiang.shop.api.feign;

import com.ningxiang.shop.api.vo.EsPageVO;
import com.ningxiang.shop.api.vo.search.EsOrderVO;
import com.ningxiang.shop.common.dto.OrderSearchDTO;
import com.ningxiang.shop.common.feign.FeignInsideAuthConfig;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 订单搜索
 * @author FrozenWatermelon
 * @date 2021/02/05
 */
@FeignClient(value = "ningxiang-search",contextId = "searchOrder")
public interface SearchOrderFeignClient {


    /**
     * 订单搜索
     * @param orderSearch 订单搜索参数
     * @return 订单列表
     */
    @PutMapping(value = FeignInsideAuthConfig.FEIGN_INSIDE_URL_PREFIX + "/insider/searchOrder/getOrderPage")
    ServerResponseEntity<EsPageVO<EsOrderVO>> getOrderPage(@RequestBody OrderSearchDTO orderSearch);

}
