package com.im.backend.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.*;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

@Configuration
public class PrometheusConfig {
    
    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(
                io.micrometer.prometheus.PrometheusConfig.DEFAULT
        );
        
        new JvmMemoryMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
        new ClassLoaderMetrics().bindTo(registry);
        new UptimeMetrics().bindTo(registry);
        new DiskSpaceMetrics().bindTo(registry);
        new ProcessMetrics().bindTo(registry);
        
        bindCustomMetrics(registry);
        
        return registry;
    }
    
    private void bindCustomMetrics(PrometheusMeterRegistry registry) {
        registry.gauge("system.cpu.usage", 
                getSystemCPUUsage());
        
        registry.gauge("system.memory.usage",
                getSystemMemoryUsage());
        
        registry.gauge("jvm.memory.used",
                getJvmMemoryUsed());
        
        registry.gauge("jvm.thread.count",
                ManagementFactory.getThreadMXBean().getThreadCount());
    }
    
    private double getSystemCPUUsage() {
        try {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) 
                    ManagementFactory.getOperatingSystemMXBean();
            return osBean.getSystemCpuLoad() * 100;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private double getSystemMemoryUsage() {
        try {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) 
                    ManagementFactory.getOperatingSystemMXBean();
            long totalMemory = osBean.getTotalPhysicalMemorySize();
            long freeMemory = osBean.getFreePhysicalMemorySize();
            long usedMemory = totalMemory - freeMemory;
            
            return (double) usedMemory / totalMemory * 100;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private double getJvmMemoryUsed() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        return totalMemory - freeMemory;
    }
}
