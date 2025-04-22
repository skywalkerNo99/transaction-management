package com.hsbc.transaction.config;

import com.hsbc.transaction.util.SnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfig {
    
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        // For demonstration, using workerId=1 and datacenterId=1
        // In production, these should be configured based on deployment environment
        return new SnowflakeIdGenerator(1, 1);
    }
} 