package de.hsrm.orchestrationsystem.tests.selenium.wordpress;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.tests.selenium.AbstractSeleniumTestCase;
import de.hsrm.orchestrationsystem.tests.selenium.SeleniumTest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component("wp-registration-security-question")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class WordpressRegisterWithSecurityQuestion extends AbstractSeleniumTestCase {

    String target;

    Map<String, Object> options;

    final String DEFAULT_USERNAME = "dieter";

    final String DEFAULT_USERMAIL = "dieter@test.de";

    final Map<String,String> DEFAULT_VALUES = Map.of(
            "wp.register.username", DEFAULT_USERNAME,
            "wp.register.usermail", DEFAULT_USERMAIL);

    private String getValueFromOptionsOrDefault(String key){
        return this.options.containsKey(key) ? (String) this.options.get(key) : DEFAULT_VALUES.get(key);
    }

    private void navigateToRegisterPage(String baseUrl) throws InterruptedException {
        driver.get(baseUrl);
        var navElement = driver.findElement(By.id("nav"));
        navElement.findElement(By.xpath(".//*[text()='Register']")).click();
        addDelay();
    }

    private void insertRegistrationData(String username, String userEmail) throws InterruptedException {
        var loginField = driver.findElement(By.id("user_login"));
        loginField.sendKeys(username);
        addDelay();
        var passwordField = driver.findElement(By.id("user_email"));
        passwordField.sendKeys(userEmail);
        Select dropDownMenu = new Select(driver.findElement(By.tagName("select")));
        var options = dropDownMenu.getOptions();
        var randomQuestionIndex = ThreadLocalRandom.current().nextInt(0, options.size());
        var randomQuestionElement = options.get(randomQuestionIndex);
        randomQuestionElement.click();
        addValueToContext("wp.register.security.question", randomQuestionElement.getText());
        addDelay();
        var randomQuestionAnswerField = driver.findElement(By.id("seq_ans[]"));
        randomQuestionAnswerField.sendKeys("yes");
        addValueToContext("wp.register.security.question.answer", "yes");
        addDelay();

    }

    private void finishRegistrationAndExpectSuccessMessage() throws InterruptedException {
        var registerButton = driver.findElement(By.id("wp-submit"));
        registerButton.click();
        addDelay();
        driver.findElement(By.id("login")).findElement(By.xpath(".//*[text()='Registration successful.']"));
    }

    @Override
    public void run() {
        this.driver = getWebDriver();
        try {
            var baseUrl = String.format("http://%s/wp-admin", this.target);
            var username = getValueFromOptionsOrDefault("wp.register.username");
            var userEmail = getValueFromOptionsOrDefault("wp.register.usermail");
            navigateToRegisterPage(baseUrl);
            insertRegistrationData(username, userEmail);
            finishRegistrationAndExpectSuccessMessage();
        }catch (NoSuchElementException | InterruptedException ex){
            throw new OrchestratorException("Error while executing test, cause: " + ex.getMessage());
        }finally {
            driver.close();
        }
    }

    @Override
    public SeleniumTest newInstance(String target, Map<String, Object> options) {
        var out = new WordpressRegisterWithSecurityQuestion();
        out.target = target;
        out.options = options;
        return out;
    }
}
