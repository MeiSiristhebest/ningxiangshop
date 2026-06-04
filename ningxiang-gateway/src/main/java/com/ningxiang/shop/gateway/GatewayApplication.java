package com.ningxiang.shop.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author FrozenWatermelon
 * @date 2020/7/23
 */
@SpringBootApplication(scanBasePackages = { "com.ningxiang.shop" })
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
