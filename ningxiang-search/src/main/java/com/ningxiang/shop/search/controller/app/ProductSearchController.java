package com.ningxiang.shop.search.controller.app;

import cn.hutool.core.collection.CollUtil;
import com.ningxiang.shop.api.multishop.bo.EsShopDetailBO;
import com.ningxiang.shop.api.multishop.feign.ShopDetailFeignClient;
import com.ningxiang.shop.api.vo.search.ShopInfoSearchVO;
import com.ningxiang.shop.common.constant.StatusEnum;
import com.ningxiang.shop.common.response.ResponseEnum;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.api.dto.EsPageDTO;
import com.ningxiang.shop.api.dto.ProductSearchDTO;
import com.ningxiang.shop.search.manager.ProductSearchManager;
import com.ningxiang.shop.api.vo.EsPageVO;
import com.ningxiang.shop.api.vo.search.ProductSearchVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * 商品搜索
 * @author FrozenWatermelon
 * @date 2020/11/16
 */
@RestController("appSearchSpuController")
@RequestMapping("/ua/search")
@Tag(name = "app-spu搜索接口")
public class ProductSearchController {

    @Autowired
    private ProductSearchManager productSearchManager;

    @Autowired
    private ShopDetailFeignClient shopDetailFeignClient;

    @GetMapping("/page")
    @Operation(summary = "商品信息列表-包含spu、品牌、分类、属性和店铺信息" , description = "spu列表-包含spu、品牌、分类、属性和店铺信息")
    public ServerResponseEntity<EsPageVO<ProductSearchVO>> page(@Valid EsPageDTO pageDTO, ProductSearchDTO productSearchDTO) {
        productSearchDTO.setSpuStatus(StatusEnum.ENABLE.value());
        EsPageVO<ProductSearchVO> searchPage = productSearchManager.page(pageDTO, productSearchDTO);
        loadShopData(searchPage.getList());
        return ServerResponseEntity.success(searchPage);
    }

    @GetMapping("/simple_page")
    @Operation(summary = "商品信息列表-包含spu信息" , description = "商品信息列表-包含spu信息")
    public ServerResponseEntity<EsPageVO<ProductSearchVO>> simplePage(@Valid EsPageDTO pageDTO, ProductSearchDTO productSearchDTO) {
        productSearchDTO.setSpuStatus(StatusEnum.ENABLE.value());
        EsPageVO<ProductSearchVO> searchPage =  productSearchManager.simplePage(pageDTO, productSearchDTO);
        loadShopData(searchPage.getList());
        return ServerResponseEntity.success(searchPage);
    }

    /**
     * 获取店铺数据
     * @param list
     */
    private void loadShopData(List<ProductSearchVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (ProductSearchVO productSearchVO : list) {
            if (Objects.isNull(productSearchVO.getShopInfo()) || Objects.isNull(productSearchVO.getShopInfo().getShopId())) {
                continue;
            }
            ServerResponseEntity<EsShopDetailBO> shopDataResponse = shopDetailFeignClient.shopExtensionData(productSearchVO.getShopInfo().getShopId());
            if (Objects.equals(shopDataResponse.getCode(), ResponseEnum.OK.value())) {
                EsShopDetailBO esShopDetailBO = shopDataResponse.getData();
                ShopInfoSearchVO shopInfo = productSearchVO.getShopInfo();
                shopInfo.setShopLogo(esShopDetailBO.getShopLogo());
                shopInfo.setShopName(esShopDetailBO.getShopName());
                shopInfo.setType(esShopDetailBO.getType());
            }
        }
    }
}
