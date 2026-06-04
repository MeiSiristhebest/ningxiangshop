package com.ningxiang.shop.platform.service.impl;

import com.ningxiang.shop.platform.model.SysUser;
import com.ningxiang.shop.platform.vo.SysUserVO;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import com.ningxiang.shop.api.auth.feign.AccountFeignClient;
import com.ningxiang.shop.api.rbac.dto.UserRoleDTO;
import com.ningxiang.shop.api.rbac.feign.UserRoleFeignClient;
import com.ningxiang.shop.common.cache.constant.CacheNames;
import com.ningxiang.shop.common.database.dto.PageDTO;
import com.ningxiang.shop.common.database.util.PageUtil;
import com.ningxiang.shop.common.database.vo.PageVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.platform.mapper.SysUserMapper;
import com.ningxiang.shop.platform.service.SysUserService;
import com.ningxiang.shop.platform.vo.SysUserSimpleVO;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author lhd
 * @date 2020/12/22
 */
@Service
public class SysUserServiceImpl implements SysUserService {

	@Resource
	private SysUserMapper sysUserMapper;
	@Autowired
	private AccountFeignClient accountFeignClient;
	@Autowired
	private UserRoleFeignClient userRoleFeignClient;



	@Override
	@Cacheable(cacheNames = CacheNames.PLATFORM_SIMPLE_INFO_KEY, key = "#userId")
	public SysUserSimpleVO getSimpleByUserId(Long userId) {
		return sysUserMapper.getSimpleByUserId(userId);
	}

	@Override
	public PageVO<SysUserVO> pageByShopId(PageDTO pageDTO, Long shopId, String nickName) {
		return PageUtil.doPage(pageDTO, () -> sysUserMapper.listByShopId(shopId, nickName));
	}

	@Override
	public SysUserVO getByUserId(Long userId) {
		SysUserVO sysUser = sysUserMapper.getByUserId(userId);
		ServerResponseEntity<List<Long>> roleIds = userRoleFeignClient.getRoleIds(sysUser.getSysUserId());
		sysUser.setRoleIds(roleIds.getData());
		return sysUser;
	}

    @Override
	@GlobalTransactional(rollbackFor = Exception.class)
	@Transactional(rollbackFor = Exception.class)
    public void save(SysUser sysUser, List<Long> roleIds) {
		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setRoleIds(roleIds);
		sysUserMapper.save(sysUser);
		userRoleDTO.setUserId(sysUser.getSysUserId());
		userRoleFeignClient.saveByUserIdAndSysType(userRoleDTO);
	}

	@Override
	@GlobalTransactional(rollbackFor = Exception.class)
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(cacheNames = CacheNames.PLATFORM_SIMPLE_INFO_KEY, key = "#sysUser.sysUserId")
	public void update(SysUser sysUser, List<Long> roleIds) {
		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setRoleIds(roleIds);
		userRoleDTO.setUserId(sysUser.getSysUserId());
		sysUserMapper.update(sysUser);
		userRoleFeignClient.updateByUserIdAndSysType(userRoleDTO);
	}

	@Override
	@GlobalTransactional(rollbackFor = Exception.class)
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(cacheNames = CacheNames.PLATFORM_SIMPLE_INFO_KEY, key = "#sysUserId")
	public void deleteById(Long sysUserId) {
		accountFeignClient.deleteByUserIdAndSysType(sysUserId);
		userRoleFeignClient.deleteByUserIdAndSysType(sysUserId);
		sysUserMapper.deleteById(sysUserId);
	}

}
