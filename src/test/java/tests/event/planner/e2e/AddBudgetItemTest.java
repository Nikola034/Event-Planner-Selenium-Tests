package tests.event.planner.e2e;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import tests.event.planner.pages.budget.AddBudgetItemPage;

import java.time.Duration;
import java.util.List;

public class AddBudgetItemTest {
    private static WebDriver driver;
    private AddBudgetItemPage addBudgetItemPage;

    @BeforeAll
    public static void setUpClass() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
    }

    @BeforeEach
    public void setUp() {
        login("johnadoe@example.com", "securepassword123");

        driver.get("http://localhost:4200/home/budget/4");
        addBudgetItemPage = new AddBudgetItemPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement addBudgetItemButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("p-button[icon='pi pi-plus']")));
        addBudgetItemButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p-dialog[header='Add Budget Item']")));

        Helper.takeScreenshoot(driver, "add_budget_item_dialog_opened");
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
    public void testMaxAmount() {
        Assertions.assertTrue(addBudgetItemPage.isPageLoaded(), "Add Budget Item page did not load");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement maxAmountInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[formControlName='maxAmount']")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView()", maxAmountInput);

        Actions actions = new Actions(driver);
        actions.moveToElement(maxAmountInput).click().perform();

        maxAmountInput.click();
        maxAmountInput.sendKeys("5000");
        Assertions.assertEquals("5000", maxAmountInput.getAttribute("value"), "MaxAmount input should have correct value");

        Helper.takeScreenshoot(driver, "add_budget_item_max_amount");
    }

    @Test
    public void testCreateBudgetItem() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rowsBeforeAdd = driver.findElements(By.cssSelector("tr"));
        int rowCountBefore = rowsBeforeAdd.size();

        addBudgetItemPage.setMaxAmount(5000);
        addBudgetItemPage.selectCategory("Protest");
        addBudgetItemPage.clickCreateBudgetItem();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("p-dialog[header='Add Budget Item']")));
        List<WebElement> rowsAfterAdd = driver.findElements(By.cssSelector("tr"));
        int rowCountAfter = rowsAfterAdd.size();

        Assertions.assertEquals(rowCountBefore + 1, rowCountAfter, "Expected row count to increase by 1");
        Helper.takeScreenshoot(driver, "add_budget_item_success");
    }
}
