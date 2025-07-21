package org.example.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.example.aspect.AuditAspect;
import org.example.service.MonitoringService;
import org.example.service.TaskService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Import({StarterConfig.class})
@ConditionalOnClass(MeterRegistry.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SyntheticHumanCoreAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public TaskService taskService(ThreadPoolExecutor commonTaskExecutor, MeterRegistry meterRegistry) {
        return new TaskService(commonTaskExecutor, meterRegistry);
    }

    @Bean
    @ConditionalOnClass(KafkaTemplate.class)
    @ConditionalOnMissingBean
    public AuditAspect auditAspect(KafkaTemplate<String, String> kafkaTemplate) {
        return new AuditAspect(kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public MonitoringService monitoringService(ThreadPoolExecutor commonTaskExecutor, MeterRegistry meterRegistry) {
        return new MonitoringService(commonTaskExecutor, meterRegistry);
    }

    @Bean
    public MeterRegistry meterRegistry() {
        return new CompositeMeterRegistry();
    }
}