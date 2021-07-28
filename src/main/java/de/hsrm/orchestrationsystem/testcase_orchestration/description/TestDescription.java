package de.hsrm.orchestrationsystem.testcase_orchestration.description;

import de.hsrm.orchestrationsystem.testcase_orchestration.validator.groups.ValidateParallelAsRoot;
import de.hsrm.orchestrationsystem.testcase_orchestration.validator.groups.ValidateSequenceAsRoot;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Data
@Valid
public class TestDescription {

    String name;

    @NotNull(groups = ValidateSequenceAsRoot.class)
    @Valid
    TestSequenceBlock sequence;

    @NotNull(groups = ValidateParallelAsRoot.class)
    @Valid
    TestParallelBlock parallel;
}
