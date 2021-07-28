package de.hsrm.orchestrationsystem.testcase_orchestration.teststep;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractBasicTestStepBuilder implements TestStepBuilder {

    String name;
    String target;
    boolean timeout;
    int timeoutDuration;
    boolean retry;
    int retryAttempts;
    String action;
    Map<String, Object> options;

    @Override
    public TestStepBuilder New() {
        this.name = "";
        this.timeout = false;
        this.timeoutDuration = -1;
        this.retry = false;
        this.retryAttempts = -1;
        this.action = "";
        this.options = null;
        this.target = "";
        return this;
    }

    @Override
    public TestStepBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public TestStepBuilder withTimeout(boolean timeout, int timeoutDuration) {
        this.timeout = timeout;
        this.timeoutDuration = timeoutDuration;
        return this;
    }

    @Override
    public TestStepBuilder withRetry(boolean retry, int retryAttempts) {
        this.retry = retry;
        this.retryAttempts = retryAttempts;
        return this;
    }

    @Override
    public TestStepBuilder withAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public TestStepBuilder withOptions(Map<String, Object> options) {
        this.options = options;
        return this;
    }

    @Override
    public TestStepBuilder withTarget(String target) {
        this.target = target;
        return this;
    }
}
