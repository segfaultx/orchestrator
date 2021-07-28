package de.hsrm.orchestrationsystem.testcase_orchestration.listeners;

import de.hsrm.orchestrationsystem.testcase_orchestration.events.OrchestratorChangeEvent;

import java.beans.PropertyChangeListener;

public interface OrchestratorChangeListener<T, M> extends PropertyChangeListener {

    void propertyChange(final OrchestratorChangeEvent<T, M> orchestratorChangeEvent);
}
