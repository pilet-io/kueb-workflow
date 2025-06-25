package io.pilet.kueb.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;

import java.util.List;

public class Yaml {
    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);


    @SneakyThrows
    public static String of(Object data) {
        return objectMapper.writeValueAsString(data);
    }

    public static String ofJson(String json) {
        if(json == null)
            return null;
        Object data = Json.map(json, Object.class);
        return of(data);
    }

    @SneakyThrows
    public static <T> T map(String yaml, Class<T> tClass) {
        return objectMapper.readValue(yaml, tClass);
    }

    public static ObjectMapper objectMapper() {
        return objectMapper;
    }

    @SneakyThrows
    public static <T> T map(String yaml, TypeReference<T> typeReference) {
        return objectMapper.readValue(yaml, typeReference);
    }

    @SneakyThrows
    public static <T> List<T> mapList(String yaml, Class<T> tClass) {
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, tClass);
        return objectMapper.readValue(yaml, type);
    }

    public static Object mapObject(String yaml) {
        return map(yaml, Object.class);
    }
}
