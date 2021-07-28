package de.hsrm.orchestrationsystem.testcase_orchestration.teststepservice;

import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestStepDescription;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;


public interface TestStepService {


    TestStep getStepForDescription(TestStepDescription description);
}
