package com.ningxiang.shop.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author FrozenWatermelon
 * @date 2020/11/19
 */
@SpringBootApplication(scanBasePackages = { "com.ningxiang.shop" })
@EnableFeignClients(basePackages = {"com.ningxiang.shop.api.**.feign"})
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

}
