package io.pilet.kueb.common.util;

import java.util.*;

import static java.lang.Character.isLowerCase;

public class Params extends LinkedHashMap<String,Object> {
    public static final Params EMPTY = new Params();

    public int getInt(String key) {
        Object value = get(key);
        return (value instanceof String)? Integer.parseInt(value.toString()) : (int) value;
    }

    public int getInt(String key, int def) {
        return (int) computeIfAbsent(key, k -> def);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public <T> T get(String key, T def, Class<T> tClass) {
        return containsKey(key)? (T) get(key) : def;
    }

    public <T> List<T> getList(String key, Class<T> tClass) {
        Object value = get(key);
        if(value instanceof List)
            return (List<T>) get(key);
        else
            return convertToList(key, tClass);
    }

    public <T> List<T> convertToList(String key, Class<T> tClass) {
        if(containsKey(key)) {
            String json = Json.of(get(key));
            return Json.mapList(json, tClass);
        } else return new LinkedList<>();
    }

    public String getString(String key, String def) {
        return (String) computeIfAbsent(key, k -> def);
    }


    public Map<String,Object> getMap(String key) {
        if(get(key) instanceof Map)
            return (Map<String,Object>) get(key);
        else return new HashMap<>();
    }

    public <T> T get(String key, Class<T> tClass) {
        return (T) get(key);
    }

    public Params getParams(String key) {
        return Params.of(getMap(key));
    }

    public boolean valueIs(String key, Object value) {
        return containsKey(key) && value.equals(get(key));
    }

    public static Params of(String key, Object value) {
        Params params = new Params();
        params.put(key, value);
        return params;
    }

    public static Params of(Map<String,Object> map) {
        if(map == null)
            return null;
        Params params = new Params();
        params.putAll(map);
        return params;
    }

    public static Params ofMap(Map<String,String> map) {
        if(map == null)
            return Params.EMPTY;
        Params params = new Params();
        params.putAll(map);
        return params;
    }

    public Params append(String key, Object value) {
        put(key, value);
        return this;
    }


    public Params sorted() {
        TreeMap<String, Object> treeMap = new TreeMap<>(this);
        Params result = new Params();
        result.putAll(treeMap);
        return result;
    }

    public static Params varOf(Params scope) {
        Params params = new Params();
        for(String key : scope.keySet()) {
            params.put(varName(key), scope.get(key));
        }
        return params;
    }

    public static String varName(String name) {
        StringBuilder underscore = new StringBuilder();
        underscore.append(name.charAt(0));
        for (int i = 1; i < name.length(); i++)
            underscore.append( isLowerCase(name.charAt(i)) ?
                    name.charAt(i) :
                    "_" + name.charAt(i) );
        return underscore.toString().toUpperCase();
    }

    public boolean getBoolean(String key) {
        return getString(key, "false").equals("true");
    }
}
