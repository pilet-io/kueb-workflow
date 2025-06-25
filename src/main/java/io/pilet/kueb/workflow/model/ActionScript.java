package io.pilet.kueb.workflow.model;

import io.pilet.kueb.common.data.Table;
import io.pilet.kueb.common.util.Params;
import io.pilet.kueb.workflow.common.ActionItem;
import io.pilet.kueb.workflow.common.RawScript;

import java.util.List;

@Table(name = "workflow.definition", pk = "name", update = "args,run,yaml")
public record ActionScript (
        String name,
        String description,
        Params args,
        String yaml,
        List<ActionItem> run
) {
    public static ActionScript of(RawScript rawScript, String yaml) {
        List<ActionItem> items = rawScript.run().stream().map(ActionItem::of).toList();
        return new ActionScript(rawScript.name(), rawScript.description(), rawScript.args(), yaml, items);
    }

}
