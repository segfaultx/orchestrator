package de.hsrm.orchestrationsystem.testcase_orchestration.validator;

import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestDescription;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorValidationException;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.validator.groups.ValidateParallelAsRoot;
import de.hsrm.orchestrationsystem.testcase_orchestration.validator.groups.ValidateSequenceAsRoot;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BasicOrchestratorValidator implements OrchestratorValidator {

    Validator validator;

    @Override
    public void validateExclusiveSequenceOrParallel(TestDescription description) {
        var sequenceValidationErrors = validator.validate(description, ValidateSequenceAsRoot.class);
        var parallelValidationErrors = validator.validate(description, ValidateParallelAsRoot.class);
        if (sequenceValidationErrors.isEmpty()){
            var sequenceErrors = validator.validate(description.getSequence());
            if (!sequenceErrors.isEmpty())
                throw new OrchestratorValidationException(sequenceErrors);
        }else if (parallelValidationErrors.isEmpty()){
            var parallelErrors = validator.validate(description.getParallel());
            if (!parallelErrors.isEmpty())
                throw new OrchestratorValidationException(parallelErrors);
        }else{
            throw new OrchestratorValidationException("Validation error, must have either sequence or parallel as root");
        }

    }

    @Override
    public void validateNoCyclicDependencies(Map<TestStep, List<TestStep>> dependencies) {
        for (TestStep step : dependencies.keySet()) {
            // initial starting point, example A
            List<TestStep> currentPath = new ArrayList<>(Collections.singleton(step));
            for (TestStep dependencyStep : dependencies.get(step)) {
                // step actually has dependencies, check path
                if (dependencies.get(dependencyStep) != null) {
                    // add step to path, example B -> [ A, B ] so far
                    currentPath.add(dependencyStep);
                    checkDependencyPathForCyclicDependencies(step, dependencyStep, dependencies, currentPath);
                }
            }
        }
    }

    private void checkDependencyPathForCyclicDependencies(TestStep origin,
                                                          TestStep current,
                                                          Map<TestStep, List<TestStep>> dependencies,
                                                          List<TestStep> path) {
        // no more dependencies, cancel path checking
        if (dependencies.isEmpty())
            return;
        // check dependencies of current dependency, in this example dependencies of B
        for (TestStep stepToCheck : dependencies.getOrDefault(current, new ArrayList<>())) {
            // if dependency of B equals initial starting point (A) cancel further checking
            if (stepToCheck == origin) {
                path.add(stepToCheck);
                throw new OrchestratorValidationException("Cyclic dependency found, path: " + path);
            }
            // else check if dependency of B has dependencies of its own
            if (dependencies.get(stepToCheck) != null) {
                path.add(stepToCheck);
                var reducedDependencies = filterDependencies(current, dependencies);
                checkDependencyPathForCyclicDependencies(origin, stepToCheck, reducedDependencies, path);
            }
        }
    }

    private Map<TestStep, List<TestStep>> filterDependencies(TestStep testStep, Map<TestStep, List<TestStep>> dependencies) {
        var out = new HashMap<TestStep, List<TestStep>>();
        for (var entry :
                dependencies.entrySet()) {
            if (entry.getKey() != testStep) {
                out.put(entry.getKey(), entry.getValue());
            }
        }
        return out;
    }
}
