package com.im.backend.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class DataSourceConfig {
    
    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/im_db}")
    private String masterUrl;
    
    @Value("${spring.datasource.username:root}")
    private String username;
    
    @Value("${spring.datasource.password:password}")
    private String password;
    
    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;
    
    @Value("${spring.datasource.hikari.maximum-pool-size:20}")
    private int maxPoolSize;
    
    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minIdle;
    
    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;
    
    @Value("${spring.datasource.hikari.idle-timeout:600000}")
    private long idleTimeout;
    
    @Value("${spring.datasource.hikari.max-lifetime:1800000}")
    private long maxLifetime;
    
    @Value("${spring.datasource.read.count:2}")
    private int readDataSourceCount;
    
    @Bean
    @Primary
    public DataSource masterDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(masterUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setPoolName("master-pool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        log.info("Creating master datasource: {}", masterUrl);
        return new HikariDataSource(config);
    }
    
    @Bean
    public Map<String, DataSource> readDataSources() {
        Map<String, DataSource> readDataSources = new HashMap<>();
        
        for (int i = 0; i < readDataSourceCount; i++) {
            HikariConfig config = new HikariConfig();
            String readUrl = masterUrl.replace("/im_db", "/im_db_read" + (i + 1));
            
            config.setJdbcUrl(readUrl);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName(driverClassName);
            config.setMaximumPoolSize(maxPoolSize / 2);
            config.setMinimumIdle(minIdle / 2);
            config.setConnectionTimeout(connectionTimeout);
            config.setIdleTimeout(idleTimeout);
            config.setMaxLifetime(maxLifetime);
            config.setPoolName("read-pool-" + (i + 1));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            String dsName = "read" + (i + 1);
            readDataSources.put(dsName, new HikariDataSource(config));
            
            log.info("Created read datasource {}: {}", dsName, readUrl);
        }
        
        return readDataSources;
    }
    
    @Bean
    public ReadWriteRoutingDataSource routingDataSource(
            DataSource masterDataSource,
            Map<String, DataSource> readDataSources) {
        
        ReadWriteRoutingDataSource routingDataSource = new ReadWriteRoutingDataSource();
        
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(ReadWriteRoutingDataSource.WRITE, masterDataSource);
        
        readDataSources.forEach((key, value) -> 
                targetDataSources.put(key, value));
        
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        
        log.info("Configured routing datasource with {} read datasources", readDataSources.size());
        return routingDataSource;
    }
}
