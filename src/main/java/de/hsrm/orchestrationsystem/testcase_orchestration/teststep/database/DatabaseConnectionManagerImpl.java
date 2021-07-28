package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseConnectionManagerImpl implements DatabaseConnectionManager {

    final Map<String, Map<String, DataSource>> connections = new HashMap<>();

    @Value("#{${orchestrator.data.systems}}")
    Map<String, String> configuredSystems;

    @Value("#{${orchestrator.data.drivers}}")
    Map<String, String> configuredDrivers;

    @Value("#{${orchestrator.data.ports}}")
    Map<String, String> configuredDbPorts;

    final int DBCONN_TIMEOUT = 5;

    final DataSourceBuilder<?> builder = DataSourceBuilder.create();

    @PreDestroy
    public void closeAllDbConnections() {
        log.info("closing db connections");
        this.connections.values().forEach(item -> item.values().forEach(conn -> {
            try {
                conn.getConnection().close();
            } catch (SQLException ex) {
                log.error("error closing db connection");
                throw new OrchestratorException("couldn't close db connection, message: " + ex.getMessage());
            }
        }));
    }

    private void validateConfiguredValuesExist(String type, String target){
        if (!configuredDrivers.containsKey(type))
            throw new OrchestratorException("unknown db type: " + type);
        builder.driverClassName(configuredDrivers.get(type));
        if (!configuredSystems.containsKey(target))
            throw new OrchestratorException("unknown system: " + target);
        if (!configuredDbPorts.containsKey(type))
            throw new OrchestratorException("no db port configured for type: " + type);

    }

    private void validateDatabaseConncetion(DataSource source){
        try {
            if(!source.getConnection().isValid(DBCONN_TIMEOUT))
                throw new OrchestratorException("Invalid database connection");
        }catch (SQLException ex){
            throw new OrchestratorException("Error creating Database connection, message: " + ex.getMessage());
        }
    }

    @Override
    public DataSource getDatabaseConnection(String type, String target, String databaseName, String username, String password) {
        log.info("getting database connection for target: {}, type: {}", target, type);
        if (this.connections.containsKey(target) && this.connections.get(target).containsKey(type))
            return this.connections.get(target).get(type);

        log.info("creating new database connection for target: {}, type: {}", target, type);
        validateConfiguredValuesExist(type, target);
        builder.url(String.format("jdbc:%s://%s:%s/%s", type, this.configuredSystems.get(target), configuredDbPorts.get(type), databaseName));
        builder.username(username);
        builder.password(password);
        DataSource source = builder.build();
        validateDatabaseConncetion(source);
        if (!connections.containsKey(target))
            connections.put(target, new HashMap<>());
        connections.get(target).put(type, source);

        return source;
    }
}
