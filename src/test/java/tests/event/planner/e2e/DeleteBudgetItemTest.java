package tests.event.planner.e2e;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DeleteBudgetItemTest {
    private static WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
    }

    @BeforeEach
    public void setup() {
        login("johndoe@gmail.com", "sifra");

        driver.get("http://localhost:4200/home/budget/4");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void login(String email, String password) {
        driver.get("http://localhost:4200"); // Replace with your login page URL

        // Wait for the email field to be clickable
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[formcontrolname='email']")));
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[formcontrolname='password']")));
        WebElement signInButton = driver.findElement(By.cssSelector("p-button[label='SIGN IN']"));

        // Fill in the login credentials
        emailField.sendKeys(email);
        passwordField.sendKeys(password);

        // Submit the login form
        signInButton.click();

        // Wait for the element visible after successful login (for example, the "See All" button)
        WebElement seeAllButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p-button[label='See All']")));
        Assertions.assertTrue(seeAllButton.isDisplayed(), "Login failed: 'See All' button not visible.");
    }

    @Test
    public void testDeleteBudgetItem() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rowsBeforeDelete = driver.findElements(By.cssSelector("tr"));
        int rowCountBefore = rowsBeforeDelete.size();

        WebElement deleteBudgetItemButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//tr[td[1][text()='Entertainment']]//p-button[@icon='pi pi-trash']")
        ));
        deleteBudgetItemButton.click();

        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(By.cssSelector("tr"), rowCountBefore));
        List<WebElement> rowsAfterDelete = driver.findElements(By.cssSelector("tr"));
        int rowCountAfter = rowsAfterDelete.size();

        Assertions.assertEquals(rowCountBefore - 1, rowCountAfter, "Expected row count to decrease by 1");
        Helper.takeScreenshoot(driver, "delete_budget_item_success");
    }
}
