package com.reseau_partage.animaux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.reseau_partage.animaux", "com.reseau_partage.core"})
@EnableJpaRepositories(basePackages = "com.reseau_partage.core.repository")
@EntityScan(basePackages = "com.reseau_partage.core.entities")
public class AnimauxApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnimauxApplication.class, args);
    }
}
