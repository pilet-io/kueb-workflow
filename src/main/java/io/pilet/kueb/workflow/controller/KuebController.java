package io.pilet.kueb.workflow.controller;

import io.pilet.kueb.common.data.Filter;
import io.pilet.kueb.workflow.model.App;
import io.pilet.kueb.workflow.model.Kueb;
import io.pilet.kueb.workflow.model.KuebFull;
import io.pilet.kueb.workflow.model.iam.IamUser;
import io.pilet.kueb.workflow.services.AppService;
import io.pilet.kueb.workflow.services.KuebService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/entity/kuebs")
public class KuebController {
    private final KuebService kuebService;
    private final AppService appService;

    public KuebController(KuebService kuebService, AppService appService) {
        this.kuebService = kuebService;
        this.appService = appService;
    }

    @PostMapping()
    KuebFull createKueb(@RequestBody KuebFull requested, @RequestAttribute IamUser user) {
        KuebFull kueb = KuebFull.of(user.org(), requested.name(), requested.apps());
        kuebService.save(Kueb.of(kueb));
        for(App app : kueb.apps()) {
            appService.save(app);
        }
        return kueb;
    }

    @GetMapping()
    List<Kueb> getKuebs(@RequestAttribute IamUser user) {
        return kuebService.list(Filter.eq("org", user.org()));
    }

    @GetMapping("/{name}")
    KuebFull getKueb(@PathVariable String name, @RequestAttribute IamUser user) {
        return kuebService.get(user.org(), name);
    }

    @DeleteMapping("/{name}")
    void deleteKueb(@PathVariable String name, @RequestAttribute IamUser user) {
        kuebService.delete(user.org(), name);
    }

    // -- App Endpoints

    @PostMapping("/{name}/apps")
    App createApp(@PathVariable String name, @RequestBody App requested, @RequestAttribute IamUser user) {
        App app = App.of(user.org(), name, requested.app());
        return appService.save(app);
    }

    @GetMapping("/{name}/apps")
    List<App> getApps(@PathVariable String name, @RequestAttribute IamUser user) {
        return appService.list(Filter.eq("org", user.org()), Filter.eq("name", name));
    }

    @GetMapping("/{name}/apps/{app}")
    App getApp(@PathVariable String name, @PathVariable String app, @RequestAttribute IamUser user) {
        return appService.get(user.org(), name, app);
    }

    @DeleteMapping("/{name}/apps/{app}")
    void deleteApp(@PathVariable String name, @PathVariable String app, @RequestAttribute IamUser user) {
        appService.delete(user.org(), name, app);
    }

}
