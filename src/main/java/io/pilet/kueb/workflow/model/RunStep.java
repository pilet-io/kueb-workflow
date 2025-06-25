package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.RunStatus;

@Table(name = "workflow.step", pk = "trace,span")
public record RunStep (
        String trace,
        String span,
        RunStatus status,
        String name,
        Params params,
        Integer result,
        Object data,
        Long created
) {
}
