package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.AppVersions;
import io.pilet.kueb.workflow.common.RunStatus;

@Table(name = "org.app", pk = "org,name,app")
public record App (
        String org,
        String name,
        String app,
        String version,
        RunStatus status,
        Params attributes
) {

    public static App of(String org, String name, String app) {
        String version = AppVersions.get(app);
        return new App(org, name, app, version, RunStatus.REQUESTED, Params.EMPTY);
    }
}
