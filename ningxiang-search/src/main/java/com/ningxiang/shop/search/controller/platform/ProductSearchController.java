package com.ningxiang.shop.search.controller.platform;

import com.ningxiang.shop.api.dto.EsPageDTO;
import com.ningxiang.shop.api.dto.ProductSearchDTO;
import com.ningxiang.shop.api.vo.EsPageVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
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
 * @Date 2021/6/21 13:48
 */
@RestController("platformSearchSpuController")
@RequestMapping("/p/search")
@Tag(name = "platform-spu列表接口")
public class ProductSearchController {

    @Autowired
    private ProductSearchManager productSearchManager;

    @GetMapping("/page")
    @Operation(summary = "商品管理信息列表（平台端）" , description = "商品管理信息列表（平台端）")
    public ServerResponseEntity<EsPageVO<SpuAdminVO>> adminPage(@Valid EsPageDTO pageDTO, ProductSearchDTO productSearchDTO) {
        productSearchDTO.setSearchType(SearchTypeEnum.PLATFORM.value());
        EsPageVO<SpuAdminVO> searchPage =  productSearchManager.adminPage(pageDTO, productSearchDTO);
        return ServerResponseEntity.success(searchPage);
    }
}
