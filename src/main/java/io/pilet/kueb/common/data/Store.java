package io.pilet.kueb.common.data;

import java.util.List;

public interface Store<T> extends Lookup<T> {
    T save(T data);
    void delete(String... id);
    List<T> list(Filter... filters);
    default boolean exists(String... id) {
        try {
            T data = get(id);
            return data != null;
        } catch (Exception e) {
            return false;
        }
    }
}
