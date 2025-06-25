package io.pilet.kueb.workflow.common;

public record EntityEvent<T> (
        String table,
        EntityMethod method,
        T data
) {
}
