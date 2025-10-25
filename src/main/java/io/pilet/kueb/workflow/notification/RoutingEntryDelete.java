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
@Notification("routing_entry_delete")
@Log4j2
public class RoutingEntryDelete implements NotificationHandler {
    private final RunExecutorService executorService;

    public RoutingEntryDelete(RunExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void onMessage(String json) {
        Params args = Json.map(json, Params.class);
        RunDetails runDetails = executorService.async(args.getString("org"), "routing-entry-delete", args);
        log.info("Route Delete Run : {}", runDetails);
    }
}
