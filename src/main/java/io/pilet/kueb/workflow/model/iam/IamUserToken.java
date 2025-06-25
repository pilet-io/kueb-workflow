package io.pilet.kueb.workflow.model.iam;

import io.pilet.kueb.common.data.Table;

@Table(name = "iam.users", pk = "token", cached = true)
public record IamUserToken (
        String email,
        String name,
        String org,
        String token
) {
}
