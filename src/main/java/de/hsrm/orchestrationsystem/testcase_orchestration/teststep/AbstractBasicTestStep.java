package de.hsrm.orchestrationsystem.testcase_orchestration.teststep;

import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.events.OrchestratorChangeEvent;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Getter
@Setter
public abstract class AbstractBasicTestStep implements TestStep {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    TestStatus status = TestStatus.PENDING;

    boolean timeout;

    LocalDateTime startTime;

    boolean wasInterruped;

    boolean wasTimeouted;

    boolean retry = false;

    int retryAttempts = -1;

    int currentRetryAttempts = 0;

    String message;

    String name;

    String target;

    String action;

    Map<String, Object> context;

    @Override
    public void addChangeListener(PropertyChangeListener listener) {
        this.changeSupport.addPropertyChangeListener("status", listener);
    }

    @Override
    public final void run() {
        this.startTime = LocalDateTime.now();
        try {
            execute();
        } catch (RuntimeException ex) {
            this.message = ex.getMessage();
            this.status = TestStatus.FAILED;
            fireStatusChangedEvent();
        }
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    protected void addValueToContext(String key, Object value) {
        this.context.put(key, value);
    }

    protected Object getValueFromContext(String key) {
        if (!this.context.containsKey(key))
            throw new OrchestratorException("Unknown context key: " + key);
        return this.context.get(key);
    }

    protected void fireStatusChangedEvent() {
        this.changeSupport.firePropertyChange(new OrchestratorChangeEvent<TestStep, TestStatus>(
                this,
                "status",
                null,
                this.status));
    }
}
