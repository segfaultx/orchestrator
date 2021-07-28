package de.hsrm.orchestrationsystem.testcase_orchestration.reader;


import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestDescription;

import java.nio.file.Path;

public interface TestDescriptionReader {

    TestDescription readTestDescription(Path path);
}
