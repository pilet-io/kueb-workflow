package io.pilet.kueb.workflow.notification;

import io.pilet.kueb.workflow.common.Notification;
import io.pilet.kueb.workflow.common.NotificationHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Notification("org_app_insert")
@Log4j2
public class AppInsert implements NotificationHandler {

    @Override
    public void onMessage(String json) {
        log.info("App Insert Received: {}", json);
    }
}
