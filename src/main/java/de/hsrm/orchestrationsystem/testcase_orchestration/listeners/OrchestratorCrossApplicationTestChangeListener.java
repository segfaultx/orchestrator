package de.hsrm.orchestrationsystem.testcase_orchestration.listeners;

import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.events.OrchestratorChangeEvent;
import de.hsrm.orchestrationsystem.testcase_orchestration.crossapplicationtest.CrossApplicationTest;
import de.hsrm.orchestrationsystem.testcase_orchestration.testcase.TestCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeEvent;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrchestratorCrossApplicationTestChangeListener implements OrchestratorChangeListener<CrossApplicationTest, TestStatus> {

    TestCase callback;

    @Override
    public void propertyChange(OrchestratorChangeEvent<CrossApplicationTest, TestStatus> orchestratorChangeEvent) {
        log.info("Received Test Suite event: {}", orchestratorChangeEvent.getSource().toString());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        propertyChange((OrchestratorChangeEvent<CrossApplicationTest, TestStatus>) propertyChangeEvent);
    }
}
