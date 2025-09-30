package com.group4.chatapp;

import com.group4.chatapp.jobs.PublishPostJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.util.Date;

@SpringBootApplication
@EnableScheduling
public class ChatApplication {
    public static void main(String[] args) throws SchedulerException {

        SpringApplication.run(ChatApplication.class, args);
    }
}