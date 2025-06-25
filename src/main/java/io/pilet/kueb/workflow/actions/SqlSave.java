package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.*;
import io.pilet.kueb.workflow.common.Runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.LinkedList;
import java.util.List;


@Action(name = "sql/save", description = "Insert a single row into table", params = {"insert,into"})
public class SqlSave implements ActionProcessor {
    static final Logger log = LoggerFactory.getLogger(SqlSave.class);

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        String json = Json.of(args.insert());
        StringBuilder sql = new StringBuilder();
        String table = args.into();
        sql.append("INSERT INTO ").append(table)
                .append(" SELECT * FROM json_populate_record(NULL::").append(table)
                .append(", ?::json)").append(" ON CONFLICT DO NOTHING");
        String query = sql.toString();
        Runtime.jdbcTemplate().update(query, json);
        logs.add("Query : " + query);
        logs.add("Data : " + json);
        return new ActionResult(HttpStatus.OK, Params.EMPTY, logs);
    }

    record Args (
            Params insert,
            String into
    ) {
    }
}
