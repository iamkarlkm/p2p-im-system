package com.im.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ShardRoutingAspect {
    
    @Around("execution(* com.im.backend.repository..*.find*(..))")
    public Object routeToReadDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            ReadWriteRoutingDataSource.setReadKey();
            log.debug("Routing {} to read datasource", joinPoint.getSignature().getName());
            return joinPoint.proceed();
        } finally {
            ReadWriteRoutingDataSource.clearKey();
        }
    }
    
    @Around("execution(* com.im.backend.repository..*.save*(..)) || " +
            "execution(* com.im.backend.repository..*.update*(..)) || " +
            "execution(* com.im.backend.repository..*.delete*(..))")
    public Object routeToWriteDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            ReadWriteRoutingDataSource.setWriteKey();
            log.debug("Routing {} to write datasource", joinPoint.getSignature().getName());
            return joinPoint.proceed();
        } finally {
            ReadWriteRoutingDataSource.clearKey();
        }
    }
}
