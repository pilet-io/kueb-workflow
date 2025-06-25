package io.pilet.kueb.common.data;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Stores {
    private final static Map<Class<?>,JdbcStoreDriver<?>> driverMap = new HashMap<>();

    public static void init(KafkaTemplate<String,String> kafkaTemplate,
                            JdbcTemplate jdbcTemplate,
                            String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Table.class);
        for(Class<?> clazz : classes) {
            driverMap.put(clazz, new JdbcStoreDriver<>(kafkaTemplate, jdbcTemplate, clazz));
        }
    }

    public static <T> Store<T> of(Class<T> clazz) {
        return (Store<T>) driverMap.get(clazz);
    }
}
