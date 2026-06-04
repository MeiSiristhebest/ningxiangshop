package com.ningxiang.shop.product.controller.app;

import com.ningxiang.shop.product.vo.app.*;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.product.model.SpuExtension;
import com.ningxiang.shop.product.service.SkuService;
import com.ningxiang.shop.product.service.SpuService;
import com.ningxiang.shop.api.product.vo.SpuVO;
import com.ningxiang.shop.product.vo.app.SkuAppVO;
import com.ningxiang.shop.product.vo.app.SpuAppVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import com.ningxiang.shop.common.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * spu信息
 *
 * @author FrozenWatermelon
 * @date 2020-10-28 15:27:24
 */
@RestController("appSpuController")
@RequestMapping("/ua/spu")
@Tag(name = "app-spu信息")
public class SpuController {

    @Autowired
    private SpuService spuService;

    @Autowired
    private SkuService skuService;



    @GetMapping("/prod_info")
    @Operation(summary = "商品详情信息" , description = "根据商品ID（prodId）获取商品信息")
    @Parameter(name = "spuId", description = "商品ID" , required = true)
    public ServerResponseEntity<SpuAppVO> prodInfo(@RequestParam("spuId") Long spuId) {

        SpuVO spu = spuService.getBySpuId(spuId);
        SpuAppVO spuAppVO = BeanUtil.map(spu, SpuAppVO.class);
        SpuExtension spuExtension = spuService.getSpuExtension(spuId);
        spuAppVO.setTotalStock(spuExtension.getActualStock());
        spuAppVO.setSaleNum(spuExtension.getSaleNum());
        List<SkuAppVO> skuAppVO = skuService.getSkuBySpuId(spu.getSpuId());
        spuAppVO.setSkus(skuAppVO);
        return ServerResponseEntity.success(spuAppVO);
    }
}
