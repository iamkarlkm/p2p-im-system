package com.im.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class ReadWriteRoutingDataSource extends AbstractRoutingDataSource {
    
    private static final ThreadLocal<String> dataSourceKey = new ThreadLocal<>();
    
    public static final String WRITE = "write";
    public static final String READ = "read";
    
    @Override
    protected Object determineCurrentLookupKey() {
        String key = dataSourceKey.get();
        if (key == null) {
            return WRITE;
        }
        return key;
    }
    
    public static void setReadKey() {
        dataSourceKey.set(READ);
        log.debug("Set data source key to READ");
    }
    
    public static void setWriteKey() {
        dataSourceKey.set(WRITE);
        log.debug("Set data source key to WRITE");
    }
    
    public static void clearKey() {
        dataSourceKey.remove();
    }
    
    public static String getCurrentKey() {
        return dataSourceKey.get();
    }
}
