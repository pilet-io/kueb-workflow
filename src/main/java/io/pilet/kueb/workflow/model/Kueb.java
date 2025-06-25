package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;
import io.pilet.kueb.workflow.common.RunStatus;

@Table(name = "org.kueb", pk = "org,name", async = true)
public record Kueb (
        String org,
        String name,
        String profile,
        RunStatus status,
        String host,
        String dns,
        String ip4,
        String ip6
) {
    public String profile() {
        return profile == null ? "medium" : profile;
    }

    public RunStatus status() {
        return status == null ? RunStatus.RUNNING : status;
    }

    public static Kueb of(String org, String name) {
        return new Kueb(org, name, "medium", RunStatus.RUNNING,
                null, null, null, null);
    }
}
