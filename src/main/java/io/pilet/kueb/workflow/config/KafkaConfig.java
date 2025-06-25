package io.pilet.kueb.workflow.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    public static final String ENTITY_TOPIC = "entity.events";

    @Bean
    public NewTopic entityTopic() {
        return TopicBuilder.name(ENTITY_TOPIC)
                .partitions(2)
                .replicas(1)
                .build();
    }

}
