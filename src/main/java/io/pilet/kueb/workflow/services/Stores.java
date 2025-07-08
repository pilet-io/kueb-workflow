package io.pilet.kueb.workflow.services;

import io.pilet.kueb.common.data.JdbcStoreDriver;
import io.pilet.kueb.common.data.Store;
import io.pilet.kueb.common.data.Table;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class Stores {
    private final static Map<Class<?>, JdbcStoreDriver<?>> driverMap = new HashMap<>();
    private final JdbcTemplate jdbcTemplate;

    public Stores(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        init("io.pilet.kueb.workflow.model");
        init("io.pilet.kueb.workflow.model.iam");
    }

    void init(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Table.class);
        for(Class<?> clazz : classes) {
            driverMap.put(clazz, new JdbcStoreDriver<>(jdbcTemplate, clazz));
        }
    }

    public <T> Store<T> of(Class<T> clazz) {
        return (Store<T>) driverMap.get(clazz);
    }
}
