package com.seal.affair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.seal.affair", "com.seal.framework"})
@SpringBootApplication
public class AffairCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AffairCenterApplication.class, args);
    }

}
