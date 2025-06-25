package io.pilet.kueb.common.data;

import java.util.List;

public interface Store<T> extends Lookup<T> {
    T save(T data);
    void delete(String... id);
    List<T> list(Filter... filters);
}
