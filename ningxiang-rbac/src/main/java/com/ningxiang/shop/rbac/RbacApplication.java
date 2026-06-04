package com.ningxiang.shop.rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author FrozenWatermelon
 * @date 2020/6/24
 */
@SpringBootApplication(scanBasePackages = { "com.ningxiang.shop" })
@EnableFeignClients(basePackages = {"com.ningxiang.shop.api.**.feign"})
public class RbacApplication {

	public static void main(String[] args) {
		SpringApplication.run(RbacApplication.class, args);
	}

}
