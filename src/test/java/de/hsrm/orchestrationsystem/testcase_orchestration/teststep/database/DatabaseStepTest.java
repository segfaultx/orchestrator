package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database;

import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database.DatabaseTestStep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.CannotReadScriptException;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DatabaseStepTest {

    @Mock
    DataSource mockDataSource;

    @Mock
    Resource mockScript;


    @Test
    public void testDatabaseStepRun() {
        var testStep = new DatabaseTestStep(mockScript, mockDataSource);
        var mockResourcePopulator = Mockito.mock(ResourceDatabasePopulator.class);
        ReflectionTestUtils.setField(testStep, "populator", mockResourcePopulator);
        testStep.run();
        assertSame(TestStatus.SUCCESS, testStep.getStatus(), "teststep should have finished cleanly");
    }

    @Test
    public void testDatabaseRunFailure() {
        var testStep = new DatabaseTestStep(mockScript, mockDataSource);
        var mockResourcePopulator = Mockito.mock(ResourceDatabasePopulator.class);
        Mockito.doThrow(CannotReadScriptException.class).when(mockResourcePopulator).execute(Mockito.any());
        ReflectionTestUtils.setField(testStep, "populator", mockResourcePopulator);
        testStep.run();
        assertSame(TestStatus.FAILED, testStep.getStatus(), "teststep should have finished cleanly");
    }

    @Test
    public void testDatabaseStepCancel() {
        var testStep = new DatabaseTestStep(mockScript, mockDataSource);
        testStep.cancel();
        assertSame(TestStatus.FAILED, testStep.getStatus(), "teststep should have failed");
    }


    @Test
    public void testDatabaseStepTimeout() {
        var testStep = new DatabaseTestStep(mockScript, mockDataSource);
        testStep.timeout();
        assertSame(TestStatus.FAILED, testStep.getStatus(), "teststep should have failed");
    }


    @Test
    public void testDatabaseStepReset() {
        var testStep = new DatabaseTestStep(mockScript, mockDataSource);
        testStep.reset();
        assertSame(TestStatus.PENDING, testStep.getStatus(), "teststep should have failed");
        assertEquals("", testStep.getMessage(), "errormessage should have been reset");
    }

    @Test
    public void testDatabaseStepConstructor() {
        var testStep = new DatabaseTestStep(mockScript, mockDataSource);
        assertNotNull(ReflectionTestUtils.getField(testStep, "script"), "script should have been set");
        assertNotNull(ReflectionTestUtils.getField(testStep, "dataSource"), "dataSource should have been set");
        assertNotNull(ReflectionTestUtils.getField(testStep, "populator"), "populator should have been initialized");
        assertEquals(mockScript, ReflectionTestUtils.getField(testStep, "script"), "should be the same script");
        assertEquals(mockDataSource, ReflectionTestUtils.getField(testStep, "dataSource"), "should be the same datasource");
        var returnValue = ReflectionTestUtils.getField(Objects.requireNonNull(ReflectionTestUtils.getField(testStep, "populator")), "scripts");
        List<Resource> scriptList = null;
        if (returnValue instanceof List){
            if (((List<?>) returnValue).get(0) instanceof Resource){
                scriptList = (List<Resource>) returnValue;
            }
        }
        assertNotNull(scriptList, "value should not be null");
        assertEquals(mockScript, scriptList.get(0));
    }
}
