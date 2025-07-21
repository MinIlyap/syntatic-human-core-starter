# Synthetic Human Core

Spring Boot стартер для логирования, аудита методов, отправки сообщений в Kafka и сбора метрик задач.

---

## Как добавить в проект

1. Добавьте зависимость в `pom.xml`:

```xml
<dependency>
  <groupId>org.example</groupId>
  <artifactId>synthetic-human-core-starter</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## Основные функции

### 1. Аудит методов

Аннотируйте любой метод:

```java
@WeylandWatchingYou(
    logToConsole = true,
    enableKafka = true,
    kafkaTopic = "audit-log",
    autoCreateTopic = true,
    topicReplicas = 1,
    topicPartitions = 1
)
public void doSomething() {
    // бизнес-логика
}
```

🔹 Логи пишутся в консоль и/или Kafka  
🔹 Возможно автоматическое создание нового топика

---

### 2. Отправка задач с приоритетами

```java
Task task = new Task();
task.setAuthor("Ripley");
task.setPriority(Priority.CRITICAL);
task.setDescription("Проверка сигнала");
task.setTime("2025-07-21T12:00:00Z");

taskService.submitTask(task);
```

- `CRITICAL` задачи выполняются сразу
- `COMMON` — идут в очередь

---

### 3. Метрики (Micrometer)

Собираются метрики:
- `tasks.total` - общее количество выполненных задач
- `tasks.critical` - количество выполненных критических задач
- `tasks.rejected` - количество отклоненных задач из-за переполнения очереди
- `tasks.by.author` - количество выполненных задач по авторам
- `android.queue.size` - количество общих задач в очереди

---

