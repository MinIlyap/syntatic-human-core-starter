package org.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WeylandWatchingYou {
    boolean logToConsole() default true;
    boolean enableKafka() default true;
    String kafkaTopic() default "audit-topic";
    short topicReplicas() default 1;
    int topicPartitions() default 1;
    boolean autoCreateTopic() default true;
}
