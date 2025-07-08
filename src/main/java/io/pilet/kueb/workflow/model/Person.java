package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;

@Table(name = "org.people", pk = "org,name")
public record Person (
        String org,
        String name,
        Integer age,
        String zip
) {
}
