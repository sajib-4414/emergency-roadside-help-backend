package com.emergency.roadside.help.responder_assignment_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.emergency.roadside.help.responder_assignment_backend.external")
public class ResponderAssignmentBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResponderAssignmentBackendApplication.class, args);
	}

}
