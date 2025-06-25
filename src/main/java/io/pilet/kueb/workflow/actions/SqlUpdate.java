package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.Action;
import io.pilet.kueb.workflow.common.ActionProcessor;
import io.pilet.kueb.workflow.common.ActionResult;
import io.pilet.kueb.workflow.common.Context;
import io.pilet.kueb.workflow.common.Runtime;
import org.springframework.http.HttpStatus;

import java.util.LinkedList;
import java.util.List;


@Action(name = "sql/update", description = "Update in table", params = {"set,in,where"})
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
        Runtime.jdbcTemplate().update(query);
        return new ActionResult(HttpStatus.OK, args.set(), logs);
    }

    String value(Object object) {
        return object instanceof String? "'" + object + "'" : object.toString();
    }

    record Args (
            Params set,
            String in,
            String where
    ) {
    }
}
