package de.hsrm.orchestrationsystem.testcase_orchestration.testcase;

import de.hsrm.orchestrationsystem.testcase_orchestration.crossapplicationtest.CrossApplicationTest;
import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.TestCaseException;
import de.hsrm.orchestrationsystem.testcase_orchestration.listeners.OrchestratorChangeListener;
import de.hsrm.orchestrationsystem.testcase_orchestration.listeners.OrchestratorCrossApplicationTestChangeListener;
import de.hsrm.orchestrationsystem.testcase_orchestration.listeners.OrchestratorCrossApplicationTestStatusReportListener;
import de.hsrm.orchestrationsystem.testcase_orchestration.statusreport.TestStatusReport;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BasicTestCase implements TestCase {

    String name;

    @NonFinal
    LocalDateTime startTime;

    Map<String, CrossApplicationTest> tests;

    @NonFinal
    TestStatusReport report;

    @NonFinal
    TestStatus status = TestStatus.PENDING;

    int MAXIMUM_CACHED_REPORTS = 10;

    List<TestStatusReport> cachedReports = new ArrayList<>();

    OrchestratorChangeListener<CrossApplicationTest, TestStatus> testListener =
            new OrchestratorCrossApplicationTestChangeListener(this);

    OrchestratorChangeListener<CrossApplicationTest, TestStatusReport> reportListener =
            new OrchestratorCrossApplicationTestStatusReportListener(this);

    public BasicTestCase(String name, List<CrossApplicationTest> tests) {
        this.tests = new HashMap<>();
        this.name = name;
        this.report = new TestStatusReport(this.name, this.status, null);
        tests.forEach(test -> {
            this.tests.put(test.getName(), test);
            test.addChangeListener("status", testListener);
            test.addChangeListener("report", reportListener);
        });
    }


    @Override
    public void run() {
        if (this.status == TestStatus.EXECUTING)
            throw new TestCaseException("Test Suite is already running");
        if (this.status == TestStatus.SUCCESS || this.status == TestStatus.FAILED){
            resetSuite();
            resetReportAndAddToCache();
        }
        this.startTime = LocalDateTime.now();
        tests.values().forEach(CrossApplicationTest::run);
        this.status = TestStatus.EXECUTING;
        this.report.setStatus(this.status);
        this.report.setStartTime(this.startTime);
    }

    @Override
    public void cancel() {
        if (this.status != TestStatus.EXECUTING)
            throw new TestCaseException("Suite not running");
        tests.values().forEach(CrossApplicationTest::cancel);
        this.status = TestStatus.FAILED;
        this.report.setStatus(this.status);
    }

    @Override
    public TestStatusReport getStatusReport() {
        return this.report;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public synchronized void updateStatusReport(TestStatusReport report) {
        this.report.addChild(report);

        if (isTestCaseFinished()){
            this.status = TestStatus.SUCCESS;
            this.report.setStatus(this.status);
            this.report.convertToFinishedReport();

        }
        if (isTestCaseFailed()){
            this.status = TestStatus.FAILED;
            this.report.setStatus(this.status);
        }
    }

    private void resetSuite(){
        this.status = TestStatus.PENDING;
        this.tests.values().forEach(CrossApplicationTest::reset);
    }


    private void resetReportAndAddToCache() {
        addReportToCache(this.report);
        this.report = new TestStatusReport(this.name, this.status, null);
        this.startTime = null;
    }


    private void addReportToCache(TestStatusReport report){
        while (this.cachedReports.size() > this.MAXIMUM_CACHED_REPORTS)
            this.cachedReports.remove(0);
        this.cachedReports.add(report);
    }

    private boolean isTestCaseFinished(){
        return this.report.getChildren().values().stream().allMatch(item -> item.getStatus() == TestStatus.SUCCESS);
    }

    private boolean isTestCaseFailed(){
        return this.report.getChildren().values().stream().anyMatch(item -> item.getStatus() == TestStatus.FAILED);
    }
}
