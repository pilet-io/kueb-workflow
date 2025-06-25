package io.pilet.kueb.workflow.actions;


import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.workflow.common.*;
import io.pilet.kueb.workflow.common.Runtime;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Action(name = "sql/map", description = "Select a map from Table", params = {"select"})
public class SqlMap implements ActionProcessor, RowMapper<SqlMap.Pair> {

    @Override
    public ActionResult process(Context context, String argsJson) {
        Args args = Json.map(argsJson, Args.class);
        List<String> logs = new LinkedList<>();
        String query = "SELECT " + args.select();
        List<Pair> list = Runtime.jdbcTemplate().query(query, this);
        Map<String,Object> map = new LinkedHashMap<>();
        for(Pair pair : list) {
            map.put(pair.key(), pair.value());
        }
        logs.add("Query : " + args.select());
        logs.add("Map size : " + map.size());
        return new ActionResult(HttpStatus.OK, map, logs);
    }

    @Override
    public Pair mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Pair(rs.getString(1), rs.getObject(2));
    }

    record Args (
            String select
    ) {}

    record Pair (
            String key,
            Object value
    ) {
    }
}
