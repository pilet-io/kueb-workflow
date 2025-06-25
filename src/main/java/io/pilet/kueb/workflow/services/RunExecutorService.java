package io.pilet.kueb.workflow.services;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.pilet.kueb.common.data.Stores;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.RunExecutor;
import io.pilet.kueb.workflow.common.RunStatus;
import io.pilet.kueb.workflow.model.ActionScript;
import io.pilet.kueb.workflow.model.RunDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Log4j2
public class RunExecutorService {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final ExecutorService otelExecutor = Context.taskWrapping(executor);
    private final Tracer tracer;

    public RunExecutorService(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("run-executor-service");
    }

    public void execute(RunDetails details) {
        ActionScript script = Stores.of(ActionScript.class).get(details.name());
        ActionScript executeScript = new ActionScript(script.name(), script.description(), details.args(), null, script.run());
        RunExecutor runExecutor = new RunExecutor(executeScript, tracer);
        otelExecutor.execute(runExecutor);
    }

    public RunDetails execute(String org, String name, Params args) {
        Span span = tracer.spanBuilder(name).setNoParent().startSpan();
        try (Scope ignored = span.makeCurrent()) {
            return run(org, name, args);
        }
    }

    public RunDetails run(String org, String name, Params args) {
        String traceId = Span.current().getSpanContext().getTraceId();
        log.info("Executing script: {} {}", traceId, name);
        Long now = System.currentTimeMillis();
        RunDetails details = new RunDetails(org, traceId, RunStatus.REQUESTED,
                name, args, now, now);
        log.info("Saving RunDetails: {}", details);
        Stores.of(RunDetails.class).save(details);
        execute(details);
        return details;
    }
}
