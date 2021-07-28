package de.hsrm.orchestrationsystem.testcase_orchestration.teststep.conditional.conditions;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("check security question exists")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class CheckIfSecurityQuestionExists implements TestCondition {

    String target;


    private WebDriver initWebDriver(){
        try {
            FirefoxBinary binary = new FirefoxBinary();
            FirefoxOptions options = new FirefoxOptions();
            options.setBinary(binary);
            options.setHeadless(true);
            return System.getProperty("webdriver.gecko.driver") == null ? new ChromeDriver() : new FirefoxDriver(options);
        }catch (WebDriverException ex){
            throw new OrchestratorException("Error creating webdriver, cause: " + ex.getMessage());
        }
    }

    @Override
    public boolean check() {
        var driver = initWebDriver();
        try {
            driver.get(String.format("%s/wp-admin", this.target));
            driver.findElement(By.id("loginform")).findElement(By.xpath(".//*[text()='Security Question']"));
        }catch (NoSuchElementException ex){
            return false;
        }finally {
            driver.close();
        }
        return true;
    }

    @Override
    public TestCondition parameterizedInstance(String target, Map<String, Object> options) {
        var out = new CheckIfSecurityQuestionExists();
        out.target = target;
        return out;
    }
}
