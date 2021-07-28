package de.hsrm.orchestrationsystem.testcase_orchestration.exceptions;

public class UnknownStepTypeException extends OrchestratorException {

    public UnknownStepTypeException(String stepType){
        super(String.format("Unknown step type: %s", stepType));
    }
}
