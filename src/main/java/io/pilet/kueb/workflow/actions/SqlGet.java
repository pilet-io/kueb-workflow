package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.*;
import io.pilet.kueb.workflow.common.Runtime;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


@Action(name = "sql/get", description = "Select a single row from a table", params = {"select"})
public class SqlGet implements ActionProcessor, RowMapper<Params> {

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        String query = "SELECT row_to_json(t) FROM (SELECT " + args.select() + ") t";
        Params params = Runtime.jdbcTemplate().queryForObject(query, this);
        logs.add("Query : " + args.select());
        logs.add("Data : " + params);
        return new ActionResult(HttpStatus.OK, params, logs);
    }

    @Override
    public Params mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Json.map(rs.getString(1), Params.class);
    }

    record Args (
            String select
    ) {
    }
}
