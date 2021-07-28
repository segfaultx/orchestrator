package de.hsrm.orchestrationsystem.testcase_orchestration.events;


import java.beans.PropertyChangeEvent;

public class OrchestratorChangeEvent<T, M> extends PropertyChangeEvent {

    public OrchestratorChangeEvent(final T source, final String propertyName, final M oldValue, M newValue) {
        super(source, propertyName, oldValue, newValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public M getOldValue() {
        return (M) super.getOldValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public M getNewValue() {
        return (M) super.getNewValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getSource() {
        return (T) super.getSource();
    }
}
