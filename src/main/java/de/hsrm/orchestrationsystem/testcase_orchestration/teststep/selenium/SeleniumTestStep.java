package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.selenium;

import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.events.OrchestratorChangeEvent;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.AbstractBasicTestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import de.hsrm.orchestrationsystem.tests.selenium.SeleniumTest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeleniumTestStep extends AbstractBasicTestStep {

    SeleniumTest test;

    public SeleniumTestStep(SeleniumTest test){
        this.test = test;
    }

    private void init(){
        this.test.addContext(this.context);
    }

    @Override
    public void execute() {
        init();
        this.status = TestStatus.EXECUTING;
        this.startTime = LocalDateTime.now();
        try {
            test.run();
            this.status = TestStatus.SUCCESS;
        } catch (OrchestratorException ex) {
            this.status = TestStatus.FAILED;
            this.message = ex.getMessage();
        }
        fireStatusChangedEvent();
    }

    @Override
    public void cancel() {
        this.test.interrupt();
        this.status = TestStatus.FAILED;
    }

    @Override
    public void retry() {
        if (this.retry && (this.currentRetryAttempts < this.retryAttempts)){
            this.status = TestStatus.PENDING;
            this.message = "";
        }
    }

    @Override
    public void timeout() {
        this.cancel();
    }

    @Override
    public void reset() {
        this.status = TestStatus.PENDING;
        this.message = "";
        this.currentRetryAttempts = 0;
    }
}
