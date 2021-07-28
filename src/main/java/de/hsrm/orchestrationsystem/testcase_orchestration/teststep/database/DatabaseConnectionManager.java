package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database;

import javax.sql.DataSource;

public interface DatabaseConnectionManager {

    DataSource getDatabaseConnection(String type, String target, String databaseName, String username, String password);
}
