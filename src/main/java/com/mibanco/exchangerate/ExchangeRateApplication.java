package com.mibanco.exchangerate;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ExchangeRateApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeRateApplication.class, args);
    }
}
