package com.ningxiang.shop.product.model;

import com.ningxiang.shop.common.model.BaseModel;

import java.io.Serializable;

/**
 * 商品详情信息
 *
 * @author FrozenWatermelon
 * @date 2020-10-28 15:27:24
 */
public class SpuDetail extends BaseModel implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	private Long spuId;

	/**
	 * 商品详情
	 */
	private String detail;

	public Long getSpuId() {
		return spuId;
	}

	public void setSpuId(Long spuId) {
		this.spuId = spuId;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		return "SpuDetail{" +
				"spuId=" + spuId +
				",createTime=" + createTime +
				",updateTime=" + updateTime +
				",detail=" + detail +
				'}';
	}
}
