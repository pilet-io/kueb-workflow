package io.pilet.kueb.workflow.controller;

import io.pilet.kueb.workflow.services.Stores;
import io.pilet.kueb.common.util.Yaml;
import io.pilet.kueb.workflow.common.RawScript;
import io.pilet.kueb.workflow.model.ActionScript;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/workflow/definitions")
public class DefinitionController {
    private final Stores stores;

    public DefinitionController(Stores stores) {
        this.stores = stores;
    }

    @PostMapping()
    public CreateDefinitionResponse createDefinition(@RequestBody String yaml) {
        RawScript rawScript = Yaml.map(yaml, RawScript.class);
        ActionScript actionScript = ActionScript.of(rawScript, yaml);
        stores.of(ActionScript.class).save(actionScript);
        return new CreateDefinitionResponse(actionScript.name(), actionScript.description());
    }

    @GetMapping(value = "/{name}", produces = "application/yaml")
    public String getDefinition(@PathVariable String name) {
        ActionScript actionScript = stores.of(ActionScript.class).get(name);
        return actionScript.yaml();
    }


    public record CreateDefinitionResponse (
            String name,
            String description
    ) {
    }

}