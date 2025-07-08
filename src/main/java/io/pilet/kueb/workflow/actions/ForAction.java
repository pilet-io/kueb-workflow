package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.*;
import io.pilet.kueb.workflow.model.ActionScript;
import org.springframework.http.HttpStatus;

import java.util.LinkedList;
import java.util.List;


@Action(name = "for", description = "For loop", params = {"each,in,run"})
public class ForAction implements ActionProcessor {

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        ActionScript actionScript = ActionScript.of(args.run());
        for(int i=0; i<args.in().length; i++) {
            logs.add("For running " + i + " out of " + args.in().length);
            context.put(args.each(), args.in()[i]);
            context.put("_index", i);
            RunExecutor runExecutor = new RunExecutor(context, actionScript, context.runtime());
            runExecutor.run();
        }
        return new ActionResult(HttpStatus.OK, Params.EMPTY, logs);
    }

    record Args (
            String each,
            Object[] in,
            LinkedList<Params> run
    ) {
    }
}
