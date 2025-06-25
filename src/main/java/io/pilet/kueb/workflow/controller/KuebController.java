package io.pilet.kueb.workflow.controller;

import io.pilet.kueb.common.data.Filter;
import io.pilet.kueb.common.data.Stores;
import io.pilet.kueb.workflow.model.Kueb;
import io.pilet.kueb.workflow.model.iam.IamUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/entity/kuebs")
public class KuebController {

    @PostMapping()
    public Kueb createKueb(@RequestBody Kueb requested, @RequestAttribute IamUser user) {
        Kueb kueb = Kueb.of(user.org(), requested.name());
        Stores.of(Kueb.class).save(kueb);
        return kueb;
    }

    @GetMapping()
    public List<Kueb> getKuebs(@RequestAttribute IamUser user) {
        return Stores.of(Kueb.class).list(Filter.eq("org", user.org()));
    }

    @DeleteMapping("/{name}")
    public void deleteKueb(@PathVariable String name, @RequestAttribute IamUser user) {
        Stores.of(Kueb.class).delete(user.org(), name);
    }

}
