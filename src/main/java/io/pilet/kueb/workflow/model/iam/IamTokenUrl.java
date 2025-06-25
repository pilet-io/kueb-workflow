package io.pilet.kueb.workflow.model.iam;

import io.pilet.kueb.common.data.Table;

import java.util.Set;

@Table(name = "iam.token_url", pk = "token,url", cached = true)
public record IamTokenUrl (
        String token,
        String url,
        Set<IamMethod> permissions
) {
}
