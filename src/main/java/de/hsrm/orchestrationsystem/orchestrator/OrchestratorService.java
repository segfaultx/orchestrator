package de.hsrm.orchestrationsystem.orchestrator;

import de.hsrm.orchestrationsystem.testcase_orchestration.creator.TestCreator;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.TestCaseException;
import de.hsrm.orchestrationsystem.testcase_orchestration.statusreport.TestStatusReport;
import de.hsrm.orchestrationsystem.testcase_orchestration.testcase.TestCase;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(value = WebApplicationContext.SCOPE_APPLICATION)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrchestratorService implements Orchestrator {

    Map<String, TestCase> suites = new HashMap<>();

    TestCreator testCreator;

    @Value("${orchestrator.yaml.basepath}")
    @NonFinal
    String YAML_BASEPATH;

    @Override
    public void startTest(String testName) {
        checkIfTestExists(testName);
        this.suites.get(testName).run();
    }

    @PostConstruct
    public void init() throws IOException {
        var basePath = new ClassPathResource(YAML_BASEPATH).getFile().toPath();
        try (var paths = Files.walk(basePath)) {
            paths.filter(path ->
                    Files.isRegularFile(path) &&
                            (path.toString().endsWith(".yml") || path.toString().endsWith(".yaml")))
                    .forEach(path -> {
                        var suite = testCreator.createTestSuite(path);
                        this.suites.put(suite.getName(), suite);
                    });
        }
    }

    @Override
    public void stopTest(String testName) {
        checkIfTestExists(testName);
        this.suites.get(testName).cancel();
    }

    @Override
    public void startTests(List<String> testNames) {
        testNames.forEach(this::checkIfTestExists);
        testNames.forEach(test -> this.suites.get(test).run());
    }

    private void checkIfTestExists(String testName){
        if (!this.suites.containsKey(testName))
            throw new TestCaseException(String.format("Unknown test: %s", testName));
    }

    @Override
    public TestStatusReport getTestStatus(String testName) {
        checkIfTestExists(testName);
        return this.suites.get(testName).getStatusReport();
    }

    @Override
    public List<TestStatusReport> getAllTestStatuses() {
        return this.suites.
                values()
                .stream()
                .map(TestCase::getStatusReport)
                .collect(Collectors.toList());
    }
}
