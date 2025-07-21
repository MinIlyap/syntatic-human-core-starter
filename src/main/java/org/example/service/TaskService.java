package org.example.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.example.model.Priority;
import org.example.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final ThreadPoolExecutor executor;
    private final MeterRegistry registry;
    private final Counter totalTasksCounter;
    private final Counter criticalTasksCounter;
    private final Counter rejectedTasksCounter;

    public TaskService(ThreadPoolExecutor executor, MeterRegistry registry) {
        this.executor = executor;
        this.registry = registry;

        this.totalTasksCounter = Counter.builder("tasks.total")
                .description("Total tasks submitted")
                .register(registry);

        this.criticalTasksCounter = Counter.builder("tasks.critical")
                .description("Critical tasks executed immediately")
                .register(registry);

        this.rejectedTasksCounter = Counter.builder("tasks.rejected")
                .description("Rejected tasks due to queue overflow")
                .register(registry);
    }

    public String submitTask(Task task) {
        String logMessage = String.format("Task: description=%s, priority=%s, author=%s, time=%s",
                task.getDescription(), task.getPriority(), task.getAuthor(), task.getTime());

        Counter.builder("tasks.by.author")
                .tag("author", task.getAuthor())
                .register(registry)
                .increment();

        totalTasksCounter.increment();

        if (task.getPriority() == Priority.CRITICAL) {
            logger.info("Executing CRITICAL task: {}", logMessage);
            criticalTasksCounter.increment();
            return "CRITICAL task executed immediately";
        } else {
            try {
                executor.submit(() -> {
                    try {
                        Thread.sleep(4000); // Делаем "живое" выполнение задач
                        logger.info("Executing COMMON task: {}", logMessage);
                        Counter.builder("tasks.completed")
                                .tag("priority", "COMMON")
                                .register(registry)
                                .increment();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                return "COMMON task submitted to queue";
            } catch (RejectedExecutionException e) {
                logger.error("Queue overflow: {}", logMessage);
                rejectedTasksCounter.increment();
                throw new RuntimeException("Queue is full, try again later");
            }
        }
    }
}