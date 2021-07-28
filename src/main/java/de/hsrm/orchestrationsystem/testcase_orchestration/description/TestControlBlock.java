package de.hsrm.orchestrationsystem.testcase_orchestration.description;

import java.util.List;

public interface TestControlBlock {

    List<TestSequenceBlock> getSequence();

    List<TestParallelBlock> getParallel();

    List<TestStepDescription> getSteps();
}
