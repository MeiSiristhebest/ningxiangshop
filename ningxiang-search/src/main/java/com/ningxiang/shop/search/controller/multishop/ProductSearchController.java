package com.ningxiang.shop.search.controller.multishop;

import com.ningxiang.shop.api.dto.EsPageDTO;
import com.ningxiang.shop.api.dto.ProductSearchDTO;
import com.ningxiang.shop.api.vo.EsPageVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import com.ningxiang.shop.search.constant.SearchTypeEnum;
import com.ningxiang.shop.search.manager.ProductSearchManager;
import com.ningxiang.shop.search.vo.SpuAdminVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * @Author lth
 * @Date 2021/6/21 10:36
 */
@RestController("multishopSearchSpuController")
@RequestMapping("/m/search")
@Tag(name = "multishop-spu管理列表接口")
public class ProductSearchController {

    @Autowired
    private ProductSearchManager productSearchManager;

    @GetMapping("/page")
    @Operation(summary = "商品信息列表" , description = "商品信息列表")
    public ServerResponseEntity<EsPageVO<SpuAdminVO>> page(@Valid EsPageDTO pageDTO, ProductSearchDTO productSearchDTO) {
        Long shopId = AuthUserContext.get().getTenantId();
        productSearchDTO.setSearchType(SearchTypeEnum.MULTISHOP.value());
        productSearchDTO.setShopId(shopId);
        EsPageVO<SpuAdminVO> searchPage =  productSearchManager.adminPage(pageDTO, productSearchDTO);
        return ServerResponseEntity.success(searchPage);
    }

}
