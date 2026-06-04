package com.ningxiang.shop.auth.controller;

import com.ningxiang.shop.api.auth.bo.UserInfoInTokenBO;
import com.ningxiang.shop.api.auth.vo.TokenInfoVO;
import com.ningxiang.shop.auth.dto.UpdatePasswordDTO;
import com.ningxiang.shop.auth.manager.TokenStore;
import com.ningxiang.shop.auth.model.AuthAccount;
import com.ningxiang.shop.auth.service.AuthAccountService;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * @author FrozenWatermelon
 * @date 2021/01/29
 */
@RestController
@Tag(name = "密码")
public class PasswordController {

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private AuthAccountService authAccountService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PutMapping("/update_password")
	@Operation(summary = "更新密码" , description = "更新当前用户的密码, 更新密码之后要退出登录，清理token")
	public ServerResponseEntity<TokenInfoVO> updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) {
		UserInfoInTokenBO userInfoInToken = AuthUserContext.get();
		AuthAccount authAccount = authAccountService.getByUserIdAndType(userInfoInToken.getUserId(), userInfoInToken.getSysType());
		if (!passwordEncoder.matches(updatePasswordDTO.getOldPassword(), authAccount.getPassword())) {
			return ServerResponseEntity.showFailMsg("旧密码不正确");
		}
		authAccountService.updatePassword(userInfoInToken.getUserId(), userInfoInToken.getSysType(), updatePasswordDTO.getNewPassword());
		tokenStore.deleteAllToken(userInfoInToken.getSysType().toString(), userInfoInToken.getUid());
		return ServerResponseEntity.success();
	}


}
