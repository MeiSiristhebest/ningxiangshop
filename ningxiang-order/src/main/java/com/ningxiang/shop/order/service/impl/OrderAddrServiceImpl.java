package com.ningxiang.shop.order.service.impl;

import com.ningxiang.shop.common.database.dto.PageDTO;
import com.ningxiang.shop.common.database.util.PageUtil;
import com.ningxiang.shop.common.database.vo.PageVO;
import com.ningxiang.shop.order.mapper.OrderAddrMapper;
import com.ningxiang.shop.order.model.OrderAddr;
import com.ningxiang.shop.order.service.OrderAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户订单配送地址
 *
 * @author FrozenWatermelon
 * @date 2020-12-05 14:13:50
 */
@Service
public class OrderAddrServiceImpl implements OrderAddrService {

    @Autowired
    private OrderAddrMapper orderAddrMapper;

    @Override
    public PageVO<OrderAddr> page(PageDTO pageDTO) {
        return PageUtil.doPage(pageDTO, () -> orderAddrMapper.list());
    }

    @Override
    public OrderAddr getByOrderAddrId(Long orderAddrId) {
        return orderAddrMapper.getByOrderAddrId(orderAddrId);
    }

    @Override
    public void save(OrderAddr orderAddr) {
        orderAddrMapper.save(orderAddr);
    }

    @Override
    public void update(OrderAddr orderAddr) {
        orderAddrMapper.update(orderAddr);
    }

    @Override
    public void deleteById(Long orderAddrId) {
        orderAddrMapper.deleteById(orderAddrId);
    }
}
