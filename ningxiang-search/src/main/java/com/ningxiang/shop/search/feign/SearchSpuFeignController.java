package com.ningxiang.shop.search.feign;


import cn.hutool.core.collection.CollUtil;
import com.ningxiang.shop.api.dto.EsPageDTO;
import com.ningxiang.shop.api.dto.ProductSearchDTO;
import com.ningxiang.shop.api.feign.SearchSpuFeignClient;
import com.ningxiang.shop.api.vo.EsPageVO;
import com.ningxiang.shop.api.vo.search.ProductSearchVO;
import com.ningxiang.shop.api.vo.search.SpuSearchVO;
import com.ningxiang.shop.common.constant.Constant;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.search.manager.ProductSearchManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 商品搜索feign连接
 * @author YXF
 * @date 2020/12/07
 */
@RestController
public class SearchSpuFeignController implements SearchSpuFeignClient {

    @Autowired
    private ProductSearchManager productSearchManager;

    @Override
    public ServerResponseEntity<EsPageVO<ProductSearchVO>> search(EsPageDTO pageDTO, ProductSearchDTO productSearchDTO) {
        return ServerResponseEntity.success(productSearchManager.simplePage(pageDTO, productSearchDTO));
    }

    @Override
    public ServerResponseEntity<List<SpuSearchVO>> getSpusBySpuIds(List<Long> spuIds){
        if (CollUtil.isEmpty(spuIds)) {
            return ServerResponseEntity.success(new ArrayList<>());
        }
        ProductSearchDTO productSearchDTO = new ProductSearchDTO();
        productSearchDTO.setSpuIds(spuIds);
        List<SpuSearchVO> list = productSearchManager.list(productSearchDTO);
        return ServerResponseEntity.success(list);
    }

    @Override
    public ServerResponseEntity<EsPageVO<ProductSearchVO>> spuPage(Integer pageNum, Integer pageSize, Long shopId) {
        EsPageDTO pageDTO = new EsPageDTO();
        pageDTO.setPageNum(pageNum);
        pageDTO.setPageSize(pageSize);
        ProductSearchDTO productSearchDTO = new ProductSearchDTO();
        // 平台id则搜索整个平台的商品
        if (!Objects.equals(shopId, Constant.PLATFORM_SHOP_ID)) {
            productSearchDTO.setShopId(shopId);
        }
        EsPageVO<ProductSearchVO> page = productSearchManager.page(pageDTO, productSearchDTO);
        return ServerResponseEntity.success(page);
    }

    @Override
    public ServerResponseEntity<List<SpuSearchVO>> limitSizeListByShopIds(List<Long> shopIds, Integer size) {
        if (CollUtil.isEmpty(shopIds)) {
            return ServerResponseEntity.success(new ArrayList<>());
        }
        List<SpuSearchVO> list = productSearchManager.limitSizeListByShopIds(shopIds, size);
        return ServerResponseEntity.success(list);
    }
}
