package com.example.chatserverparticipant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ChatServerParticipantApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServerParticipantApplication.class, args);
    }

}
