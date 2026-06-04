package com.ningxiang.shop.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author FrozenWatermelon
 * @date 2020/09/22
 */
@SpringBootApplication(scanBasePackages = { "com.ningxiang.shop" })
@EnableFeignClients(basePackages = {"com.ningxiang.shop.api.**.feign"})
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

}
