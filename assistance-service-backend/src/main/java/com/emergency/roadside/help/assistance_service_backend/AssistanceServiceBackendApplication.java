package com.emergency.roadside.help.assistance_service_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.emergency.roadside.help.assistance_service_backend.external")
public class AssistanceServiceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssistanceServiceBackendApplication.class, args);
	}

}
