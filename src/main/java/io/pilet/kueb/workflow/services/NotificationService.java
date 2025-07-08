package io.pilet.kueb.workflow.services;


import io.pilet.kueb.workflow.common.Notification;
import io.pilet.kueb.workflow.common.NotificationHandler;
import io.pilet.kueb.workflow.config.KafkaConfig;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Log4j2
public class NotificationService implements Runnable {
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final Connection connection;
    private final PGConnection pgConnection;
    private final Map<String, NotificationHandler> handlers = new HashMap<>();

    @SneakyThrows
    public NotificationService(ApplicationContext applicationContext,
                               JdbcTemplate jdbcTemplate,
                               KafkaTemplate<String,String> kafkaTemplate) {
        assert jdbcTemplate.getDataSource() != null;
        this.kafkaTemplate = kafkaTemplate;
        this.connection = jdbcTemplate.getDataSource().getConnection();
        this.pgConnection = connection.unwrap(PGConnection.class);

        Map<String,Object> map = applicationContext.getBeansWithAnnotation(Notification.class);
        //log.info("Found {} notification handlers", map.size());
        for(String key : map.keySet()) {
            NotificationHandler handler = (NotificationHandler) map.get(key);
            Notification annotation = handler.getClass().getAnnotation(Notification.class);
            log.info("Found notification handler: {} {}", annotation.value(), handler.getClass().getName());
            handlers.put(annotation.value(), handler);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(this);
    }

    @SneakyThrows
    public void run() {
        log.info("Listening for notifications");
        try (Statement statement = connection.createStatement()) {
            for(String channel : handlers.keySet()) {
                statement.execute("LISTEN " + channel);
                log.info("Listening for notifications on channel: {}", channel);
            }
        }
        while (true) {
            PGNotification[] notifications = pgConnection.getNotifications(1000);
            if (notifications == null) {
                log.info("No notifications received");
                continue;
            }
            for (PGNotification notification : notifications) {
                kafkaTemplate.send(KafkaConfig.ENTITY_DUP_NOTIFICATION_TOPIC, notification.getName(), notification.getParameter());
            }
        }
    }

}
