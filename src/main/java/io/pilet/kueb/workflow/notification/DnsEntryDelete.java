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
@Notification("dns_entry_delete")
@Log4j2
public class DnsEntryDelete implements NotificationHandler {
    private final RunExecutorService executorService;

    public DnsEntryDelete(RunExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void onMessage(String json) {
        Params args = Json.map(json, Params.class);
        RunDetails runDetails = executorService.async(args.getString("org"), "dns-entry-delete", args);
        log.info("Dns Delete Run : {}", runDetails);
    }
}
