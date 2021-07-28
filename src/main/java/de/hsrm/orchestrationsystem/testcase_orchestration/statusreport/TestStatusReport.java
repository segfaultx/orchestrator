package de.hsrm.orchestrationsystem.testcase_orchestration.statusreport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.hsrm.orchestrationsystem.testcase_orchestration.crossapplicationtest.CrossApplicationTest;
import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestStatusReport {

    String name;

    @NonFinal
    TestStatus status;

    String message;

    @JsonFormat(pattern = "HH:mm:ss - dd.MM.yyyy")
    @NonFinal
    LocalDateTime startTime;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    Map<String, TestStatusReport> children = new HashMap<>();


    public TestStatusReport (String name, TestStatus status, LocalDateTime startTime){
        this.name = name;
        this.status = status;
        this.startTime = startTime;
        this.message = null;
    }


    public TestStatusReport(String name, TestStatus status, LocalDateTime startTime, String message){
        this.name = name;
        this.status = status;
        this.startTime = startTime;
        this.message = message;
    }


    public void addChild(TestStatusReport child){
        this.children.put(child.getName(), child);
    }

    public static TestStatusReport of(CrossApplicationTest test, TestStatus status, List<TestStep> steps){
        var out = new TestStatusReport(test.getName(), status, test.getStartTime());
        var children = steps
                .stream()
                .map(step -> new TestStatusReport(step.getName(), step.getStatus(), step.getStartTime(), step.getMessage()))
                .collect(Collectors.toList());
        children.forEach(out::addChild);
        return out;
    }

    public void convertToFinishedReport(){
        this.children.clear();
    }
}
