package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.selenium;

import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.testcase_orchestration.listeners.OrchestratorTestStepChangeListener;
import de.hsrm.orchestrationsystem.tests.selenium.wordpress.WordpressRegister;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SeleniumTestStepTest {

    @Mock
    WordpressRegister mockTest;

    @Mock
    OrchestratorTestStepChangeListener changeListener;

    @Test
    public void testSeleniumTestStepRun(){
        var seleniumTestStep = new SeleniumTestStep(mockTest);
        seleniumTestStep.addChangeListener(changeListener);
        seleniumTestStep.run();
        Mockito.verify(mockTest, Mockito.times(1)).run();
    }

    @Test
    public void testSeleniumTestStepRunFailure(){
        var seleniumTestStep = new SeleniumTestStep(mockTest);
        Mockito.doThrow(new OrchestratorException("error")).when(mockTest).run();
        assertEquals(TestStatus.PENDING, seleniumTestStep.getStatus(), "step shuld have PENDING as initial status");
        seleniumTestStep.run();
        assertEquals(TestStatus.FAILED, seleniumTestStep.getStatus(), "step should have failled");
        assertEquals("error", seleniumTestStep.getMessage(), "error message should equal");
    }

    @Test
    public void testSeleniumTestStepCancel(){
        var seleniumTestStep = new SeleniumTestStep(mockTest);
        assertEquals(TestStatus.PENDING, seleniumTestStep.getStatus(), "step shuld have PENDING as initial status");
        seleniumTestStep.cancel();
        assertEquals(TestStatus.FAILED, seleniumTestStep.getStatus(), "step should have failed");
        Mockito.verify(mockTest, Mockito.times(1)).interrupt();
    }

    @Test
    public void testSeleniumTestStepTimeout(){
        var seleniumTestStep = new SeleniumTestStep(mockTest);
        assertEquals(TestStatus.PENDING, seleniumTestStep.getStatus(), "step shuld have PENDING as initial status");
        seleniumTestStep.timeout();
        assertEquals(TestStatus.FAILED, seleniumTestStep.getStatus(), "step should have failed");
        Mockito.verify(mockTest, Mockito.times(1)).interrupt();
    }

    @Test
    public void testSeleniumTestStepReset(){
        var seleniumTestStep = new SeleniumTestStep(mockTest);
        assertEquals(TestStatus.PENDING, seleniumTestStep.getStatus(), "step shuld have PENDING as initial status");
        seleniumTestStep.cancel();
        assertEquals(TestStatus.FAILED, seleniumTestStep.getStatus(), "step should have failed");
        Mockito.verify(mockTest, Mockito.times(1)).interrupt();
        seleniumTestStep.reset();
        assertEquals(TestStatus.PENDING, seleniumTestStep.getStatus(), "step should be back to PENDING");
        assertEquals("", seleniumTestStep.getMessage(), "error message should have been cleared");

    }
}
