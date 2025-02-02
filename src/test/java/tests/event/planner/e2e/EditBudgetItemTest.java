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
import tests.event.planner.pages.budget.EditBudgetItemPage;

import java.time.Duration;

public class EditBudgetItemTest {
    private static WebDriver driver;
    private EditBudgetItemPage editBudgetItemPage;

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
        editBudgetItemPage = new EditBudgetItemPage(driver);

        WebElement editBudgetItemButton = editBudgetItemPage.getWait().until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//tr[td[1][text()='Entertainment']]//p-button[@icon='pi pi-pencil']")
                )
        );
        editBudgetItemButton.click();

        editBudgetItemPage.getWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("p-dialog[header='Edit Budget Item']"))
        );
        Helper.takeScreenshoot(driver, "edit_budget_item_dialog_opened");
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
        Assertions.assertTrue(editBudgetItemPage.isPageLoaded(), "Edit Budget Item page is not loaded.");
        WebElement maxAmountInput = editBudgetItemPage.getWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector("p-dialog[header='Edit Budget Item'] input[formControlName='maxAmount']")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView()", maxAmountInput);
        Actions actions = new Actions(driver);
        actions.moveToElement(maxAmountInput).click().perform();

        maxAmountInput.click();
        maxAmountInput.sendKeys("100");
        Assertions.assertEquals("100", maxAmountInput.getAttribute("value"), "MaxAmount input should have correct value");

        Helper.takeScreenshoot(driver, "edit_budget_item_max_amount");
    }

    @Test
    public void testEditBudgetItem() {
        editBudgetItemPage.setMaxAmountInput(100);
        editBudgetItemPage.clickEditBudgetItemButton();

        editBudgetItemPage.getWait().until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("p-dialog[header='Edit Budget Item']"))
        );
        WebElement budgetItemRow = editBudgetItemPage.getWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[td[1][text()='Entertainment']]")
        ));
        WebElement maxPriceElement = budgetItemRow.findElement(By.xpath("./td[2]"));
        String maxPriceText = maxPriceElement.getText().replace("€", "").trim();

        Assertions.assertEquals("100", maxPriceText, "Expected maxPrice to be 100, but got: " + maxPriceText);
        Helper.takeScreenshoot(driver, "edit_budget_item_success");
    }
}
