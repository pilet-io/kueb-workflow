package io.pilet.kueb.workflow.services;

import com.fasterxml.jackson.core.type.TypeReference;
import io.pilet.kueb.common.data.Stores;
import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.EntityEvent;
import io.pilet.kueb.workflow.config.KafkaConfig;
import io.pilet.kueb.workflow.model.ActionScript;
import io.pilet.kueb.workflow.model.RunDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class KafkaService {
    private final RunExecutorService runExecutorService;

    public KafkaService(RunExecutorService runExecutorService) {
        this.runExecutorService = runExecutorService;
    }

    @KafkaListener(topics = KafkaConfig.ENTITY_TOPIC, groupId = "event_listener")
    void listen(String json) {
        try {
            EntityEvent<Params> entityEvent = Json.map(json, new TypeReference<>() {});
            log.info("Entity Event Received: {}", entityEvent);
            String org = entityEvent.data().containsKey("org") ? entityEvent.data().getString("org") : "system";
            String flowName = entityEvent.table().replace('.', '-') + "-" + entityEvent.method().name().toLowerCase();
            ActionScript script = Stores.of(ActionScript.class).get(flowName);
            if(script == null) {
                log.info("Workflow not found, skipping: {}", flowName);
                return;
            }
            log.info("Running Workflow: {} {} {}", org, flowName, entityEvent.data());
            RunDetails runDetails = runExecutorService.execute(org, flowName, entityEvent.data());
            log.info("RunDetails: {}", runDetails);
        } catch (Exception e) {
            log.error("Error processing Entity Event", e);
        }
    }



}
