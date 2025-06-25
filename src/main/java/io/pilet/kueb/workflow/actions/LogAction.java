package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.Action;
import io.pilet.kueb.workflow.common.ActionProcessor;
import io.pilet.kueb.workflow.common.ActionResult;
import io.pilet.kueb.workflow.common.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.LinkedList;
import java.util.List;


@Action(name = "log", description = "Log message to output", params = {"message","level:info"})
public class LogAction implements ActionProcessor {
    static final Logger log = LoggerFactory.getLogger(LogAction.class);

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        switch (args.level()) {
            case "error" -> log.error(args.message());
            case "warn" -> log.warn(args.message());
            case "trace" -> log.trace(args.message());
            case "info" -> log.info(args.message());
        }
        logs.add(args.message());
        return new ActionResult(HttpStatus.OK, Params.EMPTY, logs);
    }

    record Args (
            String level,
            String message
    ) {
        public String level() {
            return level == null? "info" : level;
        }
    }
}
