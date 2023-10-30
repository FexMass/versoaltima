package com.versoaltima.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class TaskApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TaskApplication.class);
        //If property is not set, application won't run
        String taskXPath = System.getProperty("TASK_X_PATH");
        app.setDefaultProperties(Collections
                .singletonMap("spring.config.location", taskXPath));
        app.run(args);
    }
}
