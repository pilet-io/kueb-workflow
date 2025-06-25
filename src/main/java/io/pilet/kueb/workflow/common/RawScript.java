package io.pilet.kueb.workflow.common;

import io.pilet.kueb.common.util.Params;

import java.util.LinkedList;

public record RawScript (
        String name,
        String description,
        Params args,
        LinkedList<Params> run
)  {
}
