package de.hsrm.orchestrationsystem.testcase_orchestration.listeners;

import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.events.OrchestratorChangeEvent;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.crossapplicationtest.CrossApplicationTest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeEvent;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrchestratorTestStepChangeListener implements OrchestratorChangeListener<TestStep, TestStatus> {

    CrossApplicationTest callback;

    public OrchestratorTestStepChangeListener(CrossApplicationTest suite) {
        this.callback = suite;
    }

    @Override
    public void propertyChange(OrchestratorChangeEvent<TestStep, TestStatus> orchestratorChangeEvent) {
        log.info("Received event in dedicated handler: {}", orchestratorChangeEvent.getSource().toString());
        callback.handleChangedEvent(orchestratorChangeEvent.getSource(), orchestratorChangeEvent.getNewValue());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        propertyChange((OrchestratorChangeEvent<TestStep, TestStatus>) propertyChangeEvent);
    }
}
