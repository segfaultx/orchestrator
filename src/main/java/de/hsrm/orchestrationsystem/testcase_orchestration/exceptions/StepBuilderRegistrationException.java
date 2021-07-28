package de.hsrm.orchestrationsystem.testcase_orchestration.exceptions;

import java.util.Collection;

public class StepBuilderRegistrationException extends OrchestratorException{
    public StepBuilderRegistrationException(String type, String keyword, Collection<String> availableBuilders) {
        super(String.format("Error registering step type: %s for key word: %s, available types: %s", type, keyword, availableBuilders));
    }
}
