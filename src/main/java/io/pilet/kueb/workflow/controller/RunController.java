package io.pilet.kueb.workflow.controller;

import io.pilet.kueb.common.data.Filter;
import io.pilet.kueb.workflow.model.RunDetails;
import io.pilet.kueb.workflow.model.iam.IamUser;
import io.pilet.kueb.workflow.services.RunExecutorService;
import io.pilet.kueb.workflow.services.Stores;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/workflow/runs")
public class RunController {
    private final RunExecutorService executorService;
    private final Stores stores;

    public RunController(RunExecutorService executorService, Stores stores) {
        this.executorService = executorService;
        this.stores = stores;
    }

    @PostMapping("")
    RunDetails createRun(@RequestBody RunDetails request, @RequestAttribute IamUser user) {
        return executorService.async(user.org(), request.name(), request.args());
    }

    @GetMapping("/{trace}")
    RunDetails getRun(@PathVariable String trace) {
        return stores.of(RunDetails.class).get(trace);
    }

    @GetMapping()
    List<RunDetails> getRuns(@RequestAttribute IamUser user) {
        return stores.of(RunDetails.class).list(Filter.eq("org", user.org()));
    }
}
