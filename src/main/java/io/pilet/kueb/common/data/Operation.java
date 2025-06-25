package io.pilet.kueb.common.data;

import java.util.function.BiFunction;

public enum Operation {
    EQ((f,v) -> f + " = " + '\'' + v + '\''),
    NEQ((f,v) -> f + " != " + '\'' + v + '\''),
    LIKE((f,v) -> f + " like " + '\'' + v + '\''),
    GT((f,v) -> f + " > " +  v ),
    LT((f,v) -> f + " < " + v);
    private final BiFunction<String,String,String> format;

    Operation(BiFunction<String, String, String> format) {
        this.format = format;
    }

    public String format(Filter filter) {
        return format.apply(filter.field(), filter.value());
    }

    public String format(String field, String value) {
        return format.apply(field, value);
    }
}
