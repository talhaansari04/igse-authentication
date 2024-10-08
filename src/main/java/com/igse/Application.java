package com.igse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
