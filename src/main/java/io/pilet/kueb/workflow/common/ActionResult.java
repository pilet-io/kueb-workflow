package io.pilet.kueb.workflow.common;

import io.pilet.kueb.common.util.Params;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.List;

public record ActionResult (
        HttpStatusCode statusCode,
        Object data,
        List<String> logs) {

    public static final ActionResult OK = new ActionResult(HttpStatus.OK, Params.EMPTY, List.of());
}
