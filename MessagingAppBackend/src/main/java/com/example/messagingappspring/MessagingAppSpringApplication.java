package com.example.messagingappspring;

import com.example.messagingappspring.database.DBPopulate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MessagingAppSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagingAppSpringApplication.class, args);
        DBPopulate.populate();
    }
}
