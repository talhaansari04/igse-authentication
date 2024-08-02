package com.igse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableKafka
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
public class IgseAuthenticationApplication {
	public static void main(String[] args) {
		SpringApplication.run(IgseAuthenticationApplication.class, args);
	}
}
