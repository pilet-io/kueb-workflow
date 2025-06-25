package io.pilet.kueb.workflow.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum ErrorCode {
    SYSTEM_CONFIG_ERROR(CONFLICT, "Configuration error: %s"),
    SSH_COMMAND_FAILED(CONFLICT, "Ssh failed params exit code %s"),
    SSH_EXECUTION_ERROR(CONFLICT, "Error during ssh call : %s"),
    REST_EXECUTION_ERROR(CONFLICT, "Error during rest call [%d] : %s"),
    GENERAL_ERROR(CONFLICT, "Error response code : %s"),
    GENERIC_NOT_FOUND(NOT_FOUND, "Not found : %s"),
    TASK_DEFINITION_NOT_FOUND(NOT_FOUND, "Task definition not found [%s]"),
    TASK_PROCESSOR_NOT_FOUND(NOT_FOUND, "Task processor not found [%s]"),
    FILE_NOT_FOUND(NOT_FOUND, "File not found [%s]"),
    TEMPLATE_ERROR(CONFLICT, "Error processing template [%s]"),
    REST_ERROR(CONFLICT, "Error while processing Rest call [%s]"),
    PRELOAD_CONFIG(CONFLICT, "Error during preloading of config: [%s]"),
    MISSING_ARG(CONFLICT, "Required parameter [%s] is missing"),
    KAFKA_ADMIN_EXCEPTION(CONFLICT, "Kafka admin client error : [%s]"),
    SYNTAX_ERROR(CONFLICT, "Syntax error: [%s]"),
    CONFIG_NOT_FOUND(NOT_FOUND, "Configuration not found: [%s]"),
    MISSING_PERSISTED_ANNOTATION(CONFLICT, "Missing PersistedIn annotation in [%s]"),
    WRONG_ACTION_CONFIG(CONFLICT, "Wrong action config : %s"),
    UNKNOWN_ACTION_NAME(CONFLICT, "Unknown action name : [%s]");


    private String message;
    private HttpStatus status;
    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
