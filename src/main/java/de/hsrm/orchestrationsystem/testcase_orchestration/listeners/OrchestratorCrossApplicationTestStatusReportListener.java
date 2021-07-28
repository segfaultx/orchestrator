package de.hsrm.orchestrationsystem.testcase_orchestration.listeners;

import de.hsrm.orchestrationsystem.testcase_orchestration.crossapplicationtest.CrossApplicationTest;
import de.hsrm.orchestrationsystem.testcase_orchestration.events.OrchestratorChangeEvent;
import de.hsrm.orchestrationsystem.testcase_orchestration.statusreport.TestStatusReport;
import de.hsrm.orchestrationsystem.testcase_orchestration.testcase.TestCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeEvent;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrchestratorCrossApplicationTestStatusReportListener implements OrchestratorChangeListener<CrossApplicationTest, TestStatusReport> {

    TestCase callback;

    @Override
    public void propertyChange(OrchestratorChangeEvent<CrossApplicationTest, TestStatusReport> orchestratorChangeEvent) {
        log.info("received report event: {} in {}", orchestratorChangeEvent.getNewValue(), orchestratorChangeEvent.getSource().getName());
        callback.updateStatusReport(orchestratorChangeEvent.getNewValue());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        propertyChange((OrchestratorChangeEvent<CrossApplicationTest, TestStatusReport>) propertyChangeEvent);
    }
}
