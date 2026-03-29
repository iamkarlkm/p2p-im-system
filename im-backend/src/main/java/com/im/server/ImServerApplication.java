package com.im.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 即时通讯系统后端服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ImServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ImServerApplication.class, args);
    }
}
