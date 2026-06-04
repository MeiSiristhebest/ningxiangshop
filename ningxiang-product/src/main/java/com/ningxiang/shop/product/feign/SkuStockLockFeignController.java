package com.ningxiang.shop.product.feign;

import com.ningxiang.shop.api.product.dto.SkuStockLockDTO;
import com.ningxiang.shop.api.product.feign.SkuStockLockFeignClient;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.product.service.*;
import com.ningxiang.shop.product.service.SkuStockLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author FrozenWatermelon
 * @date 2020/12/8
 */
@RestController
public class SkuStockLockFeignController implements SkuStockLockFeignClient {


    @Autowired
    private SkuStockLockService skuStockLockService;

    @Override
    public ServerResponseEntity<Void> lock(List<SkuStockLockDTO> skuStockLocksParam) {
        return skuStockLockService.lock(skuStockLocksParam);
    }
}
