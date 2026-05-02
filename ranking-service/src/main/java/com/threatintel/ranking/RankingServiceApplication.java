package com.threatintel.ranking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class RankingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RankingServiceApplication.class, args);
    }
}
