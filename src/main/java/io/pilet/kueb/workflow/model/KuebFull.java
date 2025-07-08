package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.RunStatus;

import java.util.ArrayList;
import java.util.List;

@Table(name = "org.view_kueb_full", pk = "org,name")
public record KuebFull (
        String org,
        String name,
        String profile,
        RunStatus status,
        String host,
        String dns,
        String ip4,
        String ip6,
        List<App> apps,
        Params attributes
) {
    public String profile() {
        return profile == null ? "medium" : profile;
    }

    public RunStatus status() {
        return status == null ? RunStatus.REQUESTED : status;
    }

    public static KuebFull of(String org, String name, List<App> apps) {
        List<App> appList = new ArrayList<>();
        if(apps != null && !apps.isEmpty()) {
            for(App app : apps) {
                App newApp = App.of(org, name, app.app());
                appList.add(newApp);
            }
        }
        return new KuebFull(org, name, "medium", RunStatus.REQUESTED,
                null, null, null, null, appList, Params.EMPTY);
    }
}
