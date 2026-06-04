package com.ningxiang.shop.platform.controller;

import com.ningxiang.shop.api.auth.bo.UserInfoInTokenBO;
import com.ningxiang.shop.common.database.dto.PageDTO;
import com.ningxiang.shop.common.database.vo.PageVO;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import com.ningxiang.shop.platform.dto.SysUserDTO;
import com.ningxiang.shop.platform.model.SysUser;
import com.ningxiang.shop.platform.service.SysUserService;
import com.ningxiang.shop.platform.vo.SysUserVO;
import com.ningxiang.shop.platform.vo.SysUserSimpleVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.ningxiang.shop.common.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * @author lhd
 * @date 2020/12/21
 */
@RequestMapping(value = "/sys_user")
@RestController
@Tag(name = "平台用户信息")
public class SysUserController {

	@Autowired
	private SysUserService sysUserService;


	@GetMapping("/info")
	@Operation(summary = "登陆平台用户信息" , description = "获取当前登陆平台用户的用户信息")
	public ServerResponseEntity<SysUserSimpleVO> info() {
		UserInfoInTokenBO userInfoInTokenBO = AuthUserContext.get();
		SysUserSimpleVO sysUserSimple = sysUserService.getSimpleByUserId(userInfoInTokenBO.getUserId());
		sysUserSimple.setIsAdmin(userInfoInTokenBO.getIsAdmin());
		return ServerResponseEntity.success(sysUserSimple);
	}

	@GetMapping("/page")
	@Operation(summary = "平台用户列表" , description = "获取平台用户列表")
	public ServerResponseEntity<PageVO<SysUserVO>> page(@Valid PageDTO pageDTO, String nickName) {
		UserInfoInTokenBO userInfoInTokenBO = AuthUserContext.get();
		PageVO<SysUserVO> sysUserPage = sysUserService.pageByShopId(pageDTO, userInfoInTokenBO.getTenantId(), nickName);
		return ServerResponseEntity.success(sysUserPage);
	}

	@GetMapping
	@Operation(summary = "获取平台用户信息" , description = "根据用户id获取平台用户信息")
	public ServerResponseEntity<SysUserVO> detail(@RequestParam Long sysUserId) {
		return ServerResponseEntity.success(sysUserService.getByUserId(sysUserId));
	}

	@PostMapping
	@Operation(summary = "保存平台用户信息" , description = "保存平台用户信息")
	public ServerResponseEntity<Void> save(@Valid @RequestBody SysUserDTO sysUserDTO) {
		SysUser sysUser = BeanUtil.map(sysUserDTO, SysUser.class);
		sysUser.setSysUserId(null);
		sysUser.setHasAccount(0);
		sysUserService.save(sysUser,sysUserDTO.getRoleIds());
		return ServerResponseEntity.success();
	}

	@PutMapping
	@Operation(summary = "更新平台用户信息" , description = "更新平台用户信息")
	public ServerResponseEntity<Void> update(@Valid @RequestBody SysUserDTO sysUserDTO) {
		SysUser sysUser = BeanUtil.map(sysUserDTO, SysUser.class);
		sysUserService.update(sysUser,sysUserDTO.getRoleIds());
		return ServerResponseEntity.success();
	}

	@DeleteMapping
	@Operation(summary = "删除平台用户信息" , description = "根据平台用户id删除平台用户信息")
	public ServerResponseEntity<Void> delete(@RequestParam Long sysUserId) {
		sysUserService.deleteById(sysUserId);
		return ServerResponseEntity.success();
	}
}
