package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.selenium;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.AbstractBasicTestStepBuilder;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import de.hsrm.orchestrationsystem.tests.selenium.SeleniumTest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service("selenium")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SeleniumTestStepBuilder extends AbstractBasicTestStepBuilder {

    Map<String, SeleniumTest> seleniumTestMap;

    @Value("${orchestrator.selenium.driver.location}")
    @NonFinal
    String driverPath;

    @Value("${orchestrator.selenium.driver.type}")
    @NonFinal
    String driverType;

    @Value("#{${orchestrator.selenium.target.systems}}")
    Map<String, String> configuredTargets;

    @PostConstruct
    public void setSeleniumDriverProperty() {
        System.setProperty(driverType, driverPath);
    }

    @Override
    public TestStep build() {
        if (!this.seleniumTestMap.containsKey(this.action))
            throw new OrchestratorException("Unknown selenium step: " + this.action);
        if(!this.configuredTargets.containsKey(this.target))
            throw new OrchestratorException("Unknown selenium testing target: " + this.target);
        var seleniumTarget = this.configuredTargets.get(this.target);
        var test = seleniumTestMap.get(this.action).newInstance(seleniumTarget, this.options);
        var out = new SeleniumTestStep(test);
        out.setName(this.name);
        out.setTarget(this.target);
        return out;
    }
}
