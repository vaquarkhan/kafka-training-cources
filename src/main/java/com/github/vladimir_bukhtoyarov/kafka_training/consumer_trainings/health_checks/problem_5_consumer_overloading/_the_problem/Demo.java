package com.github.vladimir_bukhtoyarov.kafka_training.consumer_trainings.health_checks.problem_5_consumer_overloading._the_problem;

import com.github.vladimir_bukhtoyarov.kafka_training.consumer_trainings.util.Constants;
import com.github.vladimir_bukhtoyarov.kafka_training.consumer_trainings.util.InfiniteIterator;
import com.github.vladimir_bukhtoyarov.kafka_training.consumer_trainings.util.Message;
import com.github.vladimir_bukhtoyarov.kafka_training.consumer_trainings.util.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

public class Demo {

    // ********* Plan **************
    // 1 - Start consumers 1,2,3
    //
    // 2 - Start producer.
    //
    // 3 - Show consumer lags on admin panel.
    //     Point that consumer does not know about its lag.
    //     Describe the complexity of problem, mention about practices of Confluent and HortonWorks to calculate the lag

    private static final class StartProducer {
        public static void main(String[] args) {
            Iterator<ProducerRecord<String, Message>> records = new InfiniteIterator<>(() -> {
                Message message = new Message();
                message.setPayload(UUID.randomUUID().toString());
                message.setDelayMillis(1000);
                return new ProducerRecord<>(Constants.TOPIC, message);
            });

            Producer producer = new Producer();
            producer.send(100, Duration.ofSeconds(1), records);
        }
    }

    private static final class StartConsumer_1 {
        public static void main(String[] args) {
            Set<String> topics = new HashSet<>(Collections.singleton(Constants.TOPIC));
            Consumer consumer = new Consumer("consumer-1", topics);
            consumer.start();
            initHealthCheck(consumer);
        }
    }

    private static final class StartConsumer_2 {
        public static void main(String[] args) {
            Set<String> topics = new HashSet<>(Collections.singleton(Constants.TOPIC));
            Consumer consumer = new Consumer("consumer-2", topics);
            consumer.start();
            initHealthCheck(consumer);
        }
    }

    private static final class StartConsumer_3 {
        public static void main(String[] args) {
            Set<String> topics = new HashSet<>(Collections.singleton(Constants.TOPIC));
            Consumer consumer = new Consumer("consumer-3", topics);
            consumer.start();
            initHealthCheck(consumer);
        }
    }

    private static void initHealthCheck(Consumer consumer) {
        Logger logger = LoggerFactory.getLogger("health-check");
        Timer healthCheckTimer = new Timer("Consumer health-checker");
        healthCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Consumer.HealthStatus status = consumer.getHealth();
                    if (status.isHealthy()) {
                        logger.info("" + status);
                    } else {
                        logger.error("" + status);
                    }
                } catch (Throwable t) {
                    logger.error("Failed to check health of consumer", t);
                }
            }
        }, 10000, 10000);
    }

}
