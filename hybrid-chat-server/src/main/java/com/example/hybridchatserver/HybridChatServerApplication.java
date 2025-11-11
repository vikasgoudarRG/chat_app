package com.example.hybridchatserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class HybridChatServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HybridChatServerApplication.class, args);
    }
}
