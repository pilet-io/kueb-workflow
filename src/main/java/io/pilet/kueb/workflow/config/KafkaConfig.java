package io.pilet.kueb.workflow.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    public static final String ENTITY_EVENTS_TOPIC = "entity.events";
    public static final String ENTITY_DUP_NOTIFICATION_TOPIC = "entity.dup.notifications";

    @Bean
    public NewTopic entityTopic() {
        return TopicBuilder.name(ENTITY_EVENTS_TOPIC)
                .partitions(2)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic entityDupNotification() {
        return TopicBuilder.name(ENTITY_DUP_NOTIFICATION_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

}
