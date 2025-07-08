package io.pilet.kueb.workflow.services;

import io.pilet.kueb.common.util.Hash;
import io.pilet.kueb.workflow.common.Notification;
import io.pilet.kueb.workflow.common.NotificationHandler;
import io.pilet.kueb.workflow.config.KafkaConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Map<String, NotificationHandler> handlers = new HashMap<>();
    private final PassiveExpiringMap<String,Boolean> cacheMap = new PassiveExpiringMap<>(7000);

    public KafkaService(ApplicationContext applicationContext,
                        KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        Map<String,Object> map = applicationContext.getBeansWithAnnotation(Notification.class);
        for(String key : map.keySet()) {
            NotificationHandler handler = (NotificationHandler) map.get(key);
            Notification annotation = handler.getClass().getAnnotation(Notification.class);
            handlers.put(annotation.value(), handler);
        }
    }

    @KafkaListener(topics = KafkaConfig.ENTITY_EVENTS_TOPIC, groupId = "event_listener")
    void onEvent(ConsumerRecord<String,String> record) {
        try {
            NotificationHandler handler = handlers.get(record.key());
            if(handler == null) {
                log.error("No handler found for key: {}, value: {}", record.key(), record.value());
            } else {
                handler.onMessage(record.value());
            }
        } catch (Exception e) {
            log.error("Error processing Entity Event", e);
        }
    }

    @KafkaListener(topics =  KafkaConfig.ENTITY_DUP_NOTIFICATION_TOPIC, groupId = "event_dedup")
    void dedup(ConsumerRecord<String,String> record) {
        String hash = Hash.of(record.key(),record.value());
        if(!cacheMap.containsKey(hash)) {
            cacheMap.put(hash, true);
            kafkaTemplate.send(KafkaConfig.ENTITY_EVENTS_TOPIC, record.key(), record.value());
        }
    }

}
