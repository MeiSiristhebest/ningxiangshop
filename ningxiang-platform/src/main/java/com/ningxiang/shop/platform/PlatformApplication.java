package com.ningxiang.shop.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author lhd
 * @date 2020/12/22
 */
@SpringBootApplication(scanBasePackages = { "com.ningxiang.shop" })
@EnableFeignClients(basePackages = {"com.ningxiang.shop.api.**.feign"})
public class PlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlatformApplication.class, args);
	}

}
