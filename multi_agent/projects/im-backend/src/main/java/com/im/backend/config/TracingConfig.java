package com.im.backend.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class TracingConfig {
    
    @Value("${opentelemetry.service.name:im-backend}")
    private String serviceName;
    
    @Value("${opentelemetry.otlp.endpoint:http://localhost:4317}")
    private String otlpEndpoint;
    
    @Value("${opentelemetry.enabled:false}")
    private boolean tracingEnabled;
    
    @Bean
    public OpenTelemetry openTelemetry() {
        if (!tracingEnabled) {
            return OpenTelemetry.noop();
        }
        
        Resource resource = Resource.getDefault()
                .merge(Resource.create(
                        Attributes.of(
                                ResourceAttributes.SERVICE_NAME, serviceName,
                                ResourceAttributes.SERVICE_VERSION, "1.0.0",
                                ResourceAttributes.DEPLOYMENT_ENVIRONMENT, "production"
                        )
                ));
        
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(otlpEndpoint)
                .setTimeout(30, TimeUnit.SECONDS)
                .build();
        
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(
                        BatchSpanProcessor.builder(spanExporter)
                                .setMaxQueueSize(2048)
                                .setMaxExportBatchSize(512)
                                .setExporterTimeout(30, TimeUnit.SECONDS)
                                .build()
                )
                .build();
        
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .build();
    }
    
    @Bean
    public io.opentelemetry.api.trace.Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(serviceName);
    }
}
