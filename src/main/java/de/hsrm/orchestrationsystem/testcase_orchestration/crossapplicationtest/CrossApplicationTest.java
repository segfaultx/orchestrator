package de.hsrm.orchestrationsystem.testcase_orchestration.crossapplicationtest;

import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;

import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;

public interface CrossApplicationTest {

    void run();

    void cancel();

    void handleChangedEvent(TestStep step, TestStatus status);

    String getName();

    LocalDateTime getStartTime();

    void addChangeListener(String property, PropertyChangeListener listener);

    void reset();
}
