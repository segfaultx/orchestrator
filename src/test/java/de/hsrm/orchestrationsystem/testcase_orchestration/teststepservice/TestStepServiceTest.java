package de.hsrm.orchestrationsystem.testcase_orchestration.teststepservice;

import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestStepDescription;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.StepBuilderRegistrationException;
import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.UnknownStepTypeException;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStepBuilder;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database.DatabaseTestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.database.DatabaseTestStepBuilder;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.selenium.SeleniumTestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.selenium.SeleniumTestStepBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestStepServiceTest {

    @Mock
    SeleniumTestStepBuilder seleniumTestStepBuilder;

    @Mock
    DatabaseTestStepBuilder databaseTestStepBuilder;

    @InjectMocks
    BasicStepService stepService;


    @Test
    public void testConfigureStepBuilders() {
        Map<String, TestStepBuilder> builders = new HashMap<>();
        builders.put("selenium", seleniumTestStepBuilder);
        builders.put("database", databaseTestStepBuilder);
        ReflectionTestUtils.setField(stepService, "stepBuilders", builders);
        Map<String, String> config = new HashMap<>();
        config.put("user_interaction", "selenium");
        config.put("data", "database");
        ReflectionTestUtils.setField(stepService, "stepsConfig", config);
        stepService.setConfiguredStepBuilders();
    }

    @Test
    public void testConfigureStepBuildersFailure() {
        Map<String, TestStepBuilder> builders = new HashMap<>();
        builders.put("selenium", seleniumTestStepBuilder);
        builders.put("database", databaseTestStepBuilder);
        ReflectionTestUtils.setField(stepService, "stepBuilders", builders);
        Map<String, String> config = new HashMap<>();
        config.put("user_interaction", "not selenium");
        config.put("data", "database");
        ReflectionTestUtils.setField(stepService, "stepsConfig", config);
        assertThrows(StepBuilderRegistrationException.class, () -> stepService.setConfiguredStepBuilders(),
                "should throw an error for unknown builder type");
    }

    private void setupTestStepBuilder(TestStepBuilder builder) {
        Mockito.when(builder.New()).thenReturn(builder);
        Mockito.when(builder.withAction(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.withName(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.withTarget(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.withOptions(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.withTimeout(Mockito.anyBoolean(), Mockito.anyInt())).thenReturn(builder);
        Mockito.when(builder.withRetry(Mockito.anyBoolean(), Mockito.anyInt())).thenReturn(builder);
    }

    private void verifyBuilderCalls(TestStepBuilder builder) {
        Mockito.verify(builder, Mockito.times(1)).New();
        Mockito.verify(builder, Mockito.times(1)).withAction(Mockito.any());
        Mockito.verify(builder, Mockito.times(1)).withName(Mockito.any());
        Mockito.verify(builder, Mockito.times(1)).withTarget(Mockito.any());
        Mockito.verify(builder, Mockito.times(1)).withTimeout(Mockito.anyBoolean(), Mockito.anyInt());
        Mockito.verify(builder, Mockito.times(1)).withRetry(Mockito.anyBoolean(), Mockito.anyInt());
        Mockito.verify(builder, Mockito.times(1)).withOptions(Mockito.any());
        Mockito.verify(builder, Mockito.times(1)).build();
    }

    @Test
    public void testGetTestStepForDescription() {
        // setup config of service
        Map<String, TestStepBuilder> configuredBuilders = new HashMap<>();
        configuredBuilders.put("user_interaction", seleniumTestStepBuilder);
        configuredBuilders.put("data", databaseTestStepBuilder);
        ReflectionTestUtils.setField(stepService, "configuredStepBuilders", configuredBuilders);

        // test first builder

        setupTestStepBuilder(seleniumTestStepBuilder);
        var mockStep = Mockito.mock(SeleniumTestStep.class);
        Mockito.when(seleniumTestStepBuilder.build()).thenReturn(mockStep);
        var mockDescription = Mockito.mock(TestStepDescription.class);
        Mockito.when(mockDescription.getType()).thenReturn("user_interaction");
        var returnValue = stepService.getStepForDescription(mockDescription);
        assertEquals(mockStep, returnValue, "should return mocked teststep");
        verifyBuilderCalls(seleniumTestStepBuilder);
        Mockito.verify(databaseTestStepBuilder, Mockito.times(0)).New();

        // test second builder
        var mockStepTwo = Mockito.mock(DatabaseTestStep.class);
        Mockito.when(databaseTestStepBuilder.build()).thenReturn(mockStepTwo);
        var mockDescriptionTwo = Mockito.mock(TestStepDescription.class);
        Mockito.when(mockDescriptionTwo.getType()).thenReturn("data");
        setupTestStepBuilder(databaseTestStepBuilder);
        var returnValueTwo = stepService.getStepForDescription(mockDescriptionTwo);
        assertEquals(mockStepTwo, returnValueTwo, "should return second mocked step");
        verifyBuilderCalls(databaseTestStepBuilder);
    }

    @Test
    public void testGetTestStepForDescriptionError() {
        var mockDescription = Mockito.mock(TestStepDescription.class);
        Mockito.when(mockDescription.getType()).thenReturn("");
        assertThrows(UnknownStepTypeException.class, () -> stepService.getStepForDescription(mockDescription),
                "should throw an error for unknown step type");
    }
}
