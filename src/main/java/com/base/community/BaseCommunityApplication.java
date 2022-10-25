package com.base.community;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
@EnableBatchProcessing
public class BaseCommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseCommunityApplication.class, args);


    }

}
