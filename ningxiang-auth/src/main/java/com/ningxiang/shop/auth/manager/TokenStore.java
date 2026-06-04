package com.ningxiang.shop.auth.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.ningxiang.shop.api.auth.bo.UserInfoInTokenBO;
import com.ningxiang.shop.api.auth.vo.TokenInfoVO;
import com.ningxiang.shop.common.exception.NingxiangException;
import com.ningxiang.shop.common.response.ResponseEnum;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Sa-Token 实现在认证中心的 Token 管理器
 *
 * @author Ningxiang
 */
@Component
public class TokenStore {

	/**
	 * 存储并获取 TokenInfo 视图对象
	 * @param userInfoInToken 用户身份信息
	 * @return TokenInfoVO
	 */
	public TokenInfoVO storeAndGetVo(UserInfoInTokenBO userInfoInToken) {
		// 拼接 Sa-Token 账号体系中的 LoginId
		String loginId = userInfoInToken.getSysType() + ":" + userInfoInToken.getUserId();

		// Sa-Token 框架执行登录
		StpUtil.login(loginId);

		// 将详细用户信息缓存于该账号对应的 Sa-Session 中，方便网关等各模块直接读取
		StpUtil.getSessionByLoginId(loginId).set("userInfo", userInfoInToken);

		TokenInfoVO tokenInfoVO = new TokenInfoVO();
		tokenInfoVO.setAccessToken(StpUtil.getTokenValue());
		tokenInfoVO.setRefreshToken(StpUtil.getTokenValue()); // 微服务推荐单 Token 体系
		tokenInfoVO.setExpiresIn((int) StpUtil.getTokenTimeout());
		return tokenInfoVO;
	}

	/**
	 * 根据 accessToken 获取用户信息
	 * @param accessToken token 字符串
	 * @param needDecrypt 在 Sa-Token 模式下已无解密必要，此处仅作入参结构适配
	 * @return 用户信息 ServerResponseEntity
	 */
	public ServerResponseEntity<UserInfoInTokenBO> getUserInfoByAccessToken(String accessToken, boolean needDecrypt) {
		Object loginIdObj = StpUtil.getLoginIdByToken(accessToken);
		if (loginIdObj == null) {
			return ServerResponseEntity.showFailMsg("accessToken 已过期");
		}

		String loginId = loginIdObj.toString();
		UserInfoInTokenBO userInfo = (UserInfoInTokenBO) StpUtil.getSessionByLoginId(loginId).get("userInfo");
		if (userInfo == null) {
			return ServerResponseEntity.showFailMsg("accessToken 已过期");
		}

		return ServerResponseEntity.success(userInfo);
	}

	/**
	 * 刷新 Token
	 * @param refreshToken 刷新 token
	 * @return TokenInfoVO
	 */
	public ServerResponseEntity<TokenInfoVO> refreshToken(String refreshToken) {
		Object loginIdObj = StpUtil.getLoginIdByToken(refreshToken);
		if (loginIdObj == null) {
			return ServerResponseEntity.showFailMsg("refreshToken 已过期");
		}

		// 执行 Token 活动期续命
		StpUtil.updateLastActiveToNow();

		String loginId = loginIdObj.toString();
		UserInfoInTokenBO userInfo = (UserInfoInTokenBO) StpUtil.getSessionByLoginId(loginId).get("userInfo");

		TokenInfoVO tokenInfoVO = new TokenInfoVO();
		tokenInfoVO.setAccessToken(refreshToken);
		tokenInfoVO.setRefreshToken(refreshToken);
		tokenInfoVO.setExpiresIn((int) StpUtil.getTokenTimeout());

		return ServerResponseEntity.success(tokenInfoVO);
	}

	/**
	 * 注销该用户在对应系统下的全部 Token 会话
	 * @param appId 账号系统类别 (如 sysType)
	 * @param uid 统一用户ID
	 */
	public void deleteAllToken(String appId, Long uid) {
		String loginId = appId + ":" + uid;
		// 踢此用户下线，注销全部设备 Token
		StpUtil.logout(loginId);
	}

	/**
	 * 更新用户信息缓存
	 */
	public void updateUserInfoByUidAndAppId(Long uid, String appId, UserInfoInTokenBO userInfoInTokenBO) {
		if (userInfoInTokenBO == null) {
			return;
		}
		String loginId = appId + ":" + uid;
		if (!StpUtil.isLogin(loginId)) {
			throw new NingxiangException(ResponseEnum.UNAUTHORIZED);
		}
		// 重写覆盖缓存中的用户信息
		StpUtil.getSessionByLoginId(loginId).set("userInfo", userInfoInTokenBO);
	}
}
