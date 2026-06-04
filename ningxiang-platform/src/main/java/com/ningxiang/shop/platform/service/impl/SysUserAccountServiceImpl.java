package com.ningxiang.shop.platform.service.impl;

import com.ningxiang.shop.api.auth.dto.AuthAccountDTO;
import com.ningxiang.shop.api.auth.bo.UserInfoInTokenBO;
import com.ningxiang.shop.api.auth.feign.AccountFeignClient;
import com.ningxiang.shop.api.auth.vo.AuthAccountVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import com.ningxiang.shop.common.util.IpHelper;
import com.ningxiang.shop.platform.dto.ChangeAccountDTO;
import com.ningxiang.shop.platform.mapper.SysUserMapper;
import com.ningxiang.shop.platform.model.SysUser;
import com.ningxiang.shop.platform.service.SysUserAccountService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * @author lhd
 * @date 2020/12/22
 */
@Service
public class SysUserAccountServiceImpl implements SysUserAccountService {

	@Resource
	private SysUserMapper sysUserMapper;
	@Autowired
	private AccountFeignClient accountFeignClient;

    @Override
	@GlobalTransactional(rollbackFor = Exception.class)
	@Transactional(rollbackFor = Exception.class)
    public ServerResponseEntity<Void> save(ChangeAccountDTO changeAccountDTO) {
		AuthAccountDTO authAccountDTO = getAuthAccountDTO(changeAccountDTO);
		authAccountDTO.setCreateIp(IpHelper.getIpAddr());
		authAccountDTO.setIsAdmin(0);
		// 保存
		ServerResponseEntity<Long> serverResponseEntity = accountFeignClient.save(authAccountDTO);
		if (!serverResponseEntity.isSuccess()) {
			return ServerResponseEntity.transform(serverResponseEntity);
		}
		SysUser sysUser = new SysUser();
		sysUser.setSysUserId(changeAccountDTO.getUserId());
		sysUser.setHasAccount(1);
		sysUserMapper.update(sysUser);
		return ServerResponseEntity.success();
    }

	@Override
	public ServerResponseEntity<Void> update(ChangeAccountDTO changeAccountDTO) {

		AuthAccountDTO authAccountDTO = getAuthAccountDTO(changeAccountDTO);
		// 更新，不涉及分布式事务
		ServerResponseEntity<Void> serverResponseEntity = accountFeignClient.update(authAccountDTO);
		if (!serverResponseEntity.isSuccess()) {
			return serverResponseEntity;
		}

		return ServerResponseEntity.success();
	}

	@Override
	public ServerResponseEntity<AuthAccountVO> getByUserIdAndSysType(Long userId, Integer sysType) {
		return accountFeignClient.getByUserIdAndSysType(userId,sysType);
	}

	private AuthAccountDTO getAuthAccountDTO(ChangeAccountDTO changeAccountDTO) {
		AuthAccountDTO authAccountDTO = new AuthAccountDTO();
		UserInfoInTokenBO userInfoInTokenBO = AuthUserContext.get();
		authAccountDTO.setPassword(changeAccountDTO.getPassword());
		authAccountDTO.setUsername(changeAccountDTO.getUsername());
		authAccountDTO.setStatus(changeAccountDTO.getStatus());
		authAccountDTO.setSysType(userInfoInTokenBO.getSysType());
		authAccountDTO.setTenantId(userInfoInTokenBO.getTenantId());
		authAccountDTO.setUserId(changeAccountDTO.getUserId());
		return authAccountDTO;
	}

}
