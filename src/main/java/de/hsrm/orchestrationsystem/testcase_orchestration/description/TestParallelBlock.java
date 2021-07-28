package de.hsrm.orchestrationsystem.testcase_orchestration.description;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Valid
public class TestParallelBlock implements TestControlBlock {

    String name;

    @NotEmpty
    @Valid
    List<@Valid TestStepDescription> steps;

    @Valid
    List<@Valid TestSequenceBlock> sequence = new ArrayList<>();

    @Valid
    List<@Valid TestParallelBlock> parallel = new ArrayList<>();
}
