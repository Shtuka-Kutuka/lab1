package com.example.dailymoodtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DailyMoodtrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyMoodtrackerApplication.class, args);
    }
}