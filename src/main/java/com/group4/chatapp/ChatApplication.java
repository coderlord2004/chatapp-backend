package com.group4.chatapp;

import org.quartz.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ChatApplication {
    public static void main(String[] args) throws SchedulerException {

        SpringApplication.run(ChatApplication.class, args);
    }
}