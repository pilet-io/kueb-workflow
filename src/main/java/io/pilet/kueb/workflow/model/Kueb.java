package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;
import io.pilet.kueb.common.util.Hash;
import io.pilet.kueb.workflow.common.RunStatus;

@Table(name = "org.kueb")
public record Kueb (
        String id,
        String org,
        String name,
        String product,
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

    public static String id(String org, String name) {
        return Hash.idOf("Ku", 12, org, name);
    }

    public static Kueb of(String org, String name) {
        return new Kueb(id(org,name), org, name, "kueb-medium", "medium", RunStatus.REQUESTED,
                null, null, null, null);
    }

    public static Kueb of(KuebFull kueb) {
        return new Kueb(id(kueb.org(), kueb.name()), kueb.org(), kueb.name(),
                kueb.product(), kueb.profile(), kueb.status(),
                kueb.host(), kueb.dns(), kueb.ip4(), kueb.ip6());
    }
}
