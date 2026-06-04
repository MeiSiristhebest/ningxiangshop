package com.ningxiang.shop.search.feign;


import com.ningxiang.shop.api.dto.EsPageDTO;
import com.ningxiang.shop.api.feign.SearchOrderFeignClient;
import com.ningxiang.shop.api.vo.EsPageVO;
import com.ningxiang.shop.api.vo.search.EsOrderVO;
import com.ningxiang.shop.common.dto.OrderSearchDTO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.search.manager.OrderSearchManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品搜索feign连接
 * @author YXF
 * @date 2020/12/07
 */
@RestController
public class SearchOrderFeignController implements SearchOrderFeignClient {

    @Autowired
    private OrderSearchManager orderSearchManager;


    @Override
    public ServerResponseEntity<EsPageVO<EsOrderVO>> getOrderPage(OrderSearchDTO orderSearch) {
        EsPageDTO pageDTO = new EsPageDTO();
        pageDTO.setPageNum(orderSearch.getPageNum());
        pageDTO.setPageSize(orderSearch.getPageSize());
        return ServerResponseEntity.success(orderSearchManager.pageSearchResult(pageDTO, orderSearch));
    }
}
