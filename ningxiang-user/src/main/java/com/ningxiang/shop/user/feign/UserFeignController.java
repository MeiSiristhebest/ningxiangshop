package com.ningxiang.shop.user.feign;

import com.ningxiang.shop.api.user.feign.UserFeignClient;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.user.service.UserService;
import com.ningxiang.shop.api.user.vo.UserApiVO;
import com.ningxiang.shop.common.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户地址feign连接
 * @author FrozenWatermelon
 * @date 2020/12/07
 */
@RestController
public class UserFeignController implements UserFeignClient {

    @Autowired
    private UserService userService;


    @Override
    public ServerResponseEntity<List<UserApiVO>> getUserByUserIds(List<Long> userIds) {
        List<UserApiVO> userList = userService.getUserByUserIds(userIds);
        return ServerResponseEntity.success(userList);
    }

    @Override
    public ServerResponseEntity<UserApiVO> getUserData(Long userId) {
        UserApiVO user = userService.getByUserId(userId);
        return ServerResponseEntity.success(user);
    }

}
