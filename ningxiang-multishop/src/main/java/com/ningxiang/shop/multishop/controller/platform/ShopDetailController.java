package com.ningxiang.shop.multishop.controller.platform;

import com.ningxiang.shop.api.multishop.vo.ShopDetailVO;
import com.ningxiang.shop.common.constant.Constant;
import com.ningxiang.shop.common.database.dto.PageDTO;
import com.ningxiang.shop.common.database.vo.PageVO;
import com.ningxiang.shop.common.exception.NingxiangException;
import com.ningxiang.shop.common.response.ResponseEnum;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import com.ningxiang.shop.multishop.dto.ShopDetailDTO;
import com.ningxiang.shop.multishop.model.ShopDetail;
import com.ningxiang.shop.multishop.service.ShopDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.ningxiang.shop.common.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 店铺详情
 *
 * @author FrozenWatermelon
 * @date 2020-12-05 15:50:25
 */
@RestController("platformShopDetailController")
@RequestMapping("/platform/shop_detail")
@Tag(name = "platform-店铺信息")
public class ShopDetailController {

    @Autowired
    private ShopDetailService shopDetailService;



    @GetMapping("/page")
    @Operation(summary = "分页查询" , description = "分页查询")
    public ServerResponseEntity<PageVO<ShopDetailVO>> getShopAuditingPage(PageDTO pageDTO, ShopDetailDTO shopDetailDTO) {
        if (!Objects.equals(Constant.PLATFORM_SHOP_ID, AuthUserContext.get().getTenantId())) {
            throw new NingxiangException(ResponseEnum.UNAUTHORIZED);
        }
        return ServerResponseEntity.success(shopDetailService.page(pageDTO, shopDetailDTO));
    }

    @GetMapping("/info")
    @Operation(summary = "店铺详情" , description = "店铺详情")
    public ServerResponseEntity<ShopDetailVO> getInfo(@RequestParam Long shopId) {
        ShopDetailVO shopDetailVO = shopDetailService.getByShopId(shopId);
        return ServerResponseEntity.success(shopDetailVO);
    }

    /**
     * 新建店铺
     */
    @PostMapping("/create_shop")
    @Operation(summary = "新建店铺" , description = "新建店铺")
    public ServerResponseEntity<Void> createShop(@RequestBody ShopDetailDTO shopDetailDTO) {
        shopDetailService.createShop(shopDetailDTO);
        return ServerResponseEntity.success();
    }

    @PutMapping("/update_shop")
    @Operation(summary = "更新店铺" , description = "更新店铺")
    public ServerResponseEntity<Void> updateShop(@RequestBody ShopDetailDTO shopDetailDTO) {
        shopDetailService.update(BeanUtil.map(shopDetailDTO, ShopDetail.class));
        return ServerResponseEntity.success();
    }
}
