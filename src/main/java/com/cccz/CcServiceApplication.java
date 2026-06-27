package com.cccz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class CcServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(CcServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CcServiceApplication.class, args);
        log.info("service is running");
    }
}
