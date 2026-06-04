package com.ningxiang.shop.leaf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author leaf
 */
@SpringBootApplication(scanBasePackages = { "com.ningxiang.shop" })
@EnableFeignClients(basePackages = {"com.ningxiang.shop.api.**.feign"})
public class LeafServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeafServerApplication.class, args);
	}

}
