package de.hsrm.orchestrationsystem.testcase_orchestration.creator;

import de.hsrm.orchestrationsystem.testcase_orchestration.crossapplicationtest.CrossApplicationTest;
import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestDescription;
import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestParallelBlock;
import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestSequenceBlock;
import de.hsrm.orchestrationsystem.testcase_orchestration.description.TestStepDescription;
import de.hsrm.orchestrationsystem.testcase_orchestration.reader.YAMLTestDescriptionReader;
import de.hsrm.orchestrationsystem.testcase_orchestration.testcase.TestCase;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.TestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststep.selenium.SeleniumTestStep;
import de.hsrm.orchestrationsystem.testcase_orchestration.teststepservice.BasicStepService;
import de.hsrm.orchestrationsystem.testcase_orchestration.validator.BasicOrchestratorValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BasicTestCreatorTest {

    @MockBean
    @Mock
    YAMLTestDescriptionReader reader;

    @MockBean
    @Mock
    BasicStepService stepService;

    @MockBean
    @Mock
    BasicOrchestratorValidator validator;

    @Mock
    SeleniumTestStep mockTestStep;

    @Mock
    SeleniumTestStep mockTestStepTwo;

    @Mock
    SeleniumTestStep mockTestStepThree;

    TestDescription mockDescription;

    @InjectMocks
    @Spy
    BasicTestCreator creator;

    @BeforeEach
    private void setupSimpleDescription() {
        mockDescription = new TestDescription();
        mockDescription.setName("Test");
        var mockParallelBlock = new TestParallelBlock();
        mockParallelBlock.setName("Parallel A");
        var mockTestStepDescription = new TestStepDescription();
        mockTestStepDescription.setName("Teststep A");
        mockTestStepDescription.setAction("Action A");
        mockTestStepDescription.setRetry(false);
        mockTestStepDescription.setTimeout(false);
        mockTestStepDescription.setType("Type A");
        mockTestStepDescription.setTarget("Target A");
        mockParallelBlock.setSteps(Collections.singletonList(mockTestStepDescription));
        mockDescription.setParallel(mockParallelBlock);
        Mockito.when(reader.readTestDescription(Mockito.any())).thenReturn(mockDescription);
        Mockito.when(mockTestStep.getName()).thenReturn("Teststep A");
        Mockito.when(stepService.getStepForDescription(Mockito.any())).thenReturn(mockTestStep, mockTestStepTwo, mockTestStepThree);

    }

    private void addSimpleParallelDependencyToDescription() {
        var mockParallelBlock = mockDescription.getParallel();
        var mockInnerParallelBlock = new TestParallelBlock();
        var mockTestStepDescriptionTwo = new TestStepDescription();
        mockTestStepDescriptionTwo.setName("Teststep B");
        Mockito.when(mockTestStepTwo.getName()).thenReturn("Teststep B");
        mockInnerParallelBlock.setSteps(Collections.singletonList(mockTestStepDescriptionTwo));
        mockParallelBlock.setParallel(Collections.singletonList(mockInnerParallelBlock));
    }

    private void addSimpleSequenceDependencyToDescription() {
        var mockParallelBlock = mockDescription.getParallel();
        var mockInnerSequenceBlock = new TestSequenceBlock();
        Mockito.when(mockTestStepTwo.getName()).thenReturn("Teststep B");
        Mockito.when(mockTestStepThree.getName()).thenReturn("Teststep C");
        var mocksteps = prepateMockTestSteps();
        mockInnerSequenceBlock.setSteps(mocksteps.subList(1, mocksteps.size() -1));
        mockParallelBlock.setSequence(Collections.singletonList(mockInnerSequenceBlock));
    }

    private List<TestStepDescription> prepateMockTestSteps() {
        var mockTestStepDescription = new TestStepDescription();
        mockTestStepDescription.setName("Teststep A");
        mockTestStepDescription.setAction("Action A");
        mockTestStepDescription.setRetry(false);
        mockTestStepDescription.setTimeout(false);
        mockTestStepDescription.setType("Type A");
        mockTestStepDescription.setTarget("Target A");

        var mockTestStepDescriptionTwo = new TestStepDescription();
        mockTestStepDescriptionTwo.setName("Teststep B");
        mockTestStepDescriptionTwo.setAction("Action B");
        mockTestStepDescriptionTwo.setRetry(false);
        mockTestStepDescriptionTwo.setTimeout(false);
        mockTestStepDescriptionTwo.setType("Type B");
        mockTestStepDescriptionTwo.setTarget("Target B");

        var mockTestStepDescriptionThree = new TestStepDescription();
        mockTestStepDescriptionThree.setName("Teststep C");
        mockTestStepDescriptionThree.setAction("Action C");
        mockTestStepDescriptionThree.setRetry(false);
        mockTestStepDescriptionThree.setTimeout(false);
        mockTestStepDescriptionThree.setType("Type C");
        mockTestStepDescriptionThree.setTarget("Target C");

        return Arrays.asList(mockTestStepDescription, mockTestStepDescriptionTwo, mockTestStepDescriptionThree);
    }

    @Test
    public void testCreatorCreateTestSuiteNoDependencies() {
        var result = creator.createTestSuite(Mockito.any());
        Mockito.verify(reader, Mockito.times(1)).readTestDescription(Mockito.any());
        Mockito.verify(stepService, Mockito.times(1)).getStepForDescription(Mockito.any());
        assertStepsEquals(Collections.singletonList(mockTestStep), result);
    }

    @Test
    public void testCreatorCreateTestSuiteSimpleDependencies() {
        addSimpleParallelDependencyToDescription();
        var result = creator.createTestSuite(Mockito.any());
        Mockito.verify(reader, Mockito.times(1)).readTestDescription(Mockito.any());
        Mockito.verify(stepService, Mockito.times(2)).getStepForDescription(Mockito.any());
        assertStepsEquals(Arrays.asList(mockTestStep, mockTestStepTwo), result);
        var simpleDependencyMap = new HashMap<TestStep, List<TestStep>>();
        simpleDependencyMap.put(mockTestStepTwo, Collections.singletonList(mockTestStep));
        assertDependencyEquals(simpleDependencyMap, result);
    }


    @Test
    public void testCreatorCreateTestSuiteSimpleSequenceDependency() {
        var mockDescription = new TestDescription();
        mockDescription.setName("Test");
        var mockSequenceBlock = new TestSequenceBlock();
        var mockTestStepDescriptions = prepateMockTestSteps();
        mockSequenceBlock.setSteps(Arrays.asList(mockTestStepDescriptions.get(0), mockTestStepDescriptions.get(1)));
        mockDescription.setSequence(mockSequenceBlock);
        Mockito.when(reader.readTestDescription(Mockito.any())).thenReturn(mockDescription);
        Mockito.when(mockTestStepTwo.getName()).thenReturn("Teststep B");
        var result = creator.createTestSuite(Mockito.any());
        var sequenceDependencyMap = new HashMap<TestStep, List<TestStep>>();
        sequenceDependencyMap.put(mockTestStepTwo, Collections.singletonList(mockTestStep));
        assertDependencyEquals(sequenceDependencyMap, result);
        assertStepsEquals(Arrays.asList(mockTestStep, mockTestStepTwo), result);
    }

    @Test
    public void testCreatorCreateTestSuiteSequenceDependencyParentParallel() {
        var mockSequenceBlock = new TestSequenceBlock();
        mockSequenceBlock.setSteps(prepateMockTestSteps().subList(1,3));
        mockDescription.getParallel().setSequence(Collections.singletonList(mockSequenceBlock));
        Mockito.when(reader.readTestDescription(Mockito.any())).thenReturn(mockDescription);
        Mockito.when(mockTestStepTwo.getName()).thenReturn("Teststep B");
        Mockito.when(mockTestStepThree.getName()).thenReturn("Teststep C");
        var result = creator.createTestSuite(Mockito.any());
        var sequenceDependencyMap = new HashMap<TestStep, List<TestStep>>();
        sequenceDependencyMap.put(mockTestStepTwo, Collections.singletonList(mockTestStep));
        sequenceDependencyMap.put(mockTestStepThree, Collections.singletonList(mockTestStepTwo));
        assertDependencyEquals(sequenceDependencyMap, result);
        assertStepsEquals(Arrays.asList(mockTestStep, mockTestStepTwo, mockTestStepThree), result);
    }

    @Test
    public void testCreatorTestSequenceDependenciesToParallelParent() {
        var mockSequenceBlock = new TestSequenceBlock();
        var mocktestStepDescriptions = prepateMockTestSteps();
        mockSequenceBlock.setSteps(mocktestStepDescriptions.subList(2,3));
        mockDescription.getParallel().setSteps(mocktestStepDescriptions.subList(0,2));
        mockDescription.getParallel().setSequence(Collections.singletonList(mockSequenceBlock));
        Mockito.when(reader.readTestDescription(Mockito.any())).thenReturn(mockDescription);
        Mockito.when(mockTestStepTwo.getName()).thenReturn("Teststep B");
        Mockito.when(mockTestStepThree.getName()).thenReturn("Teststep C");
        var result = creator.createTestSuite(Mockito.any());
        var sequenceDependencyMap = new HashMap<TestStep, List<TestStep>>();
        sequenceDependencyMap.put(mockTestStepThree, Arrays.asList(mockTestStep, mockTestStepTwo));
        assertDependencyEquals(sequenceDependencyMap, result);
        assertStepsEquals(Arrays.asList(mockTestStep, mockTestStepTwo, mockTestStepThree), result);
    }

    @Test
    public void testCreatorTestSequenceDependenciesToSequenceParent() {
        var mockSequenceBlock = new TestSequenceBlock();
        var mocktestStepDescriptions = prepateMockTestSteps();
        mockDescription.setParallel(null);
        mockDescription.setSequence(mockSequenceBlock);
        mockSequenceBlock.setSteps(Collections.singletonList(mocktestStepDescriptions.get(0)));
        var mockInnerSequenceBlock = new TestSequenceBlock();
        mockInnerSequenceBlock.setSteps(mocktestStepDescriptions.subList(1,3));
        mockSequenceBlock.setSequence(Collections.singletonList(mockInnerSequenceBlock));

        Mockito.when(reader.readTestDescription(Mockito.any())).thenReturn(mockDescription);
        Mockito.when(mockTestStepTwo.getName()).thenReturn("Teststep B");
        Mockito.when(mockTestStepThree.getName()).thenReturn("Teststep C");
        var result = creator.createTestSuite(Mockito.any());
        var sequenceDependencyMap = new HashMap<TestStep, List<TestStep>>();
        sequenceDependencyMap.put(mockTestStepTwo, Collections.singletonList(mockTestStep));
        sequenceDependencyMap.put(mockTestStepThree, Collections.singletonList(mockTestStepTwo));
        assertDependencyEquals(sequenceDependencyMap, result);
        assertStepsEquals(Arrays.asList(mockTestStep, mockTestStepTwo, mockTestStepThree), result);
    }


    @Test
    public void testCreatorTestParallelDependenciesToSequenceParent() {
        var mockSequenceBlock = new TestSequenceBlock();
        var mocktestStepDescriptions = prepateMockTestSteps();
        mockDescription.setParallel(null);
        mockDescription.setSequence(mockSequenceBlock);
        mockSequenceBlock.setSteps(mocktestStepDescriptions.subList(0,2));
        var mockInnerParallelBlock = new TestParallelBlock();
        mockInnerParallelBlock.setSteps(mocktestStepDescriptions.subList(2,3));
        mockSequenceBlock.setParallel(Collections.singletonList(mockInnerParallelBlock));

        Mockito.when(reader.readTestDescription(Mockito.any())).thenReturn(mockDescription);
        Mockito.when(mockTestStepTwo.getName()).thenReturn("Teststep B");
        Mockito.when(mockTestStepThree.getName()).thenReturn("Teststep C");
        var result = creator.createTestSuite(Mockito.any());
        var sequenceDependencyMap = new HashMap<TestStep, List<TestStep>>();


        sequenceDependencyMap.put(mockTestStepTwo, Collections.singletonList(mockTestStep));
        sequenceDependencyMap.put(mockTestStepThree, Collections.singletonList(mockTestStepTwo));
        assertDependencyEquals(sequenceDependencyMap, result);
        assertStepsEquals(Arrays.asList(mockTestStep, mockTestStepTwo, mockTestStepThree), result);
    }








    private void assertStepsEquals(List<TestStep> expected, TestCase result) {
        var resultsTests = (Map<String, CrossApplicationTest>) ReflectionTestUtils.getField(result, "tests");
        var resultTest = resultsTests.get("Test");
        var resultTestSteps = (List<TestStep>) ReflectionTestUtils.getField(resultTest, "STEPS");
        assertThat(expected).hasSameElementsAs(resultTestSteps);
    }

    private void assertDependencyEquals(Map<TestStep, List<TestStep>> expected, TestCase result) {
        var resultsTests = (Map<String, CrossApplicationTest>) ReflectionTestUtils.getField(result, "tests");
        var resultTest = resultsTests.get("Test");
        var resultTestStepDependencies = (Map<TestStep, List<TestStep>>) ReflectionTestUtils.getField(resultTest, "dependencies");
        assertThat(expected).isEqualTo(resultTestStepDependencies);
    }

}
