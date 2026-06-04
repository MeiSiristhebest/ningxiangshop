package com.ningxiang.shop.api.multishop.feign;

import com.ningxiang.shop.common.feign.FeignInsideAuthConfig;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author lth
 * @Date 2021/7/8 11:10
 */
@FeignClient(value = "ningxiang-multishop",contextId = "indexImg")
public interface IndexImgFeignClient {

    /**
     * 根据商品d删除轮播图信息
     * @param spuId 商品id
     * @param shopId 店鋪id
     * @return void
     */
    @GetMapping(value = FeignInsideAuthConfig.FEIGN_INSIDE_URL_PREFIX + "/insider/indexImg/deleteBySpuId")
    ServerResponseEntity<Void> deleteBySpuId(@RequestParam("spuId") Long spuId, @RequestParam("shopId") Long shopId);
}
