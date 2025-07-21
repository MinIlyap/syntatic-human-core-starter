package org.example.aspect;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.annotation.WeylandWatchingYou;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Aspect
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AdminClient adminClient;

    public AuditAspect(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.adminClient = AdminClient.create(kafkaTemplate.getProducerFactory().getConfigurationProperties());
        logger.info("AuditAspect initialized with Kafka support");
    }

    @Around("@annotation(weylandWatchingYou)")
    public Object audit(ProceedingJoinPoint joinPoint, WeylandWatchingYou weylandWatchingYou) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        String auditMessage = String.format("Audit: Method: %s, Params: %s, Result: %s, Duration: %dms, Time: %s",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs(),
                result,
                duration,
                Instant.now().toString());

        if (weylandWatchingYou.logToConsole()) {
            logger.info(auditMessage);
        }

        if (weylandWatchingYou.enableKafka()) {
            try {
                String topic = StringUtils.hasText(weylandWatchingYou.kafkaTopic())
                        ? weylandWatchingYou.kafkaTopic()
                        : "audit-topic";

                if (weylandWatchingYou.autoCreateTopic()) {
                    createTopicIfNeeded(topic,
                            weylandWatchingYou.topicPartitions(),
                            weylandWatchingYou.topicReplicas());
                }

                kafkaTemplate.send(topic, auditMessage);
            } catch (Exception e) {
                logger.error("Failed to send audit to Kafka", e);
            }
        }

        return result;
    }

    private void createTopicIfNeeded(String topicName, int partitions, short replicas) {
        try {
            if (!adminClient.listTopics().names().get().contains(topicName)) {
                NewTopic newTopic = new NewTopic(topicName, partitions, replicas);
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
                logger.info("Created new Kafka topic: {}", topicName);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.warn("Failed to create Kafka topic: {}", topicName, e);
        }
    }
}
