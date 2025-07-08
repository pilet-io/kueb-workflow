package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;

@Table(name = "org.car", pk = "org,name,model")
public record Car (
        String org,
        String name,
        String model,
        Integer year
) {
}
