package io.pilet.kueb.workflow.common;

import java.util.HashMap;
import java.util.Map;

public class AppVersions {
    private static final Map<String,String> versions = new HashMap<>();

    public static void register(String name, String version) {
        versions.put(name, version);
    }

    public static String get(String name) {
        String version = versions.get(name);
        if(version == null) {
            throw new ProcessingException(ErrorCode.SYSTEM_CONFIG_ERROR, "Missing app version for " + name);
        }
        return version;
    }

}
