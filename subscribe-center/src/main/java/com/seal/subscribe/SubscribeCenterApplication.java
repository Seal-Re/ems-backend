package com.seal.subscribe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.seal.subscribe", "com.seal.framework"})
@SpringBootApplication
public class SubscribeCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubscribeCenterApplication.class, args);
    }

}
