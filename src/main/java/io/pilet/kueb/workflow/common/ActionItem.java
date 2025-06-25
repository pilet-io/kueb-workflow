package io.pilet.kueb.workflow.common;

import io.pilet.kueb.common.util.Json;
import io.pilet.kueb.common.util.Params;

public record ActionItem (
        String name,
        Class<? extends ActionProcessor> processorClass,
        Params args,
        String as
) {

    public static ActionItem of(Params rawItem) {
        String as = rawItem.getString("as");
        rawItem.remove("as");
        if(rawItem.size() != 1)
            throw new ProcessingException(ErrorCode.WRONG_ACTION_CONFIG, Json.of(rawItem));
        String name = rawItem.keySet().stream().findFirst().get();
        if(!Actions.exists(name))
            throw new ProcessingException(ErrorCode.UNKNOWN_ACTION_NAME, name);

        return new ActionItem ( name, Actions.get(name),
                Params.of(rawItem.getMap(name)), as);
    }
}
