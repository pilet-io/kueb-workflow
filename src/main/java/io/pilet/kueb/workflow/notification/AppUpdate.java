package io.pilet.kueb.workflow.notification;

import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.Notification;
import io.pilet.kueb.workflow.common.NotificationHandler;
import io.pilet.kueb.workflow.model.RunDetails;
import io.pilet.kueb.workflow.services.RunExecutorService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Notification("org_app_update")
@Log4j2
public class AppUpdate implements NotificationHandler {
    private final RunExecutorService executorService;

    public AppUpdate(RunExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void onMessage(String json) {
        log.info("App Update Received: {}", json);
        Params args = Json.map(json, Params.class);
        if(args.containsKey("status") && args.get("status").equals("RUNNING")) {
            RunDetails runDetails = executorService.async(args.getString("org"), "org-app-update", args);
            log.info("App-Update: {}", runDetails);

        }

    }
}
