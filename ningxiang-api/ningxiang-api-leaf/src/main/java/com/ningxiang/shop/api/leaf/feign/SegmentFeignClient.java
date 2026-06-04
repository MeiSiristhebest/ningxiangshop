package com.ningxiang.shop.api.leaf.feign;

import com.ningxiang.shop.common.feign.FeignInsideAuthConfig;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author FrozenWatermelon
 * @date 2020/09/08
 */
@FeignClient(value = "ningxiang-leaf",contextId ="segment")
public interface SegmentFeignClient {

	/**
	 * 获取id
	 * @param key
	 * @return
	 */
	@GetMapping(value = FeignInsideAuthConfig.FEIGN_INSIDE_URL_PREFIX + "/insider/segment")
	ServerResponseEntity<Long> getSegmentId(@RequestParam("key") String key);


}
