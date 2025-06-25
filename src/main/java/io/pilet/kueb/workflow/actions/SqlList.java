package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.Action;
import io.pilet.kueb.workflow.common.ActionProcessor;
import io.pilet.kueb.workflow.common.ActionResult;
import io.pilet.kueb.workflow.common.Context;
import io.pilet.kueb.workflow.common.Runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


@Action(name = "sql/list", description = "Select from Table", params = {"select"})
public class SqlList implements ActionProcessor, RowMapper<Params> {
    static final Logger log = LoggerFactory.getLogger(SqlList.class);

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        String query = "SELECT row_to_json(t) FROM (SELECT " + args.select() + ") t";
        List<Params> list = Runtime.jdbcTemplate().query(query, this);
        logs.add("Query : " + args.select());
        logs.add("Data size : " + list.size());
        return new ActionResult(HttpStatus.OK, list, logs);
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
