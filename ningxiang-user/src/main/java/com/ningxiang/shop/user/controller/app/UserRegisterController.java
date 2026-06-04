package com.ningxiang.shop.user.controller.app;


import cn.hutool.core.util.StrUtil;
import com.ningxiang.shop.api.auth.bo.UserInfoInTokenBO;
import com.ningxiang.shop.api.auth.constant.SysTypeEnum;
import com.ningxiang.shop.api.auth.feign.AccountFeignClient;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.api.auth.vo.TokenInfoVO;
import com.ningxiang.shop.user.dto.UserRegisterDTO;
import com.ningxiang.shop.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * 用户信息
 * @author FrozenWatermelon
 */
@RestController
@RequestMapping("/ua/user/register")
@Tag(name = "app-用户注册接口")
public class UserRegisterController {

    @Autowired
    private  UserService userService;
    @Autowired
    private AccountFeignClient accountFeignClient;

    @Operation(summary = "注册")
    @PostMapping
    public ServerResponseEntity<TokenInfoVO> register(@Valid @RequestBody UserRegisterDTO param) {

        if (StrUtil.isBlank(param.getNickName())) {
            param.setNickName(param.getUserName());
        }
        // 1. 保存账户信息
        Long uid = userService.save(param);
        // 2. 登录
        UserInfoInTokenBO userInfoInTokenBO = new UserInfoInTokenBO();
        userInfoInTokenBO.setUid(uid);
        userInfoInTokenBO.setUserId(param.getUserId());
        userInfoInTokenBO.setSysType(SysTypeEnum.ORDINARY.value());
        userInfoInTokenBO.setIsAdmin(0);
        return accountFeignClient.storeTokenAndGetVo(userInfoInTokenBO);
    }

}
