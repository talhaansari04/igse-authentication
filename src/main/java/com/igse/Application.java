package com.igse;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
//@EnableSchedulerLock()
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
