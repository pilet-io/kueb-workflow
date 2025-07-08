package io.pilet.kueb.common.util;

import com.jayway.jsonpath.JsonPath;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonPathUtils {

    public static Params render(String json, Map<String,Object> args) {
        Params result = new Params();
        for(String key : args.keySet()) {
            Object value = args.get(key);
            result.put(key, renderObject(json, value));
        }
        return result;
    }

    public static Params render(String json, Map<String,Object> args, String skipKey) {
        Params result = new Params();
        for(String key : args.keySet()) {
            if(skipKey.equals(key)) {
                result.put(key, args.get(key));
            } else {
                Object value = args.get(key);
                result.put(key, renderObject(json, value));
            }
        }
        return result;
    }


    public static Object renderObject(String json, Object value) {
        if(value instanceof String strValue) {
            return render(json, strValue);

        } else if(value instanceof Map) {
            return render(json, (Map<String,Object>) value);

        } else if(value instanceof List) {
            List<Object> objects = (List<Object>) value;
            List<Object> result = new LinkedList<>();
            for(Object obj : objects) {
                Object rendered = renderObject(json, obj);
                result.add( rendered );
            }
            return result;

        } else
            return value;
    }

    public static Object render(String json, String text) {
        List<String> list = split(text);
        if(list.size() == 1 && list.get(0).startsWith("$.")) {
            return JsonPath.read(json, list.get(0));
        }
        StringBuilder builder = new StringBuilder();
        for(String part : list) {
            if(part.startsWith("$."))
                builder.append(JsonPath.read(json, part).toString());
            else
                builder.append(part);
        }
        return builder.toString();
    }

    public static List<String> split(String text) {
        List<String> list = new LinkedList<>();
        int index = 0;
        while(index < text.length()) {
            int next = text.indexOf("$.", index);
            if(next == -1) {
                list.add(text.substring(index));
                break;
            } else {
                String prefix = text.substring(index, next);
                if(!prefix.isEmpty())
                    list.add(prefix);
                index = expressionEnd(text, next);
                String value = text.substring(next, index);
                if(value.indexOf('{') != -1) {
                    list.add(value.replace("{",""));
                    index++;
                } else
                    list.add(value);
            }
        }
        return list;

    }

    static Set<Character> closingChars = Set.of(' ',',','/','\'','}','"',')','\n');
    public static int expressionEnd(String text, int index) {
        for(int pos = index; pos < text.length(); pos++) {
            char ch = text.charAt(pos);
            if(closingChars.contains(ch)) {
                return pos;
            }
        }
        return text.length();
    }

}
