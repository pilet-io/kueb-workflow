package io.pilet.kueb.workflow.model.iam;

import io.pilet.kueb.common.data.Table;

@Table(name = "iam.users", pk = "email", cached = true)
public record IamUser (
        String email,
        String name,
        String org,
        String token
) {
}
