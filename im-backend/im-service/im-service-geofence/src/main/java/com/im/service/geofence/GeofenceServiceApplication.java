package com.im.service.geofence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 地理围栏服务启动类
 * 提供LBS位置服务、地理围栏管理、位置分享等功能
 * 
 * @author IM Development Team
 * @since 2026-04-12
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.im.service.geofence", "com.im.common"})
public class GeofenceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeofenceServiceApplication.class, args);
    }
}
