package com.reseau_partage.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.reseau_partage.auth",
		"com.reseau_partage.core"
})
@EntityScan(basePackages = "com.reseau_partage.core.entities")
@EnableJpaRepositories(basePackages = {
		"com.reseau_partage.auth",
		"com.reseau_partage.core.repository"
})
@EnableDiscoveryClient
public class AuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

}
