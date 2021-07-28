package de.hsrm.orchestrationsystem.testcase_orchestration.description;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Valid
public class TestStepDescription {

    @NotNull
    String name;

    @NotNull
    String type;

    @NotNull
    String action;

    @NotNull
    String target;

    @Getter(AccessLevel.NONE)
    boolean timeout = false;

    Integer timeoutDuration = -1;

    @Getter(AccessLevel.NONE)
    boolean retry = false;

    Integer retryAttempts = -1;

    Map<String, Object> options = new HashMap<>();

    public boolean getRetry() {
        return this.retry;
    }

    public boolean getTimeout() {
        return this.timeout;
    }

}
