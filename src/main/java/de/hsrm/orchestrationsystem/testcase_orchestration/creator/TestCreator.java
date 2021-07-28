package de.hsrm.orchestrationsystem.testcase_orchestration.creator;

import de.hsrm.orchestrationsystem.testcase_orchestration.testcase.TestCase;

import java.nio.file.Path;

public interface TestCreator {

    TestCase createTestSuite(Path path);
}
