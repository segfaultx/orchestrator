package de.hsrm.orchestrationsystem.testcase_orchestration.teststep;

import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;

import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.util.Map;

public interface TestStep extends Runnable {

    void execute();

    void cancel();

    void retry();

    void timeout();

    void addChangeListener(PropertyChangeListener listener);

    TestStatus getStatus();

    String getName();

    String getMessage();

    LocalDateTime getStartTime();

    void reset();

    void setContext(Map<String, Object> context);
}
