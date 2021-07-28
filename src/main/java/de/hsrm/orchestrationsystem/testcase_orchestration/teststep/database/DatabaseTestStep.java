package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database;

import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.events.OrchestratorChangeEvent;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.AbstractBasicTestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;

import javax.sql.DataSource;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DatabaseTestStep extends AbstractBasicTestStep {

    DataSource dataSource;

    Resource script;

    ResourceDatabasePopulator populator;

    public DatabaseTestStep(Resource script, DataSource dataSource) {
        this.script = script;
        this.dataSource = dataSource;
        this.populator = new ResourceDatabasePopulator();
        this.populator.addScript(script);
    }

    @Override
    public void execute() {
        this.status = TestStatus.EXECUTING;
        try {
            log.info("EXECUTING Database Script: {}", this.script.getFilename());
            this.populator.execute(dataSource);
            this.status = TestStatus.SUCCESS;
        } catch (ScriptException ex) {
            this.message = ex.getMessage();
            this.status = TestStatus.FAILED;
        }
        fireStatusChangedEvent();
    }

    @Override
    public void cancel() {
        this.status = TestStatus.FAILED;
    }

    @Override
    public void retry() {
        if (this.retry && (this.currentRetryAttempts < this.retryAttempts)){
            this.status = TestStatus.PENDING;
            this.message = "";
        }
    }

    @Override
    public void timeout() {
        cancel();
    }

    @Override
    public void reset() {
        this.status = TestStatus.PENDING;
        this.message = "";
        this.currentRetryAttempts = 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
