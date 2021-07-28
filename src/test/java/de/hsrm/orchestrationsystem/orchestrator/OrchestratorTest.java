package de.hsrm.orchestrationsystem.orchestrator;


import de.hsrm.orchestrationsystem.testcase_orchestration.creator.TestCreator;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.TestCaseException;
import de.hsrm.orchestrationsystem.testcase_orchestration.statusreport.TestStatusReport;
import de.hsrm.orchestrationsystem.testcase_orchestration.testcase.BasicTestCase;
import de.hsrm.orchestrationsystem.testcase_orchestration.testcase.TestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class OrchestratorTest {

    @Mock
    private TestCreator creator;

    @InjectMocks
    private OrchestratorService orchestrator;

    @Mock
    private BasicTestCase mockTestCase;


    private void setupOrchestrator() throws IOException {
        ReflectionTestUtils.setField(orchestrator, "YAML_BASEPATH", "/yaml");
        orchestrator.init();
    }

    @Test
    public void testStartTest() throws IOException {
        Mockito.when(mockTestCase.getName()).thenReturn("Test A");
        Mockito.when(creator.createTestSuite(any(Path.class))).thenReturn(mockTestCase);
        setupOrchestrator();

        orchestrator.startTest("Test A");
        Mockito.verify(mockTestCase, Mockito.times(1)).run();
    }

    @Test
    public void testStartTestFailure() {
        assertThrows(TestCaseException.class, () -> orchestrator.startTest("Test A"),
                "Exception for unknown test is properly thrown");
    }

    @Test
    public void testGetTestStatus() throws IOException {
        var mockReport = Mockito.mock(TestStatusReport.class);
        Mockito.when(mockTestCase.getName()).thenReturn("Test A");
        Mockito.when(creator.createTestSuite(any(Path.class))).thenReturn(mockTestCase);
        Mockito.when(mockTestCase.getStatusReport()).thenReturn(mockReport);
        setupOrchestrator();
        var report = orchestrator.getTestStatus("Test A");
        assertEquals(mockReport, report, "Report equals");
    }

    @Test
    public void testGetTestStatuses() {
        var mockReport = Mockito.mock(TestStatusReport.class);
        var mockReportTwo = Mockito.mock(TestStatusReport.class);
        var mockTestCaseTwo = Mockito.mock(BasicTestCase.class);

        var reportList = Arrays.asList(mockReport, mockReportTwo);
        Map<String, TestCase> suites = new HashMap<>();
        suites.put("Test A", mockTestCase);
        suites.put("Test B", mockTestCaseTwo);
        ReflectionTestUtils.setField(orchestrator, "suites", suites);
        Mockito.when(mockTestCase.getStatusReport()).thenReturn(mockReport);
        Mockito.when(mockTestCaseTwo.getStatusReport()).thenReturn(mockReportTwo);
        var returnedReports = orchestrator.getAllTestStatuses();
        assertThat(returnedReports).hasSameElementsAs(reportList);
    }

    @Test
    public void testStartTests() {
        var mockTestCaseTwo = Mockito.mock(BasicTestCase.class);
        Map<String, TestCase> suites = new HashMap<>();
        suites.put("Test A", mockTestCase);
        suites.put("Test B", mockTestCaseTwo);
        ReflectionTestUtils.setField(orchestrator, "suites", suites);
        orchestrator.startTests(Arrays.asList("Test A", "Test B"));
        Mockito.verify(mockTestCase, Mockito.times(1)).run();
        Mockito.verify(mockTestCaseTwo, Mockito.times(1)).run();
    }

    @Test
    public void testStartTestsFailure() {
        var mockTestCaseTwo = Mockito.mock(BasicTestCase.class);
        Map<String, TestCase> suites = new HashMap<>();
        suites.put("Test A", mockTestCase);
        suites.put("Test C", mockTestCaseTwo);
        ReflectionTestUtils.setField(orchestrator, "suites", suites);
        assertThrows(TestCaseException.class, () -> orchestrator.startTests(Arrays.asList("Test A", "Test B")),
                "Exception is thrown when submitting unknown test");
        Mockito.verify(mockTestCase, Mockito.times(0)).run();
        Mockito.verify(mockTestCaseTwo, Mockito.times(0)).run();
    }

    @Test
    public void testStopTest() {
        Map<String, TestCase> suites = new HashMap<>();
        suites.put("Test A", mockTestCase);
        ReflectionTestUtils.setField(orchestrator, "suites", suites);
        orchestrator.stopTest("Test A");
        Mockito.verify(mockTestCase, Mockito.times(1)).cancel();
    }

    @Test
    public void testStopTestFailure() {
        Map<String, TestCase> suites = new HashMap<>();
        suites.put("Test A", mockTestCase);
        ReflectionTestUtils.setField(orchestrator, "suites", suites);
        assertThrows(TestCaseException.class, () -> orchestrator.stopTest("Test B"),
                "Exception for unknown test is thrown when attempting to stop");
        Mockito.verify(mockTestCase, Mockito.times(0)).cancel();
    }
}
