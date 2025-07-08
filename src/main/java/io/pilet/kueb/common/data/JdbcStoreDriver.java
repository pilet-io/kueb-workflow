package io.pilet.kueb.common.data;

import io.pilet.kueb.common.util.Json;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Log4j2
public class JdbcStoreDriver<T> implements Store<T>, RowMapper<T> {
    private final JdbcTemplate jdbcTemplate;
    private final Class<T> tClass;
    private final Table table;
    private final String tableName;
    private final String pk;
    private final String onConflict;

    public JdbcStoreDriver(JdbcTemplate jdbcTemplate,
                           Class<T> tClass) {
        this.jdbcTemplate = jdbcTemplate;
        this.tClass = tClass;
        table = tClass.getAnnotation(Table.class);
        this.tableName = table.name();
        String[] parts = table.pk().split(",");
        StringBuilder pkBuilder = new StringBuilder();
        for(int i=0; i<parts.length; i++) {
            if(i != 0)
                pkBuilder.append(" and ");
            pkBuilder.append(parts[i]).append(" = ?");
        }
        this.pk = pkBuilder.toString();
        if(table.update().isEmpty()) {
            this.onConflict = " ON CONFLICT DO NOTHING";
        } else {
            parts = table.update().split(",");
            StringBuilder builder = new StringBuilder();
            for(int i=0; i<parts.length; i++) {
                builder.append(parts[i]);
                builder.append(" = EXCLUDED.");
                builder.append(parts[i]);
                if(i != parts.length-1)
                    builder.append(", ");
            }
            this.onConflict = " ON CONFLICT (" + table.pk() + ") DO UPDATE SET " + builder;
        }
        Lookups.register(tClass, this, table.cached());
    }

    @Override
    public T save(T data) {
        if(data instanceof Initialized) {
            ((Initialized) data).init();
        }
        String json = Json.of(data);
        String sql = "INSERT INTO " + tableName +
                " SELECT * FROM json_populate_record(NULL::" +
                tableName +
                ", ?::json)" +
                onConflict;
        jdbcTemplate.update(sql, json);
        return data;
    }

    @Override
    public void delete(String... id) {
        T data = get(id);
        String sql = "DELETE FROM " + tableName + " WHERE " + pk;
        jdbcTemplate.update(sql, (Object[]) id);
    }

    @Override
    public List<T> list(Filter... filters) {
        String comma = "";
        StringBuilder where = new StringBuilder();
        for(Filter filter : filters) {
            where.append(comma);
            where.append(filter.toSQL());
            comma = " AND ";
        }
        String sql = "SELECT row_to_json(" + tableName + ".*) json FROM " + tableName
                + " WHERE " + where;
        if(!table.sort().isEmpty()) {
            sql = sql + " ORDER BY " + table.sort();
        }
        sql = sql + " LIMIT " + table.limit();
        log.info("SQL: {}", sql);
        return jdbcTemplate.query(sql, this);
    }

    @Override
    public T get(String... id) {
        String sql = "SELECT row_to_json(" + tableName + ".*) json FROM " + tableName + " WHERE " + pk;
        log.info("SQL: {} {}", sql, id);
        return jdbcTemplate.queryForObject(sql, this, (Object[]) id);
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Json.map(rs.getString(1), tClass);
    }

}
