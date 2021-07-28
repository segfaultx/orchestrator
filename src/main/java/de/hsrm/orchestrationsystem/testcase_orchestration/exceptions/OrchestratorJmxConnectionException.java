package de.hsrm.orchestrationsystem.testcase_orchestration.exceptions;

/**
 *
 */
public class OrchestratorJmxConnectionException extends OrchestratorException {

    public OrchestratorJmxConnectionException(String target, String application, String errormsg) {
        super(String.format("Couldn't get connection for target: %s and application: %s, reason: %s", target, application, errormsg));
    }
}
