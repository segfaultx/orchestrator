package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.selenium;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.tests.selenium.SeleniumTest;
import de.hsrm.orchestrationsystem.tests.selenium.wordpress.WordpressRegister;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SeleniumTestStepBuilderTest {

    @InjectMocks
    private SeleniumTestStepBuilder builder;


    @Test
    public void testBuildSeleniumTestStep() {
        Map<String, SeleniumTest> tests = new HashMap<>();
        var seleniumTestMock = Mockito.mock(WordpressRegister.class);
        tests.put("loginTest", seleniumTestMock);
        Mockito.when(seleniumTestMock.newInstance(Mockito.any(), Mockito.any())).thenReturn(seleniumTestMock);
        ReflectionTestUtils.setField(builder, "seleniumTestMap", tests);
        var returnedValue = builder.New()
                .withName("Test A")
                .withAction("loginTest")
                .withTarget("system a")
                .withOptions(null)
                .withRetry(false, 0)
                .withTimeout(false, 0)
                .build();
        var internalTest = ReflectionTestUtils.getField(returnedValue, null, "test");
        assertEquals(seleniumTestMock, internalTest, "test object should be the same");
    }

    @Test
    public void testBuildSeleniumTestStepFail(){
        ReflectionTestUtils.setField(builder, "seleniumTestMap", new HashMap<>());
        assertThrows(OrchestratorException.class, () -> builder.withAction("fail").build(), "error for unknown selenium test should be thrown");
    }

    @Test
    public void testSetSystemPropertyPostConstruct(){
        ReflectionTestUtils.setField(builder, "driverPath", "test");
        ReflectionTestUtils.setField(builder, "driverType", "testo");
        builder.setSeleniumDriverProperty();
        var setProp = System.getProperty("testo");
        assertEquals("test", setProp, "properties should be equal");
    }
}
