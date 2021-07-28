package de.hsrm.orchestrationsystem.testcase_orchestration.crossapplicationtest;


import de.hsrm.orchestrationsystem.testcase_orchestration.enums.TestStatus;
import de.hsrm.orchestrationsystem.testcase_orchestration.events.OrchestratorChangeEvent;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.testcase_orchestration.listeners.OrchestratorTestStepChangeListener;
import de.hsrm.orchestrationsystem.testcase_orchestration.statusreport.TestStatusReport;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class BasicCrossApplicationTest implements CrossApplicationTest {

    final String name;

    LocalDateTime startTime;

    final List<TestStep> STEPS;

    final int MAXIMUM_PARALLEL_STEPS;

    final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    final Map<TestStep, List<TestStep>> dependencies;

    Map<TestStep, List<TestStep>> synchronizedDependencies;

    TestStatus status = TestStatus.PENDING;

    ThreadPoolExecutor executor;

    final Map<String, Object> context = Collections.synchronizedMap(new HashMap<>());

    private void init() {
        if (executor == null) {
            setupThreadPool();
            PropertyChangeListener listener = new OrchestratorTestStepChangeListener(this);
            STEPS.forEach(item -> {
                item.addChangeListener(listener);
                item.setContext(this.context);
            });
        }
        var copyMap = new HashMap<TestStep, List<TestStep>>();
        dependencies.forEach((entry, value) -> copyMap.put(entry, new ArrayList<>(value)));
        this.synchronizedDependencies = Collections.synchronizedMap(copyMap);
    }

    private void setupThreadPool() {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAXIMUM_PARALLEL_STEPS);
        this.executor.setMaximumPoolSize(MAXIMUM_PARALLEL_STEPS);
    }

    @Override
    public void run() {
        this.status = TestStatus.EXECUTING;
        this.startTime = LocalDateTime.now();
        init();
        runAllStepsWithoutDependencies();
    }

    @Override
    public void cancel() {
        this.status = TestStatus.FAILED;
        this.executor.shutdown();
        try {
            if (this.executor.awaitTermination(5L, TimeUnit.SECONDS)) {
                var oldStatus = this.status;
                this.status = TestStatus.FAILED;
                propertyChangeSupport.firePropertyChange(
                        new OrchestratorChangeEvent<CrossApplicationTest, TestStatus>(this, "status", oldStatus, this.status)
                );
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new OrchestratorException("Couldn't cancel Test Suite");
        } finally {
            fireReportEvent();
        }
    }

    @Override
    public synchronized void handleChangedEvent(TestStep step, TestStatus status) {
        switch (status) {
            case SUCCESS:
                updateDependencies();
                break;
            case FAILED: {
                step.retry();
                if (step.getStatus() == TestStatus.PENDING)
                    runAllStepsWithoutDependencies();
                else
                    cancel();
                break;
            }

        }
        fireReportEvent();
    }

    private synchronized void updateDependencies() {
        var iterator = synchronizedDependencies.entrySet().iterator();
        while (iterator.hasNext()) {
            var dependencyEntry = iterator.next();
            var val = dependencyEntry.getValue();
            val = val
                    .stream()
                    .filter(item -> item.getStatus() != TestStatus.SUCCESS)
                    .collect(Collectors.toList());
            if (val.isEmpty())
                iterator.remove();
        }
        runAllStepsWithoutDependencies();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    @Override
    public void addChangeListener(String property, PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(property, listener);
    }

    @Override
    public void reset() {
        this.status = TestStatus.PENDING;
        this.STEPS.forEach(TestStep::reset);
        setupThreadPool();
    }

    private void runAllStepsWithoutDependencies() {
        if (isTestSuiteFinished()) {
            log.info("Test Suite finished!");
            var oldStatus = this.status;
            this.status = TestStatus.SUCCESS;
            propertyChangeSupport.firePropertyChange(
                    new OrchestratorChangeEvent<CrossApplicationTest, TestStatus>(
                            this,
                            "status",
                            oldStatus,
                            this.status)
            );
            return;
        }
        for (var step : STEPS) {
            if (canRun(step))
                executor.submit(step);
        }
        fireReportEvent();
    }

    private void fireReportEvent() {
        propertyChangeSupport.firePropertyChange(
                new OrchestratorChangeEvent<CrossApplicationTest, TestStatusReport>(
                        this,
                        "report",
                        null,
                        TestStatusReport.of(this, this.status, this.STEPS)
                )
        );
    }

    private boolean isTestSuiteFinished() {
        return STEPS
                .stream()
                .allMatch(step -> step.getStatus() == TestStatus.SUCCESS);
    }

    private boolean hasDependency(TestStep step) {
        return synchronizedDependencies.get(step) != null;
    }

    private boolean canRun(TestStep step) {
        return !hasDependency(step) && step.getStatus() == TestStatus.PENDING;
    }
}
