package de.hsrm.orchestrationsystem.rest.orchestrator_rest;

import de.hsrm.orchestrationsystem.orchestrator.Orchestrator;
import de.hsrm.orchestrationsystem.testcase_orchestration.statusreport.TestStatusReport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orchestration")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class OrchestratorRestController {

    Orchestrator orchestrator;


    @PostMapping("{test}")
    public ResponseEntity<String> startTest(@PathVariable String test) {
        orchestrator.startTest(test);
        return ResponseEntity.ok(String.format("Started test: %s", test));
    }

    @GetMapping("status/{test}")
    public ResponseEntity<TestStatusReport> getStatus(@PathVariable String test) {
        var status = orchestrator.getTestStatus(test);
        return ResponseEntity.ok(status);
    }

    @GetMapping("status")
    public ResponseEntity<List<TestStatusReport>> getAllStatuses() {
        return ResponseEntity.ok(orchestrator.getAllTestStatuses());
    }

    @PostMapping
    public ResponseEntity<String> startTests(@RequestParam(name = "test") List<String> tests) {
        orchestrator.startTests(tests);
        return ResponseEntity.ok(String.format("Started tests: %s", tests));
    }

    @PostMapping("stop/{testName}")
    public ResponseEntity<String> stopTest(@PathVariable String testName) {
        orchestrator.stopTest(testName);
        return ResponseEntity.ok(String.format("Stopped test: %s", testName));
    }
}
