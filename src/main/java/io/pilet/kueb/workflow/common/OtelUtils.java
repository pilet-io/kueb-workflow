package io.pilet.kueb.workflow.common;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.pilet.kueb.common.util.Params;

public class OtelUtils {

    public static Attributes attributesOf(Params params) {
        AttributesBuilder builder = Attributes.builder();
        if(params != null)
            for(String key : params.keySet()) {
                Object value = params.get(key);
                if(value != null)
                    if(value instanceof Integer) {
                        builder.put(key, (Integer) value);
                    } else if(value instanceof Double) {
                        builder.put(key, (Double) value);
                    } else if(value instanceof Boolean) {
                        builder.put(key, (Boolean) value);
                    } else {
                        builder.put(key, value.toString());
                    }
            }
        return builder.build();
    }
}
