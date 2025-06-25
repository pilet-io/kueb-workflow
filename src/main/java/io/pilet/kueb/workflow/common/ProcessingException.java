package io.pilet.kueb.workflow.common;

import io.pilet.kueb.common.util.Params;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProcessingException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Params details;

    public ProcessingException(Throwable cause, ErrorCode errorCode, Params details, Object... args) {
        super(String.format(errorCode.getMessage(), args), cause);
        this.errorCode = errorCode;
        this.details = details;
    }
    public ProcessingException(ErrorCode errorCode, Params details, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
        this.details = details;
    }

    public ProcessingException(ErrorCode errorCode, Object... args) {
        this(errorCode, Params.EMPTY, args);
    }

    public Params errorDetails() {
        Params error = Params
                .of("code", getErrorCode().name())
                .append("message", getMessage());
        if(getCause() != null) {
            List<StackTraceElement> traceElements = Arrays
                    .stream(getCause().getStackTrace())
                    .limit(4)
                    .collect(Collectors.toList());
            error.put("cause", traceElements);
        }
        if(getDetails().size() > 0)
            error.put("more", getDetails());
        return error;
    }

}
