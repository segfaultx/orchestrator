package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.conditional.conditions;

import java.util.Map;

public interface TestCondition {

    boolean check();

    TestCondition parameterizedInstance(String target, Map<String, Object> options);
}
