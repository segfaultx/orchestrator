package de.hsrm.orchestrationsystem.testcase_orchestration.teststep;

import java.util.Map;

public interface TestStepBuilder {

    TestStepBuilder New();

    TestStepBuilder withName(String name);

    TestStepBuilder withTimeout(boolean timeout, int timeoutDuration);

    TestStepBuilder withRetry(boolean retry, int retryAttempts);

    TestStepBuilder withAction(String action);

    TestStepBuilder withOptions(Map<String, Object> options);

    TestStepBuilder withTarget(String target);

    TestStep build();
}
