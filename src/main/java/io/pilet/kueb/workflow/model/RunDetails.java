package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.RunStatus;

@Table(name = "workflow.run", pk = "trace", sort = "created desc")
public record RunDetails (
        String org,
        String trace,
        RunStatus status,
        String name,
        Params args,
        Long created,
        Long updated
) {
}
