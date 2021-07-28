package de.hsrm.orchestrationsystem.tests.selenium.wordpress;

import de.hsrm.orchestrationsystem.testcase_orchestration.exceptions.OrchestratorException;
import de.hsrm.orchestrationsystem.tests.selenium.AbstractSeleniumTestCase;
import de.hsrm.orchestrationsystem.tests.selenium.SeleniumTest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("wp-approve-user")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordpressApproveUserAndAssignPassword extends AbstractSeleniumTestCase {

    @Override
    public void run() {
        this.driver = getWebDriver();
        try {
            loginAsAdmin(driver);
            addDelay();
            navigateToUsersRequiringApproval(driver);
            addDelay();
            approveUser(driver);
            addDelay();
            navigateToUserPageAndSetPassword(driver);
            addDelay();
        } catch (NoSuchElementException | InterruptedException ex) {
            throw new OrchestratorException("Error while executing test, cause: " + ex.getMessage());
        } finally {
            driver.close();
        }
    }


    private void loginAsAdmin(WebDriver driver) throws InterruptedException {
        var baseUrl = "http://localhost:8000/wp-admin";
        driver.get(baseUrl);
        var loginField = driver.findElement(By.id("user_login"));
        loginField.sendKeys("admin");
        addDelay();
        var passwordField = driver.findElement(By.id("user_pass"));
        passwordField.sendKeys("admin");
        addDelay();
        var loginButton = driver.findElement(By.id("wp-submit"));
        loginButton.click();
    }

    private void navigateToUsersRequiringApproval(WebDriver driver) throws InterruptedException {
        driver.findElement(By.id("menu-users")).click();
        addDelay();
        var usersMenu = driver.findElement(By.id("menu-users"));
        usersMenu.findElement(By.xpath(".//*[text()='Approve New Users']")).click();

    }

    private void approveUser(WebDriver driver) {
        var table = driver.findElement(By.id("pw_pending_users"));
        var tableRows = table.findElements(By.tagName("tr"));
        for (var element : tableRows) {
            try {
                element.findElement(By.xpath(".//*[text()='dieter']"));
                element.findElement(By.xpath(".//*[text()='Approve']")).click();
                addDelay();
                break;
            }catch (NoSuchElementException | InterruptedException ignored){
                // ignored on purpose
            }
        }
        driver.findElement(By.id("pw_pending_users")).findElement(By.xpath(".//*[text()='There are no users with a status of pending']"));
    }

    private void navigateToUserPageAndSetPassword(WebDriver driver) throws InterruptedException {
        driver.findElement(By.id("menu-users")).findElement(By.className("wp-first-item")).click();
        addDelay();
        driver.findElement(By.id("the-list")).findElement(By.xpath(".//*[text()='dieter']")).click();
        addDelay();
        driver.findElement(By.className("wp-generate-pw")).click();
        addDelay();
        addValueToContext("wp.newuser.password", driver.findElement(By.id("pass1")).getAttribute("data-pw"));
        addDelay();
        driver.findElement(By.id("submit")).click();
        addDelay();
        driver.findElement(By.id("message")).findElement(By.xpath(".//*[text()='User updated.']"));
        addDelay();
    }

    @Override
    public SeleniumTest newInstance(String target, Map<String, Object> options) {
        return new WordpressApproveUserAndAssignPassword();
    }

}
