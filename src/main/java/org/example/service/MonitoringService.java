package org.example.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

@Service
public class MonitoringService {
    private final ThreadPoolExecutor commonTaskExecutor;

    public MonitoringService(ThreadPoolExecutor commonTaskExecutor, MeterRegistry meterRegistry) {
        this.commonTaskExecutor = commonTaskExecutor;
        Gauge.builder("android.queue.size", commonTaskExecutor, e -> e.getQueue().size())
                .description("Current number of tasks in the queue")
                .register(meterRegistry);
    }
}

