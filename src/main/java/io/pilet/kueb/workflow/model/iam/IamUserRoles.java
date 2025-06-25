package io.pilet.kueb.workflow.model.iam;

import io.pilet.kueb.common.data.Table;

@Table(name = "iam.user_roles", pk = "email,role")
public record IamUserRoles (
        String email,
        String role
) {
}
