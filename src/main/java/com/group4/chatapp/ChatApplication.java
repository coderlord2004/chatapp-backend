package com.group4.chatapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {

    public static void main(String[] args) {

        SpringApplication.run(ChatApplication.class, args);
        System.out.println("Running completed!");
    }

}