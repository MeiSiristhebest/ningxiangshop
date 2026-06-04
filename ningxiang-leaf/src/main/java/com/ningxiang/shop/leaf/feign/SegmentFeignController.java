package com.ningxiang.shop.leaf.feign;

import com.ningxiang.shop.api.leaf.feign.SegmentFeignClient;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.leaf.common.Result;
import com.ningxiang.shop.leaf.common.Status;
import com.ningxiang.shop.leaf.exception.LeafServerException;
import com.ningxiang.shop.leaf.exception.NoKeyException;
import com.ningxiang.shop.leaf.service.SegmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


/**
 * @author FrozenWatermelon
 * @date 2020/7/15
 */
@RestController
public class SegmentFeignController implements SegmentFeignClient {

	private static final Logger logger = LoggerFactory.getLogger(SegmentFeignController.class);


	@Autowired
	private SegmentService segmentService;

	@Override
	public ServerResponseEntity<Long> getSegmentId(String key) {
		return ServerResponseEntity.success(get(key, segmentService.getId(key)));
	}


	private Long get(String key, Result id) {
		Result result;
		if (key == null || key.isEmpty()) {
			throw new NoKeyException();
		}
		result = id;
		if (Objects.equals(result.getStatus(), Status.EXCEPTION)) {
			throw new LeafServerException(result.toString());
		}
		return result.getId();
	}
}
