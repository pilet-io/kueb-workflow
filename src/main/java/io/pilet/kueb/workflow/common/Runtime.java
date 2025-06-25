package io.pilet.kueb.workflow.common;

import io.pilet.kueb.workflow.services.RunExecutorService;
import org.springframework.jdbc.core.JdbcTemplate;

public class Runtime {
    private static JdbcTemplate jdbcTemplate;
    private static RunExecutorService executorService;

    public static void init(JdbcTemplate jdbcTemplate, RunExecutorService executorService) {
        Runtime.jdbcTemplate = jdbcTemplate;
        Runtime.executorService = executorService;
    }

    public static JdbcTemplate jdbcTemplate() {
        return jdbcTemplate;
    }

    public static RunExecutorService executorService() {
        return executorService;
    }
}
