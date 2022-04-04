package com.szs.szsrefund;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class SzsRefundApplication {

    public static void main(String[] args) {
        SpringApplication.run(SzsRefundApplication.class, args);
    }

}
