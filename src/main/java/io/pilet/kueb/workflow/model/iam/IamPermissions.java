package io.pilet.kueb.workflow.model.iam;

import io.pilet.kueb.common.data.Table;

import java.util.Set;

@Table(name = "iam.permissions", pk = "url,role")
public record IamPermissions (
        String url,
        String role,
        Set<IamMethod> permissions
) {
}
