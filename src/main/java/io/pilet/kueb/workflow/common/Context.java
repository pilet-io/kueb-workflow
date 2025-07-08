package io.pilet.kueb.workflow.common;

import io.pilet.kueb.common.util.Params;

public class Context extends Params {
    private final Runtime runtime;

    public Context(Runtime runtime) {
        this.runtime = runtime;
    }

    public Runtime runtime() {
        return runtime;
    }

    public static Context withArgs(Runtime runtime, Params args) {
        Context context = new Context(runtime);
        context.put("args", args);
        return context;
    }

}
