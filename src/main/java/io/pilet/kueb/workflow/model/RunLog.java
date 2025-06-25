package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;

@Table(name = "workflow.log", pk = "trace,span")
public record RunLog (
        String trace,
        String span,
        String command,
        Integer number,
        String line
) {
}
