package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;

@Table(name = "dns.zone", pk = "zone", cached = true)
public record DnsZone (
        String id,
        String zone,
        String status
) {
}
