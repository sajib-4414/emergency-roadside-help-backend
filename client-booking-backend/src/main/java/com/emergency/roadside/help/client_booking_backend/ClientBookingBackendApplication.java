package com.emergency.roadside.help.client_booking_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class ClientBookingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientBookingBackendApplication.class, args);
	}

}
