package com.ningxiang.shop.multishop.controller.multishop;

import com.ningxiang.shop.api.multishop.vo.ShopDetailVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import com.ningxiang.shop.multishop.service.ShopDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author lth
 * @Date 2021/6/24 14:46
 */
@RequestMapping(value = "/m/shop_detail")
@RestController("multishopShopDetailController")
@Tag(name = "multishop-店铺详情信息")
public class ShopDetailController {

    @Autowired
    private ShopDetailService shopDetailService;

    @GetMapping("/info")
    @Operation(summary = "获取店铺详情信息" , description = "获取店铺详情信息")
    public ServerResponseEntity<ShopDetailVO> info() {
        Long shopId = AuthUserContext.get().getTenantId();
        ShopDetailVO shopDetailVO = shopDetailService.getByShopId(shopId);
        return ServerResponseEntity.success(shopDetailVO);
    }
}
