package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class StarterConfig {

    @Bean
    public ThreadPoolExecutor commonTaskExecutor() {
        return new ThreadPoolExecutor(
                2,
                5,
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
