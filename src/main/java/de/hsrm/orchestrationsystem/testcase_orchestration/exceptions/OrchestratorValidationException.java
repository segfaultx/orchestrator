package de.hsrm.orchestrationsystem.testcase_orchestration.exceptions;

import java.util.Set;

/**
 *
 */
public class OrchestratorValidationException extends OrchestratorException {

    public OrchestratorValidationException(Set<?> errors) {
        super("Validation error, cause: " + errors.toString());
    }

    public OrchestratorValidationException(String message){
        super(message);
    }

}
