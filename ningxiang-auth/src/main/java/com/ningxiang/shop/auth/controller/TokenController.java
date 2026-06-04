package com.ningxiang.shop.auth.controller;

import com.ningxiang.shop.common.security.bo.TokenInfoBO;
import com.ningxiang.shop.auth.dto.RefreshTokenDTO;
import com.ningxiang.shop.auth.manager.TokenStore;
import com.ningxiang.shop.api.auth.vo.TokenInfoVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.ningxiang.shop.common.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * @author FrozenWatermelon
 * @date 2020/6/30
 */
@RestController
@Tag(name = "token")
public class TokenController {

	@Autowired
	private TokenStore tokenStore;


	@PostMapping("/ua/token/refresh")
	public ServerResponseEntity<TokenInfoVO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
		return tokenStore.refreshToken(refreshTokenDTO.getRefreshToken());
	}

}
