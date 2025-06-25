package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.workflow.common.Action;
import io.pilet.kueb.workflow.common.ActionProcessor;
import io.pilet.kueb.workflow.common.ActionResult;
import io.pilet.kueb.workflow.common.Context;
import org.springframework.http.HttpStatus;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


@Action(name = "random", description = "Random pick from an array", params = {"from"})
public class RandomAction implements ActionProcessor {

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        Random random = new Random();
        int index = random.nextInt(args.from().length);
        Object data = args.from()[index];
        logs.add("Picked " + index + " : " + data);
        return new ActionResult(HttpStatus.OK, data, logs);
    }

    record Args (
            Object[] from
    ) {
    }
}
