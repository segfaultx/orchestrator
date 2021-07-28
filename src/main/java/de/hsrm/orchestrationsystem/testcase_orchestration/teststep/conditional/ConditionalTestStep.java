package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.conditional;

import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.events.OrchestratorChangeEvent;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.AbstractBasicTestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.conditional.conditions.TestCondition;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConditionalTestStep extends AbstractBasicTestStep {

    TestCondition IF;

    TestStep THEN;

    TestStep ELSE;

    @NonFinal
    TestStep toExecute;

    private void init(){
        THEN.setContext(this.context);
        ELSE.setContext(this.context);
    }

    @Override
    public void execute() {
        this.status = TestStatus.EXECUTING;
        init();
        if (IF.check())
            this.toExecute = THEN;
        else
            this.toExecute = ELSE;
        log.info("EXECUTING TEST STEP {}", this.toExecute.getName());
        this.toExecute.run();
        this.status = toExecute.getStatus();
        this.message = toExecute.getMessage();
        fireStatusChangedEvent();
    }

    @Override
    public void cancel() {
        this.status = TestStatus.FAILED;
        this.toExecute.cancel();
    }

    @Override
    public void retry() {
        if (this.retry && (this.currentRetryAttempts < this.retryAttempts)){
            this.status = TestStatus.PENDING;
            this.message = "";
            this.THEN.reset();
            this.ELSE.reset();
        }
    }

    @Override
    public void timeout() {
        cancel();
    }

    @Override
    public void reset() {
        this.THEN.reset();
        this.ELSE.reset();
        this.message = "";
        this.status = TestStatus.PENDING;
        this.currentRetryAttempts = 0;
    }
}
