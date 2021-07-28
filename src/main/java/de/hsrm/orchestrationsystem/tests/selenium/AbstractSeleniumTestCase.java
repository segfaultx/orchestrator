package de.hsrm.orchestrationsystem.tests.selenium;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractSeleniumTestCase implements SeleniumTest {

    Map<String, Object> context;

    WebDriver driver;


    @Override
    public void addContext(Map<String, Object> context) {
        this.context = context;
    }

    protected WebDriver getWebDriver(){
        try {
            return System.getProperty("webdriver.gecko.driver") == null ? new ChromeDriver() : new FirefoxDriver();
        }catch (WebDriverException ex){
            throw new OrchestratorException("Error creating webdriver, cause: " + ex.getMessage());
        }
    }

    protected void addDelay() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    @Override
    public void interrupt() {
        this.driver.close();
    }

    protected void addValueToContext(String key, Object value){
        this.context.put(key, value);
    }

    protected Object getValueFromContext(String key){
        if (!this.context.containsKey(key))
            throw new OrchestratorException("Unknown context key: " + key);
        return this.context.get(key);
    }

}
