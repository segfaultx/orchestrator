package de.hsrm.orchestrationsystem.testcase_orchestration.testcase;

import de.hsrm.orchestrationsystem.testcase_orchestration.statusreport.TestStatusReport;

public interface TestCase {

    void run();

    void cancel();

    TestStatusReport getStatusReport();

    String getName();

    void updateStatusReport(TestStatusReport report);
}
