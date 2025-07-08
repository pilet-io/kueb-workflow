package io.pilet.kueb.workflow.common;

import io.opentelemetry.api.trace.Tracer;
import io.pilet.kueb.workflow.services.Stores;
import org.springframework.jdbc.core.JdbcTemplate;

public class Runtime {
    private final JdbcTemplate jdbcTemplate;
    private final Stores stores;
    private final Tracer tracer;

    public Runtime(JdbcTemplate jdbcTemplate, Stores stores, Tracer tracer) {
        this.jdbcTemplate = jdbcTemplate;
        this.stores = stores;
        this.tracer = tracer;
    }

    public JdbcTemplate jdbcTemplate() {
        return jdbcTemplate;
    }

    public Stores stores() {
        return stores;
    }

    public Tracer tracer() {
        return tracer;
    }
}
