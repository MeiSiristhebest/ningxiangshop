package com.ningxiang.shop.api.product.feign;

import com.ningxiang.shop.api.product.vo.SkuVO;
import com.ningxiang.shop.common.feign.FeignInsideAuthConfig;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author FrozenWatermelon
 * @date 2020/11/12
 */
@FeignClient(value = "ningxiang-product",contextId = "sku")
public interface SkuFeignClient {

    /**
     * 通过skuId获取sku信息
     * @param skuId skuId
     * @return sku信息
     */
    @GetMapping(value = FeignInsideAuthConfig.FEIGN_INSIDE_URL_PREFIX + "/sku/getById")
    ServerResponseEntity<SkuVO> getById(@RequestParam("skuId") Long skuId);

}
