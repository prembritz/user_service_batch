package com.persistentminds.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Configuration
public class UserServiceBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceBatchApplication.class, args);
    }
}
