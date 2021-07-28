package de.hsrm.orchestrationsystem.testcase_orchestration.teststep;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.testcase_orchestration.listeners.OrchestratorTestStepChangeListener;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.selenium.SeleniumTestStep;
import de.hsrm.orchestrationsystem.tests.selenium.wordpress.WordpressRegister;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AbstractBasicTestStepTest {


    private AbstractBasicTestStep testStep;
    private AbstractBasicTestStep spy;
    private Map<String, Object> mockContext;

    @Mock
    OrchestratorTestStepChangeListener listener;

    @BeforeEach
    public void setTestStep() {
        var mockTest = Mockito.mock(WordpressRegister.class);
        this.testStep = new SeleniumTestStep(mockTest);
        this.spy = Mockito.spy(testStep);
    }

    @Test
    public void testAbstractBasicTestStepRun() {
        spy.run();
        Mockito.verify(spy, Mockito.times(1)).execute();
    }


    private void setTestStepContext() {
        mockContext = new HashMap<>();
        mockContext.put("test", "test");
        testStep.setContext(mockContext);

    }

    @Test
    public void testAbstractBasicTestStepSetContext() {
        setTestStepContext();
        assertEquals(mockContext, ReflectionTestUtils.getField(testStep, "context"), "should bet he same context");
    }

    @Test
    public void testAbstractBasicTestStepAddValueToContext() {
        setTestStepContext();
        testStep.addValueToContext("the test", "the value");
        assertEquals(mockContext, ReflectionTestUtils.getField(testStep, "context"), "should be the same context");
    }

    @Test
    public void testAbstractBasicTestStepGetvalueFromContext() {
        setTestStepContext();
        assertEquals("test", testStep.getValueFromContext("test"), "should contain the value");
    }

    @Test
    public void testAbstractBasicTestStepGetvalueFromContextError() {
        setTestStepContext();
        assertThrows(OrchestratorException.class, () -> testStep.getValueFromContext("testo"),
                "should throw an error for unknown key");
    }
}
