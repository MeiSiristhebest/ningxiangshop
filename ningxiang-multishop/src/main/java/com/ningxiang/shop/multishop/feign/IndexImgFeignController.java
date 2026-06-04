package com.ningxiang.shop.multishop.feign;

import com.ningxiang.shop.api.multishop.feign.IndexImgFeignClient;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.multishop.service.IndexImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author lth
 * @Date 2021/7/8 11:12
 */
@RestController
public class IndexImgFeignController implements IndexImgFeignClient {

    @Autowired
    private IndexImgService indexImgService;

    @Override
    public ServerResponseEntity<Void> deleteBySpuId(Long spuId, Long shopId) {
        indexImgService.deleteBySpuId(spuId, shopId);
        return ServerResponseEntity.success();
    }
}
