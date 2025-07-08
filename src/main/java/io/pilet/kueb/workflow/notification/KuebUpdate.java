package io.pilet.kueb.workflow.notification;

import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.Notification;
import io.pilet.kueb.workflow.common.NotificationHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Notification("org_kueb_update")
@Log4j2
public class KuebUpdate implements NotificationHandler {
    @Override
    public void onMessage(String json) {
        Params args = Json.map(json, Params.class);
        //RunDetails runDetails = executorService.execute(args.getString("org"), "org-kueb-insert", args);
        log.info("Kueb Update: {}", args);
    }
}
