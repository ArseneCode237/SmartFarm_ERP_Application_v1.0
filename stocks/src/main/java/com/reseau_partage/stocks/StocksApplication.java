package com.reseau_partage.stocks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.reseau_partage.stocks", "com.reseau_partage.core"})
@EnableJpaRepositories(basePackages = {"com.reseau_partage.core.repository", "com.reseau_partage.stocks.repository"})
@EntityScan(basePackages = {"com.reseau_partage.core.entities", "com.reseau_partage.stocks.entities"})
@EnableAsync
@EnableScheduling
public class StocksApplication {

    public static void main(String[] args) {
        SpringApplication.run(StocksApplication.class, args);
    }
}