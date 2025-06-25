package io.pilet.kueb.common.util;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ShellVar {

    public static String of(String pref, Map<String,Object> data) {
        String prefix = (!pref.isEmpty())? convertName("", pref) + "_" : "";
        Map<String,String> map = mapOf(prefix, data);
        StringBuilder builder = new StringBuilder();
        for(String key : map.keySet() ) {
            builder.append("export ").append(key).append('=')
                    .append('"')
                    .append(map.get(key))
                    .append('"');
            builder.append('\n');
        }
        return builder.toString();
    }

    public static Map<String,String> mapOf(Map<String,Object> data) {
        return mapOf("", data);
    }

    public static Map<String,String> mapOf(String prefix, Map<String,Object> data) {
        Map<String,String> result = new TreeMap<>();
        for(String key : data.keySet()) {
            String varName = convertName(prefix, key);
            Object value = data.get(key);
            if(value instanceof Map) {
                Map<String,String> vars = mapOf(varName + "_", (Map<String, Object>) value);
                result.putAll(vars);
            } else if(value instanceof List) {
                Map<String,String> vars = listOf(varName + "_", (List<Object>) value);
                result.putAll(vars);
            } else
                result.put(varName, data.get(key).toString());
        }
        return result;
    }

    public static Map<String,String> listOf(String prefix, List<Object> list) {
        Map<String,String> result = new TreeMap<>();
        for(int i=0; i<list.size(); i++) {
            Object value = list.get(i);
            if(value instanceof Map) {
                Map<String,String> vars = mapOf(prefix + i + "_", (Map<String, Object>) value);
                result.putAll(vars);
            } else
                result.put(prefix + i, value.toString());
        }
        return result;
    }

    public static String convertName(String prefix, String name) {
        return prefix + name.replaceAll("[\\s.-]","_").toUpperCase();
    }
}
