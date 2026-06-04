package com.ningxiang.shop.user.feign;

import com.ningxiang.shop.api.user.feign.UserAddrFeignClient;
import com.ningxiang.shop.common.order.vo.UserAddrVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import com.ningxiang.shop.user.service.UserAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户地址feign连接
 * @author FrozenWatermelon
 * @date 2020/12/07
 */
@RestController
public class UserAddrFeignController implements UserAddrFeignClient {

    @Autowired
    private UserAddrService userAddrService;

    @Override
    public ServerResponseEntity<UserAddrVO> getUserAddrByAddrId(Long addrId) {
        return ServerResponseEntity.success(userAddrService.getUserAddrByUserId(addrId,AuthUserContext.get().getUserId()));
    }
}
