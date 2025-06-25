package io.pilet.kueb.workflow.common;



public interface ActionProcessor {
    ActionResult process(Context context, String argJson);
}
