package de.hsrm.orchestrationsystem.testcase_orchestration.validator;

import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestDescription;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;

import java.util.List;
import java.util.Map;

public interface OrchestratorValidator {

    void validateExclusiveSequenceOrParallel(TestDescription description);

    void validateNoCyclicDependencies(Map<TestStep, List<TestStep>> dependencies);
}
