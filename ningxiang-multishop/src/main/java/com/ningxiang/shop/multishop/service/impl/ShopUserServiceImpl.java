package com.ningxiang.shop.multishop.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ningxiang.shop.api.auth.bo.UserInfoInTokenBO;
import com.ningxiang.shop.api.auth.feign.AccountFeignClient;
import com.ningxiang.shop.api.leaf.feign.SegmentFeignClient;
import com.ningxiang.shop.api.rbac.dto.UserRoleDTO;
import com.ningxiang.shop.api.rbac.feign.UserRoleFeignClient;
import com.ningxiang.shop.common.database.dto.PageDTO;
import com.ningxiang.shop.common.database.util.PageUtil;
import com.ningxiang.shop.common.database.vo.PageVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import com.ningxiang.shop.multishop.mapper.ShopUserMapper;
import com.ningxiang.shop.multishop.model.ShopUser;
import com.ningxiang.shop.multishop.service.ShopUserService;
import com.ningxiang.shop.multishop.vo.ShopUserVO;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author FrozenWatermelon
 * @date 2020/09/03
 */
@Service
public class ShopUserServiceImpl implements ShopUserService {

	@Resource
	private ShopUserMapper shopUserMapper;
	@Autowired
	private AccountFeignClient accountFeignClient;
	@Autowired
	private UserRoleFeignClient userRoleFeignClient;
	@Autowired
	private SegmentFeignClient segmentFeignClient;

	@Override
	public PageVO<ShopUserVO> pageByShopId(PageDTO pageDTO, Long shopId, String nickName) {
		return PageUtil.doPage(pageDTO, () -> shopUserMapper.listByShopId(shopId, nickName));
	}

	@Override
	public ShopUserVO getByUserId(Long userId) {
		ShopUserVO shopUser = shopUserMapper.getByUserId(userId);
		ServerResponseEntity<List<Long>> roleIds = userRoleFeignClient.getRoleIds(shopUser.getShopUserId());
		shopUser.setRoleIds(roleIds.getData());
		return shopUser;
	}

    @Override
	@Transactional(rollbackFor = Exception.class)
    public void save(ShopUser shopUser, List<Long> roleIds) {
		shopUserMapper.save(shopUser);
		if (CollUtil.isEmpty(roleIds)) {
			return;
		}
		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setRoleIds(roleIds);
		userRoleDTO.setUserId(shopUser.getShopUserId());
		userRoleFeignClient.saveByUserIdAndSysType(userRoleDTO);
	}

	@Override
	@GlobalTransactional(rollbackFor = Exception.class)
	@Transactional(rollbackFor = Exception.class)
	public void update(ShopUser shopUser, List<Long> roleIds) {
		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setRoleIds(roleIds);
		userRoleDTO.setUserId(shopUser.getShopUserId());
		shopUserMapper.update(shopUser);
		userRoleFeignClient.updateByUserIdAndSysType(userRoleDTO);
	}

	@Override
	@GlobalTransactional(rollbackFor = Exception.class)
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(Long shopUserId) {
		UserInfoInTokenBO userInfoInTokenBO = AuthUserContext.get();
		accountFeignClient.deleteByUserIdAndSysType(shopUserId);
		userRoleFeignClient.deleteByUserIdAndSysType(shopUserId);
		shopUserMapper.deleteById(shopUserId);
	}

    @Override
    public Long getUserIdByShopId(Long shopId) {
        return shopUserMapper.getUserIdByShopId(shopId);
    }

}
