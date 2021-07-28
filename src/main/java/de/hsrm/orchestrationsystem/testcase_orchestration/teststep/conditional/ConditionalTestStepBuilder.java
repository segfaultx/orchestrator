package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.conditional;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestStepDescription;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.AbstractBasicTestStepBuilder;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.conditional.conditions.TestCondition;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststepservice.TestStepService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("conditional")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ConditionalTestStepBuilder extends AbstractBasicTestStepBuilder {

    TestStepService stepService;

    Map<String, TestCondition> conditions;

    @Value("#{${orchestrator.target.systems}}")
    Map<String,String> configuredTargets;

    @Autowired
    public void setStepService(@Lazy TestStepService stepService) {
        this.stepService = stepService;
    }

    @Autowired
    public void setConditions(Map<String, TestCondition> conditions){
        this.conditions = conditions;
    }

    private TestStep getStepFromDescription(TestStepDescription desc){
        desc.setTarget(this.target);
        return stepService.getStepForDescription(desc);
    }

    private ConditionalTestStep newConditionalTestStepWithParameters(TestCondition IF, TestStep THEN, TestStep ELSE){
        var out = new ConditionalTestStep(IF, THEN, ELSE);
        out.setName(this.name);
        out.setAction(this.action);
        out.setTarget(this.target);
        out.setTimeout(this.timeout);
        return out;
    }

    @Override
    public TestStep build() {
        if (!this.options.containsKey("then") || !this.options.containsKey("else"))
            throw new OrchestratorException("missing then or else option, please check");
        if (!this.conditions.containsKey(this.action))
            throw new OrchestratorException("unknown action for conditional step: " + this.action);
        if (!this.configuredTargets.containsKey(this.target))
            throw new OrchestratorException("unknown target system: " + this.target);
        var mapper = new ObjectMapper();
        var thenDesc = mapper.convertValue(this.options.get("then"), TestStepDescription.class);
        var elseDesc = mapper.convertValue(this.options.get("else"), TestStepDescription.class);
        var thenStep = getStepFromDescription(thenDesc);
        var elseStep = getStepFromDescription(elseDesc);
        this.target = this.configuredTargets.get(this.target);
        var ifStep = this.conditions.get(this.action).parameterizedInstance(this.target, this.options);
        return newConditionalTestStepWithParameters(ifStep, thenStep, elseStep);
    }
}
