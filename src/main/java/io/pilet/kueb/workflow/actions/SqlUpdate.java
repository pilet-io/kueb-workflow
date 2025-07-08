package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.Action;
import io.pilet.kueb.workflow.common.ActionProcessor;
import io.pilet.kueb.workflow.common.ActionResult;
import io.pilet.kueb.workflow.common.Context;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Action(name = "sql/update", description = "Update in table", params = {"set,in,where"})
@Log4j2
public class SqlUpdate implements ActionProcessor {

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        String comma = args.set().entrySet().stream()
                .map(e -> e.getKey() + "=" + value(e.getValue()) )
                .reduce((a,b) -> a + "," + b).orElse("");
        String query = "UPDATE " + args.in() +
                " SET " + comma +
                " WHERE " + args.where();
        logs.add("Query : " + query);
        log.info("sql/update: {}", query);
        context.runtime().jdbcTemplate().update(query);
        return new ActionResult(HttpStatus.OK, args.set(), logs);
    }

    String value(Object object) {
        return switch(object) {
            case String s -> "'" + object + "'";
            case Map m -> "'" + Json.of(object) + "'";
            default -> object.toString();
        };
    }

    record Args (
            Params set,
            String in,
            String where
    ) {
    }
}
