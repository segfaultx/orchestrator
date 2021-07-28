package de.hsrm.orchestrationsystem.testcase_orchestration;

import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestParallelBlock;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class TestDescriptionUtil {

    public static List<TestStep> getParallelParentSteps(@NotNull Map<String, TestStep> mappedSteps, @NotNull TestParallelBlock parent){
        return parent
                .getSteps()
                .stream()
                .map(step -> mappedSteps.get(step.getName()))
                .collect(toList());
    }

}
