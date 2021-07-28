package de.hsrm.orchestrationsystem.testcase_orchestration.creator;

import de.hsrm.orchestrationsystem.testcase_orchestration.TestDescriptionUtil;
import de.hsrm.orchestrationsystem.testcase_orchestration.crossapplicationtest.BasicCrossApplicationTest;
import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestControlBlock;
import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestDescription;
import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestParallelBlock;
import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestSequenceBlock;
import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestStepDescription;
import de.hsrm.orchestrationsystem.testcase_orchestration.reader.TestDescriptionReader;
import de.hsrm.orchestrationsystem.testcase_orchestration.testcase.BasicTestCase;
import de.hsrm.orchestrationsystem.testcase_orchestration.testcase.TestCase;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststepservice.TestStepService;
import de.hsrm.orchestrationsystem.testcase_orchestration.validator.OrchestratorValidator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BasicTestCreator implements TestCreator {

    TestDescriptionReader reader;

    TestStepService stepService;

    OrchestratorValidator validator;

    @Value("${orchestrator.max.parallel.test.steps}")
    @NonFinal
    int MAXIMUM_TEST_STEPS;


    @Override
    public TestCase createTestSuite(Path path) {
        var description = reader.readTestDescription(path);
        validator.validateExclusiveSequenceOrParallel(description);
        var stepsList = buildTestSteps(description);

        var dependencies = buildDependencies(stepsList, description);
        validator.validateNoCyclicDependencies(dependencies);
        String name = description.getName() == null ? path.getFileName().toString() : description.getName();
        return new BasicTestCase(name, Collections.singletonList(new BasicCrossApplicationTest(description.getName(), stepsList, MAXIMUM_TEST_STEPS, dependencies)));
    }

    private List<TestStep> buildTestSteps(TestDescription description) {
        var out = new ArrayList<TestStep>();

        if (description.getSequence() != null)
            out.addAll(buildTestSteps(description.getSequence()));
        if (description.getParallel() != null)
            out.addAll(buildTestSteps(description.getParallel()));

        return out;
    }

    private List<TestStep> buildTestSteps(TestControlBlock description) {
        var out = new ArrayList<TestStep>();
        description.getSteps().forEach(step -> out.add(stepService.getStepForDescription(step)));
        description.getSequence().forEach(item -> out.addAll(buildTestSteps(item)));
        description.getParallel().forEach(item -> out.addAll(buildTestSteps(item)));
        return out;
    }

    private Map<TestStep, List<TestStep>> buildDependencies(List<TestStep> steps, TestDescription description) {
        Map<TestStep, List<TestStep>> out = new HashMap<>();
        var mappedSteps = steps.
                stream()
                .collect(Collectors.toMap(TestStep::getName, testStep -> testStep));
        if (description.getParallel() != null)
            out.putAll(generateParallelDependenciesMap(mappedSteps, description.getParallel()));

        if (description.getSequence() != null)
            out.putAll(generateSequenceDependenciesMap(mappedSteps, description.getSequence()));

        return out;
    }

    private Map<TestStep, List<TestStep>> generateParallelDependenciesMap(Map<String, TestStep> mappedSteps,
                                                                          TestParallelBlock description) {
        Map<TestStep, List<TestStep>> out = new HashMap<>();
        description.getParallel().forEach(item -> out.putAll(generateParallelDependenciesMap(mappedSteps, item, description)));
        description.getSequence().forEach(item -> out.putAll(generateSequenceDependenciesMap(mappedSteps, item, description)));

        return out;
    }

    private Map<TestStep, List<TestStep>> generateSequenceDependenciesMap(Map<String, TestStep> mappedSteps,
                                                                          TestSequenceBlock description) {

        Map<TestStep, List<TestStep>> out = new HashMap<>(buildSequenceDependencies(mappedSteps, description));
        description.getParallel().forEach(item -> out.putAll(generateParallelDependenciesMap(mappedSteps, item, description)));
        description.getSequence().forEach(item -> out.putAll(generateSequenceDependenciesMap(mappedSteps, item, description)));

        return out;
    }

    private Map<TestStep, List<TestStep>> generateParallelDependenciesMap(Map<String, TestStep> mappedSteps,
                                                                          TestParallelBlock description,
                                                                          TestParallelBlock parent) {
        Map<TestStep, List<TestStep>> out = new HashMap<>();
        var parentDependencies = TestDescriptionUtil.getParallelParentSteps(mappedSteps, parent);
        description.getSteps().forEach(step -> out.put(mappedSteps.get(step.getName()), parentDependencies));

        description.getParallel().forEach(item -> out.putAll(generateParallelDependenciesMap(mappedSteps, item, description)));
        description.getSequence().forEach(item -> out.putAll(generateSequenceDependenciesMap(mappedSteps, item, description)));

        return out;
    }

    private Map<TestStep, List<TestStep>> generateParallelDependenciesMap(Map<String, TestStep> mappedSteps,
                                                                          TestParallelBlock description,
                                                                          TestSequenceBlock parent) {
        Map<TestStep, List<TestStep>> out = new HashMap<>();
        TestStepDescription lastSequenceStep = parent.getSteps().get(parent.getSteps().size() - 1);
        TestStep parentDependency = mappedSteps.get(lastSequenceStep.getName());
        description.getSteps().forEach(step ->
                out.put(mappedSteps.get(step.getName()),
                        Collections.singletonList(parentDependency))
        );
        return out;
    }


    private Map<TestStep, List<TestStep>> generateSequenceDependenciesMap(Map<String, TestStep> mappedSteps,
                                                                          TestSequenceBlock description,
                                                                          TestParallelBlock parent) {
        Map<TestStep, List<TestStep>> out = new HashMap<>();
        var parentDependencies = TestDescriptionUtil.getParallelParentSteps(mappedSteps, parent);
        var firstSequenceStep = description.getSteps().get(0);
        out.put(mappedSteps.get(firstSequenceStep.getName()), parentDependencies);
        out.putAll(buildSequenceDependencies(mappedSteps, description));
        description.getSequence().forEach(item -> out.putAll(generateSequenceDependenciesMap(mappedSteps, item, description)));
        description.getParallel().forEach(item -> out.putAll(generateParallelDependenciesMap(mappedSteps, item, description)));

        return out;
    }

    private Map<TestStep, List<TestStep>> generateSequenceDependenciesMap(Map<String, TestStep> mappedSteps,
                                                                          TestSequenceBlock description,
                                                                          TestSequenceBlock parent) {
        Map<TestStep, List<TestStep>> out = new HashMap<>();
        var lastParentSequenceStep = parent.getSteps().get(parent.getSteps().size() - 1);
        var firstSequenceStep = description.getSteps().get(0);
        out.put(mappedSteps.get(firstSequenceStep.getName()),
                Collections.singletonList(mappedSteps.get(lastParentSequenceStep.getName())));
        out.putAll(buildSequenceDependencies(mappedSteps, description));
        description.getSequence().forEach(item -> out.putAll(generateSequenceDependenciesMap(mappedSteps, item, description)));
        description.getParallel().forEach(item -> out.putAll(generateParallelDependenciesMap(mappedSteps, item, description)));


        return out;
    }

    private Map<TestStep, List<TestStep>> buildSequenceDependencies(Map<String, TestStep> mappedSteps,
                                                                    TestSequenceBlock description) {
        var out = new HashMap<TestStep, List<TestStep>>();
        if (description.getSteps().size() > 1) {
            var descriptionSteps = description.getSteps();
            for (int i = descriptionSteps.size() - 1; i > 0; i--) {
                var currentStep = mappedSteps.get(descriptionSteps.get(i).getName());
                var previousStep = mappedSteps.get(descriptionSteps.get(i - 1).getName());
                out.put(mappedSteps.get(currentStep.getName()),
                        Collections.singletonList(mappedSteps.get(previousStep.getName())));
            }
        }

        return out;
    }
}
