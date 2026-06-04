package com.ningxiang.shop.rbac.service.impl;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import com.ningxiang.shop.rbac.mapper.UserRoleMapper;
import com.ningxiang.shop.rbac.service.UserRoleService;

/**
 * @author FrozenWatermelon
 * @date 2020/6/23
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

	@Resource
	private UserRoleMapper userRoleMapper;

}
