package io.pilet.kueb.common.util;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Log4j2
public class Folder {

    @SneakyThrows
    public static void scan(String path, String endsWith, Consumer<KeyValue> consumer) {
        Files.walk(Paths.get(path))
                .filter(p -> p.toString().endsWith(endsWith))
                .peek(p -> {
                    try {
                        String key = p.toString()
                                .replace(endsWith,"")
                                .replace(path + "/","");
                        String value = Files.readString(p);
                        consumer.accept(new KeyValue(key, value));
                    } catch (Exception e) {
                        log.error(e);
                    }
                }).count();
    }

    @SneakyThrows
    public static List<KeyValue> load(String path, String endsWith) {
        return Files.walk(Paths.get(path))
                .filter(p -> p.toString().endsWith(endsWith))
                .map(p -> {
                    try {
                        String key = p.toString()
                                .replace(endsWith,"")
                                .replace(path + "/","");
                        String value = Files.readString(p);
                        return new KeyValue(key, value);
                    } catch (Exception e) {
                        log.error(e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
