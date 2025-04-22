package com.hsbc.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for the Transaction Management System.
 * This application provides RESTful APIs for managing financial transactions.
 * 
 * @EnableCaching enables Spring's caching support
 * @SpringBootApplication combines @Configuration, @EnableAutoConfiguration, and @ComponentScan
 */
@SpringBootApplication
@EnableCaching
public class TransactionManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionManagementApplication.class, args);
    }
} 