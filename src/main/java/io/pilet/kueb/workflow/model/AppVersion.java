package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;

@Table(name = "org.app_version", pk = "app,version")
public record AppVersion (
        String app,
        String version,
        String preferred
) {
}
