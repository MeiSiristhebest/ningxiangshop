package com.ningxiang.shop.common.exception;

import com.ningxiang.shop.common.response.ResponseEnum;

/**
 * @author FrozenWatermelon
 * @date 2020/7/11
 */
public class NingxiangException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Object object;

	private ResponseEnum responseEnum;

	public NingxiangException(String msg) {
		super(msg);
	}

	public NingxiangException(String msg, Object object) {
		super(msg);
		this.object = object;
	}

	public NingxiangException(String msg, Throwable cause) {
		super(msg, cause);
	}


	public NingxiangException(ResponseEnum responseEnum) {
		super(responseEnum.getMsg());
		this.responseEnum = responseEnum;
	}

	public NingxiangException(ResponseEnum responseEnum, Object object) {
		super(responseEnum.getMsg());
		this.responseEnum = responseEnum;
		this.object = object;
	}


	public Object getObject() {
		return object;
	}

	public ResponseEnum getResponseEnum() {
		return responseEnum;
	}

}
