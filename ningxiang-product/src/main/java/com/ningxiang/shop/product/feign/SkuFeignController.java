package com.ningxiang.shop.product.feign;

import com.ningxiang.shop.api.product.feign.SkuFeignClient;
import com.ningxiang.shop.api.product.vo.SkuVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.product.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author FrozenWatermelon
 * @date 2020/12/8
 */
@RestController
public class SkuFeignController implements SkuFeignClient {

    @Autowired
    private SkuService skuService;


    @Override
    public ServerResponseEntity<SkuVO> getById(Long skuId) {
        return ServerResponseEntity.success(skuService.getSkuBySkuId(skuId));
    }
}
