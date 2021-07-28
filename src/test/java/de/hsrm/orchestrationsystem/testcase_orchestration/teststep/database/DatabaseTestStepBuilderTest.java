package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database.DatabaseConnectionManagerImpl;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database.DatabaseTestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database.DatabaseTestStepBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DatabaseTestStepBuilderTest {

    @Mock
    DatabaseConnectionManagerImpl mockConnectionManager;

    @Mock
    DataSource mockDataSource;

    @InjectMocks
    DatabaseTestStepBuilder builder;

    @Test
    public void testDatabaseTestStepBuilderBuild() {
        Mockito.when(mockConnectionManager.getDatabaseConnection(Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString())
        ).thenReturn(mockDataSource);
        var optionsMap = new HashMap<String, Object>();
        optionsMap.put("dbType", "bla");
        optionsMap.put("dbName", "bla");
        optionsMap.put("dbUsername", "bla");
        optionsMap.put("dbPassword", "bla");
        var returnValue = builder.New()
                .withAction("action")
                .withName("the name")
                .withOptions(optionsMap)
                .withTarget(Mockito.anyString())
                .withRetry(Mockito.anyBoolean(), Mockito.anyInt())
                .withTimeout(Mockito.anyBoolean(), Mockito.anyInt())
                .build();
        assertTrue(returnValue instanceof DatabaseTestStep, "returned step should be a db step");
    }

    @Test
    public void testDatabaseTestStepBuilderBuildFail() {
        var configuredBuilder = builder.New()
                .withOptions(new HashMap<>());
        assertThrows(OrchestratorException.class, configuredBuilder::build,
                "should throw an error since keywords are not configured");
    }
}
