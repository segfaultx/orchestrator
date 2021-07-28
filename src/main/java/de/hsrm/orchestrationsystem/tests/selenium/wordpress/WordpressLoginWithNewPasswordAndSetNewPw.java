package de.hsrm.orchestrationsystem.tests.selenium.wordpress;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.tests.selenium.AbstractSeleniumTestCase;
import de.hsrm.orchestrationsystem.tests.selenium.SeleniumTest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("wp-user-login-with-new-pw")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordpressLoginWithNewPasswordAndSetNewPw extends AbstractSeleniumTestCase {

    String target;

    Map<String, Object> options;

    final String DEFAULT_USERNAME = "dieter";

    private String getUsernameFromOptionsOrDefault() {
        return this.options.containsKey("wp.login.username") ?
                (String) this.options.get("wp.login.username") : DEFAULT_USERNAME;
    }

    @Override
    public void run() {
        this.driver = getWebDriver();
        try {
            loginAsUser(driver);
            addDelay();
            setNewUserPassword(driver);
            addDelay();
        } catch (NoSuchElementException | InterruptedException ex) {
            throw new OrchestratorException("Error while executing test, cause: " + ex.getMessage());
        } finally {
            driver.close();
        }
    }


    private void loginAsUser(WebDriver driver) throws InterruptedException {
        var baseUrl = String.format("http://%s/wp-admin", this.target);
        driver.get(baseUrl);
        var loginField = driver.findElement(By.id("user_login"));
        var username = getUsernameFromOptionsOrDefault();
        loginField.sendKeys(username);
        addDelay();
        var passwordField = driver.findElement(By.id("user_pass"));
        passwordField.sendKeys((String) getValueFromContext("wp.newuser.password"));
        addDelay();
        var loginButton = driver.findElement(By.id("wp-submit"));
        loginButton.click();
    }


    private void setNewUserPassword(WebDriver driver) throws InterruptedException {
        driver.findElement(By.className("wp-generate-pw")).click();
        addDelay();
        var passwordField = driver.findElement(By.id("pass1"));
        passwordField.clear();
        addDelay();
        passwordField.sendKeys("m");
        addDelay();
        passwordField = driver.findElement(By.id("pass1"));
        passwordField.sendKeys("ynewpassword!");
        addDelay();
        driver.findElement(By.id("submit")).click();
        addDelay();
        var messageToast = driver.findElement(By.id("message"));
        messageToast.findElement(By.xpath(".//*[text()='Profile updated.']"));
        addDelay();
    }


    @Override
    public SeleniumTest newInstance(String target, Map<String, Object> options) {
        var out = new WordpressLoginWithNewPasswordAndSetNewPw();
        out.target = target;
        out.options = options;
        return out;
    }

}
