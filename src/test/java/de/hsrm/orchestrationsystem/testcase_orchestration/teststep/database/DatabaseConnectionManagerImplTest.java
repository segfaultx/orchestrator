package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database.DatabaseConnectionManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class DatabaseConnectionManagerImplTest {

    @InjectMocks
    DatabaseConnectionManagerImpl databaseConnectionManager;

    @Mock
    DataSourceBuilder mockBuilder;

    @Mock
    DataSource mockDataSource;

    @Mock
    Connection mockConnection;


    @BeforeEach
    private void setupConnectionManager() {
        var testMap = new HashMap<String, String>();
        testMap.put("test", "test");
        setConnectionmanagerField("configuredSystems", testMap);
        setConnectionmanagerField("configuredDrivers", testMap);
        setConnectionmanagerField("configuredDbPorts", testMap);
        ReflectionTestUtils.setField(databaseConnectionManager, "connections", new HashMap<>());
    }

    private void setConnectionmanagerField(String fieldName, Map<String, String> value) {
        ReflectionTestUtils.setField(databaseConnectionManager, fieldName, value);
    }

    private void setupConnectionsForConnectionManager() {
        var connectionsMap = new HashMap<String, Map<String, DataSource>>();
        var mockConnectionMap = new HashMap<String, DataSource>();
        mockConnectionMap.put("test", mockDataSource);
        connectionsMap.put("test", mockConnectionMap);
        ReflectionTestUtils.setField(databaseConnectionManager, "connections", connectionsMap);
    }

    private void setConnectionManagerMockBuilder() throws SQLException {
        Mockito.when(mockBuilder.build()).thenReturn(mockDataSource);
        Mockito.when(mockDataSource.getConnection()).thenReturn(mockConnection);
        Mockito.when(mockConnection.isValid(Mockito.anyInt())).thenReturn(true);
        ReflectionTestUtils.setField(databaseConnectionManager, "builder", mockBuilder);
    }

    @Test
    public void testConnectionmanagerImplGetDbConnectionExists() {
        setupConnectionsForConnectionManager();
        assertEquals(mockDataSource, databaseConnectionManager.getDatabaseConnection("test",
                "test", "test", "test", "test"),
                "should be the same connection object");
    }

    @Test
    public void testConnectionManagerImplGetDbConnectionNewCreation() throws SQLException {
        setConnectionManagerMockBuilder();
        assertEquals(mockDataSource, databaseConnectionManager.getDatabaseConnection(
                "test",
                "test",
                "test",
                "test",
                "test"),
                "should return the mock connection");
    }

    @Test
    public void testConnectionManagerImplGetDbConnectionNewCreationError() throws SQLException {
        setConnectionManagerMockBuilder();
        Mockito.when(mockConnection.isValid(Mockito.anyInt())).thenReturn(false);
        assertThrows(OrchestratorException.class, () -> databaseConnectionManager.getDatabaseConnection(
                "test",
                "test",
                "test",
                "test",
                "test"),
                "should throw an error for invalid connection");
        Mockito.when(mockConnection.isValid(Mockito.anyInt())).thenThrow(SQLException.class);
        assertThrows(OrchestratorException.class, () -> databaseConnectionManager.getDatabaseConnection(
                "test",
                "test",
                "test",
                "test",
                "test"),
                "should throw an error for invalid connection");
    }

    @Test
    public void testConnectionManagerImplGetDBConnectionErrors() {
        assertThrows(OrchestratorException.class, () -> databaseConnectionManager
                        .getDatabaseConnection("", "", "",
                                "", ""),
                "error is thrown for invalid parameters (all)");
        assertThrows(OrchestratorException.class, () -> databaseConnectionManager
                        .getDatabaseConnection("", "test", "test",
                                "test", "test"),
                "error is thrown for invalid parameters (1st)");
        assertThrows(OrchestratorException.class, () -> databaseConnectionManager
                        .getDatabaseConnection("test", "", "test",
                                "test", "test"),
                "error is thrown for invalid parameters (2nd)");
        ReflectionTestUtils.setField(databaseConnectionManager, "configuredDbPorts", new HashMap<>());
        assertThrows(OrchestratorException.class, () -> databaseConnectionManager
                        .getDatabaseConnection("test", "test", "test",
                                "test", "test"),
                "error is thrown for invalid parameters (unknown port)");
    }

    @Test
    public void testConnectionManagerPreDestruct() throws SQLException {
        setupConnectionsForConnectionManager();
        Mockito.when(mockDataSource.getConnection()).thenReturn(mockConnection);
        databaseConnectionManager.closeAllDbConnections();
        Mockito.verify(mockConnection, Mockito.times(1)).close();
    }

    @Test
    public void testConnectionManagerPreDestructFailure() throws SQLException {
        setupConnectionsForConnectionManager();
        Mockito.when(mockDataSource.getConnection()).thenReturn(mockConnection);
        Mockito.doThrow(SQLException.class).when(mockConnection).close();
        assertThrows(OrchestratorException.class, () -> databaseConnectionManager.closeAllDbConnections(), "should throw an error if close fails");
        Mockito.verify(mockConnection, Mockito.times(1)).close();
    }
}
