package io.pilet.kueb.workflow.common;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.pilet.kueb.common.data.Stores;
import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.JsonPathUtils;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.model.ActionScript;
import io.pilet.kueb.workflow.model.RunLog;
import io.pilet.kueb.workflow.model.RunStep;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import static io.pilet.kueb.workflow.common.RunStatus.DONE;
import static io.pilet.kueb.workflow.common.RunStatus.RUNNING;

@Log4j2
public class RunExecutor implements Runnable {
    private static final String DONE_STEP = "done";
    private final ActionScript script;
    private final Tracer tracer;

    public RunExecutor(ActionScript script, Tracer tracer) {
        this.script = script;
        this.tracer = tracer;
    }

    @Override
    public void run() {
        log.info("Executing script: {} {}",
                Span.current().getSpanContext().getTraceId(),
                Span.current().getSpanContext().getSpanId());
        Context context = Context.withArgs(script.args());
        for(ActionItem item : script.run()) {
            runActionItem(context, item);
        }
        saveDone();
    }


    @SneakyThrows
    void runActionItem(Context context, ActionItem item) {
        Span span = newSpan(item.name(), item.args());
        Params args = JsonPathUtils.render(Json.of(context), item.args());
        ActionProcessor processor = item.processorClass().getDeclaredConstructor().newInstance();
        ActionResult result = processor.process(context, Json.of(args));
        context.put(item.name().replace('/', '_'), result.data());
        if(item.as() != null) {
            context.put(item.as(), result.data());
        }
        saveRunning(span, item.name(), args, result);
        span.end();
    }

    void saveRunning(Span span, String name, Params args, ActionResult result) {
        String traceId = span.getSpanContext().getTraceId();
        String spanId = span.getSpanContext().getSpanId();
        RunStep step = new RunStep(traceId, spanId, RUNNING, name, args,
                result.statusCode().value(), result.data(), System.currentTimeMillis());
        Stores.of(RunStep.class).save(step);
        for(int i=0; i<result.logs().size(); i++) {
            String line = result.logs().get(i);
            RunLog log = new RunLog(traceId, spanId, name, i, line);
            Stores.of(RunLog.class).save(log);
        }
    }

    void saveDone() {
        Span span = newSpan(DONE_STEP, Params.EMPTY);
        String traceId = span.getSpanContext().getTraceId();
        String spanId = span.getSpanContext().getSpanId();
        RunStep step = new RunStep(traceId, spanId, DONE, DONE_STEP, Params.EMPTY,
                200, null, System.currentTimeMillis());
        Stores.of(RunStep.class).save(step);
        span.end();
    }


    Span newSpan(String name, Params params) {
        Span span = tracer.spanBuilder(name).startSpan();
        Attributes attributes = OtelUtils.attributesOf(params);
        span.setAllAttributes(attributes);
        log.info("Starting span: {}", span);
        return span;
    }

}
