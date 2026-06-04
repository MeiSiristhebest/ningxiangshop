package com.ningxiang.shop.product.controller.admin;

import cn.hutool.core.collection.CollUtil;
import com.ningxiang.shop.api.product.vo.AttrVO;
import com.ningxiang.shop.common.constant.Constant;
import com.ningxiang.shop.common.database.dto.PageDTO;
import com.ningxiang.shop.common.database.vo.PageVO;
import com.ningxiang.shop.common.exception.NingxiangException;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import com.ningxiang.shop.product.constant.AttrType;
import com.ningxiang.shop.product.constant.SearchType;
import com.ningxiang.shop.product.dto.AttrDTO;
import com.ningxiang.shop.product.model.Attr;
import com.ningxiang.shop.product.model.AttrValue;
import com.ningxiang.shop.product.service.AttrService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import com.ningxiang.shop.common.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * 属性信息
 *
 * @author FrozenWatermelon
 * @date 2020-10-28 15:27:23
 */
@RestController("platformAttrController")
@RequestMapping("/admin/attr")
@Tag(name = "admin-属性信息")
public class AttrController {

    @Autowired
    private AttrService attrService;



	@GetMapping("/page")
	@Operation(summary = "获取属性信息列表" , description = "分页获取属性信息列表")
	public ServerResponseEntity<PageVO<AttrVO>> page(@Valid PageDTO pageDTO, AttrDTO attrDTO) {
		PageVO<AttrVO> attrPage = attrService.page(pageDTO, attrDTO);
		return ServerResponseEntity.success(attrPage);
	}

	@GetMapping
    @Operation(summary = "获取属性信息" , description = "根据attrId获取属性信息")
    public ServerResponseEntity<AttrVO> getByAttrId(@RequestParam Long attrId) {
        return ServerResponseEntity.success(attrService.getByAttrId(attrId));
    }

    @PostMapping
    @Operation(summary = "保存属性信息" , description = "保存属性信息")
    public ServerResponseEntity<Void> save(@Valid @RequestBody AttrDTO attrDTO) {
        if (Objects.equals(Constant.PLATFORM_SHOP_ID, AuthUserContext.get().getTenantId()) && Objects.isNull(attrDTO.getAttrType())) {
            throw new NingxiangException("属性类型不能为空");
        }
	    checkAttrInfo(attrDTO);
        Attr attr = BeanUtil.map(attrDTO, Attr.class);
        attr.setAttrValues(BeanUtil.mapAsList(attrDTO.getAttrValues(), AttrValue.class));
        attrService.save(attr, attrDTO.getCategoryIds());
        removeCacheAttrUnionCategory(attrDTO.getCategoryIds());
        return ServerResponseEntity.success();
    }

    @PutMapping
    @Operation(summary = "更新属性信息" , description = "更新属性信息")
    public ServerResponseEntity<Void> update(@Valid @RequestBody AttrDTO attrDTO) {
        checkAttrInfo(attrDTO);
        Attr attr = BeanUtil.map(attrDTO, Attr.class);
        if (CollUtil.isNotEmpty(attrDTO.getAttrValues())) {
            attr.setAttrValues(BeanUtil.mapAsList(attrDTO.getAttrValues(), AttrValue.class));
        }
        List<Long> categoryIds = null;
        if (Objects.equals(AttrType.BASIC.value(), attr.getAttrType())) {
            categoryIds = attrService.getAttrOfCategoryIdByAttrId(attrDTO.getAttrId());
            categoryIds.addAll(attrDTO.getCategoryIds());
        }
        attrService.update(attr, attrDTO.getCategoryIds());
        removeCacheAttrUnionCategory(categoryIds);
        return ServerResponseEntity.success();
    }

    @DeleteMapping
    @Operation(summary = "删除属性信息" , description = "根据属性信息id删除属性信息")
    public ServerResponseEntity<Void> delete(@RequestParam Long attrId) {
        List<Long> categoryIds = attrService.getAttrOfCategoryIdByAttrId(attrId);
        attrService.deleteById(attrId);
        if (CollUtil.isNotEmpty(categoryIds)) {
            removeCacheAttrUnionCategory(categoryIds);
        }
        return ServerResponseEntity.success();
    }

    @GetMapping("/get_attrs_by_category_id")
    @Operation(summary = "根据分类及属性类别获取属性列表" , description = "根据分类及属性类别获取属性列表")
    @Parameter(name = "categoryId", description = "分类id" , required = true)
    public ServerResponseEntity<List<AttrVO>> getAttrsByCategoryId(@RequestParam(value = "categoryId") Long categoryId) {
        return ServerResponseEntity.success(attrService.getAttrsByCategoryIdAndAttrType(categoryId));
    }

    @GetMapping("/get_shop_attrs")
    @Operation(summary = "获取店铺中的销售属性" , description = "获取店铺中的销售属性")
    public ServerResponseEntity<List<AttrVO>> getShopAttrs () {
        return ServerResponseEntity.success(attrService.getShopAttrs(AuthUserContext.get().getTenantId()));
    }

    /**
     * 校验属性数据
     * @param attrDTO
     */
    private void checkAttrInfo(AttrDTO attrDTO) {
        if (!Objects.equals(Constant.PLATFORM_SHOP_ID, AuthUserContext.get().getTenantId())) {
            attrDTO.setAttrType(AttrType.SALES.value());
        }
        if (Objects.equals(AttrType.SALES.value(), attrDTO.getAttrType())) {
            attrDTO.setSearchType(SearchType.NOT_SEARCH.value());
            return;
        }
        if (CollUtil.isEmpty(attrDTO.getCategoryIds())) {
            throw new NingxiangException("关联分类不能为空");
        }
        if (Objects.isNull(attrDTO.getSearchType())) {
            throw new NingxiangException("搜索属性不能为空");
        }
    }

    /**
     *  删除属性关联的分类缓存
     */
    private void removeCacheAttrUnionCategory(List<Long> categoryIds) {
        // 清除分类缓存
        if (!Objects.equals(Constant.PLATFORM_SHOP_ID, AuthUserContext.get().getTenantId()) || CollUtil.isEmpty(categoryIds)) {
            return;
        }
        attrService.removeAttrByCategoryId(categoryIds);
    }

}
