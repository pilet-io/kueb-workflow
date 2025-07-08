package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;
import io.pilet.kueb.workflow.common.RunStatus;

@Table(name = "org.kueb", pk = "org,name")
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
        return status == null ? RunStatus.REQUESTED : status;
    }

    public static Kueb of(String org, String name) {
        return new Kueb(org, name, "medium", RunStatus.REQUESTED,
                null, null, null, null);
    }

    public static Kueb of(KuebFull kueb) {
        return new Kueb(kueb.org(), kueb.name(), kueb.profile(), kueb.status(),
                kueb.host(), kueb.dns(), kueb.ip4(), kueb.ip6());
    }
}
