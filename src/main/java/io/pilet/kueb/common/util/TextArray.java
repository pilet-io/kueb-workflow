package io.pilet.kueb.common.util;

import java.util.*;

public class TextArray {
    List<String> list = new LinkedList<>();

    public TextArray append(String str) {
        list.add(str);
        return this;
    }

    public static TextArray split(String str, String delimiter) {
        TextArray textArray = new TextArray();
        return textArray.append(str, delimiter);
    }

    public TextArray append(String str, String delimiter) {
        String[] parts = str.split(delimiter);
        Collections.addAll(list, parts);
        return this;
    }

    public String toSql() {
        Set<String> nameSet = new LinkedHashSet<>(list);
        String comma = String.join("\",\"", nameSet);
        return "{\"" + comma + "\"}";
    }

    public String get(int index) {
        return list.get(index);
    }

    public String[] toArray() {
        return list.toArray(new String[list.size()]);
    }

}
