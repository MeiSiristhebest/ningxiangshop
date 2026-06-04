package com.ningxiang.shop.product.service;

import com.ningxiang.shop.api.product.vo.CategoryVO;
import com.ningxiang.shop.product.dto.CategoryDTO;
import com.ningxiang.shop.product.model.Category;

import java.util.List;

/**
 * 分类信息
 *
 * @author FrozenWatermelon
 * @date 2020-10-28 15:27:24
 */
public interface CategoryAndSpuService {
	/**
	 * 分类的启用和禁用
	 * @param categoryDTO
	 * @return
	 */
    Boolean categoryEnableOrDisable(CategoryDTO categoryDTO);

}
