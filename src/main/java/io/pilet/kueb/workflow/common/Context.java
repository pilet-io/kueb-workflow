package io.pilet.kueb.workflow.common;

import io.pilet.kueb.common.util.Params;

public class Context extends Params {

    public static Context withArgs(Params args) {
        Context context = new Context();
        context.put("args", args);
        return context;
    }
}
