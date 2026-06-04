package com.ningxiang.shop.api.auth.feign;

import com.ningxiang.shop.api.auth.bo.UserInfoInTokenBO;
import com.ningxiang.shop.common.constant.Auth;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author FrozenWatermelon
 * @date 2020/7/15
 */
@FeignClient(value = "ningxiang-auth",contextId ="token")
public interface TokenFeignClient {

	/**
	 * 校验token并返回token保存的用户信息
	 * @param accessToken accessToken
	 * @return token保存的用户信息
	 */
	@GetMapping(value = Auth.CHECK_TOKEN_URI)
	ServerResponseEntity<UserInfoInTokenBO> checkToken(@RequestParam("accessToken") String accessToken);

}
