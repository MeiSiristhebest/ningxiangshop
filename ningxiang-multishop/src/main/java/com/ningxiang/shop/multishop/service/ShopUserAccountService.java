package com.ningxiang.shop.multishop.service;

import com.ningxiang.shop.api.auth.vo.AuthAccountVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.multishop.dto.ChangeAccountDTO;

/**
 * @author FrozenWatermelon
 * @date 2020/09/03
 */
public interface ShopUserAccountService {

	/**
	 * 添加账户
	 * @param changeAccountDTO 账户信息
	 * @return void
	 */
	ServerResponseEntity<Void> save(ChangeAccountDTO changeAccountDTO);

	/**
	 * 更新账户
	 * @param changeAccountDTO 账户信息
	 * @return
	 */
	ServerResponseEntity<Void> update(ChangeAccountDTO changeAccountDTO);

	/**
	 * 根据用户id和系统类型获取用户信息
	 * @param userId 用户id
	 * @param sysType 系统类型
	 * @return void
	 */
	ServerResponseEntity<AuthAccountVO> getByUserIdAndSysType(Long userId, Integer sysType);
}
