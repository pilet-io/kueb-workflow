package io.pilet.kueb.workflow.services;


import io.pilet.kueb.workflow.common.NotificationHandler;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class NotificationService implements Runnable {
    private final Connection connection;
    private final PGConnection pgConnection;
    private final Map<String, NotificationHandler> handlers = new HashMap<>();

    @SneakyThrows
    public NotificationService(ApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        assert jdbcTemplate.getDataSource() != null;
        this.connection = jdbcTemplate.getDataSource().getConnection();
        this.pgConnection = connection.unwrap(PGConnection.class);
        /*
        Map<String,Object> map = applicationContext.getBeansWithAnnotation(Notification.class);
        log.info("Found {} notification handlers", map.size());
        for(String key : map.keySet()) {
            NotificationHandler handler = (NotificationHandler) map.get(key);
            Notification annotation = handler.getClass().getAnnotation(Notification.class);
            log.info("Found notification handler: {} {}", annotation.value(), handler.getClass().getName());
            handlers.put(annotation.value(), handler);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(this);

         */
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
                NotificationHandler handler = handlers.get(notification.getName());
                if(handler != null) {
                    handler.onMessage(notification.getParameter());
                }
            }
        }
    }

}
