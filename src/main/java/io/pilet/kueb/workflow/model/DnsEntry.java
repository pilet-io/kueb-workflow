package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;
import io.pilet.kueb.workflow.common.RunStatus;

@Table(name = "routing.entry", pk = "dns,zone")
public record DnsEntry (
        String id,
        String dns,
        String zone,
        String cname,
        String org,
        String name,
        RunStatus status
) {
}
