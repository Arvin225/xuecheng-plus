package com.example.mpgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MpGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MpGeneratorApplication.class, args);
    }

}
