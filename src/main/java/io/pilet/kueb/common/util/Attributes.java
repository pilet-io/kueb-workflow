package io.pilet.kueb.common.util;

import java.util.TreeMap;

public class Attributes extends TreeMap<String,String> {
    public static final Attributes EMPTY = new Attributes();

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public int getInt(String key, int def) {
        return containsKey(key)? getInt(key) : def;
    }

    public static Attributes of(String key, Object value) {
        Attributes params = new Attributes();
        if(value != null)
            params.put(key, value.toString());
        return params;
    }

    public Attributes append(String key, Object value) {
        if(value != null)
            put(key, value.toString());
        return this;
    }

}
