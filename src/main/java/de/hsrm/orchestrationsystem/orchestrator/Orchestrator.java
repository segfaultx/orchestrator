package de.hsrm.orchestrationsystem.orchestrator;

import de.hsrm.orchestrationsystem.testcase_orchestration.statusreport.TestStatusReport;

import java.util.List;

public interface Orchestrator {

    void startTest(String testName);

    void stopTest(String testName);

    void startTests(List<String> testNames);

    TestStatusReport getTestStatus(String testName);

    List<TestStatusReport> getAllTestStatuses();
}
