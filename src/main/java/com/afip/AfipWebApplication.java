package com.afip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.afip")
public class AfipWebApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AfipWebApplication.class, args);
    }
}