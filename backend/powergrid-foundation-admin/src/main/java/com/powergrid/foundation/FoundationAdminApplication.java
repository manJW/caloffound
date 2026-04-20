package com.powergrid.foundation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.powergrid.foundation")
@MapperScan("com.powergrid.foundation.core.persistence.mapper")
public class FoundationAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoundationAdminApplication.class, args);
    }
}
