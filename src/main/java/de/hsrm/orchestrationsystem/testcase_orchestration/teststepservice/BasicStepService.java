package de.hsrm.orchestrationsystem.testcase_orchestration.teststepservice;

import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestStepDescription;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.StepBuilderRegistrationException;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.UnknownStepTypeException;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStepBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class BasicStepService implements TestStepService {

    Map<String, TestStepBuilder> stepBuilders;

    @Value("#{${orchestrator.steps.config}}")
    Map<String, String> stepsConfig;

    Map<String, TestStepBuilder> configuredStepBuilders = new HashMap<>();

    @PostConstruct
    public void setConfiguredStepBuilders() {
        for (Map.Entry<String, String> entry : stepsConfig.entrySet()) {
            var key = entry.getKey();
            var val = entry.getValue();
            log.info("registering step type: {} for key word: {}", val, key);
            var valToAdd = stepBuilders.get(val);
            if (valToAdd == null)
                throw new StepBuilderRegistrationException(entry.getValue(), entry.getKey(), stepBuilders.keySet());
            this.configuredStepBuilders.put(key, valToAdd);
        }
    }

    @Override
    public TestStep getStepForDescription(TestStepDescription description) {
        if (!this.configuredStepBuilders.containsKey(description.getType()))
            throw new UnknownStepTypeException(description.getType());
        var builder = this.configuredStepBuilders.get(description.getType());

        return builder
                .New()
                .withTarget(description.getTarget())
                .withName(description.getName())
                .withAction(description.getAction())
                .withTimeout(description.getTimeout(), description.getTimeoutDuration())
                .withRetry(description.getRetry(), description.getRetryAttempts())
                .withOptions(description.getOptions())
                .build();
    }
}
