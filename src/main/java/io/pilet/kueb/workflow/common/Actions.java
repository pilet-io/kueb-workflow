package io.pilet.kueb.workflow.common;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Actions {
    private final static Map<String, Class<? extends ActionProcessor>> actionMap = new HashMap<>();

    public static void scanActions(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Action.class);
        for(Class<?> clazz : classes) {
            Action action = clazz.getAnnotation(Action.class);
            Class<? extends ActionProcessor> processorClass = (Class<? extends ActionProcessor>) clazz;
            actionMap.put(action.name(), processorClass);
        }
    }

    public static boolean exists(String name) {
        return actionMap.containsKey(name);
    }

    public static Class<? extends ActionProcessor> get(String name) {
        return actionMap.get(name);
    }

}
