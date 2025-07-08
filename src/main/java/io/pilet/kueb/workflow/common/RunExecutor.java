package io.pilet.kueb.workflow.common;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
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
    private final Runtime runtime;
    private final Context context;

    public RunExecutor(Context context, ActionScript script, Runtime runtime) {
        this.script = script;
        this.runtime = runtime;
        this.context = context;
    }

    public RunExecutor(ActionScript script, Runtime runtime) {
        this.script = script;
        this.runtime = runtime;
        this.context = Context.withArgs(runtime, script.args());
    }


    @Override
    public void run() {
        for(ActionItem item : script.run()) {
            runActionItem(context, item);
        }
        saveDone();
    }


    @SneakyThrows
    void runActionItem(Context context, ActionItem item) {
        Span span = newSpan(item.name(), item.args());
        Params args = JsonPathUtils.render(Json.of(context), item.args(), "run");
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
        runtime.stores().of(RunStep.class).save(step);
        for(int i=0; i<result.logs().size(); i++) {
            String line = result.logs().get(i);
            RunLog log = new RunLog(traceId, spanId, name, i, line);
            runtime.stores().of(RunLog.class).save(log);
        }
    }

    void saveDone() {
        Span span = newSpan(DONE_STEP, Params.EMPTY);
        String traceId = span.getSpanContext().getTraceId();
        String spanId = span.getSpanContext().getSpanId();
        RunStep step = new RunStep(traceId, spanId, DONE, DONE_STEP, Params.EMPTY,
                200, null, System.currentTimeMillis());
        runtime.stores().of(RunStep.class).save(step);
        span.end();
    }


    Span newSpan(String name, Params params) {
        Span span = runtime.tracer().spanBuilder(name).startSpan();
        Attributes attributes = OtelUtils.attributesOf(params);
        span.setAllAttributes(attributes);
        return span;
    }

}
