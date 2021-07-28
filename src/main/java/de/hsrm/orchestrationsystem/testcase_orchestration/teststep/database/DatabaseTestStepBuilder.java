package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.AbstractBasicTestStepBuilder;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component("database")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DatabaseTestStepBuilder extends AbstractBasicTestStepBuilder {

    DatabaseConnectionManager connectionManager;

    String[] DB_STEP_KEYWORDS = new String[]{"dbType", "dbUsername", "dbPassword", "dbName"};

    @Override
    public TestStep build() {
        Arrays.stream(DB_STEP_KEYWORDS).forEach(key -> {
            if (!options.containsKey(key))
                throw new OrchestratorException(key + " not properly set for step: " + this.name);
        });
        var script = new ClassPathResource(this.action);
        var type = (String) options.get("dbType");
        var username = (String) options.get("dbUsername");
        var password = (String) options.get("dbPassword");
        var dbName = (String) options.get("dbName");

        var source = connectionManager.getDatabaseConnection(type, this.target, dbName, username, password);
        var out = new DatabaseTestStep(script, source);
        out.setName(this.name);
        out.setTimeout(this.timeout);
        return out;
    }
}
