package io.pilet.kueb.common.data;

public interface Lookup<T> {
    T get(String... id);
}
