package tests.event.planner.e2e;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import tests.event.planner.pages.budget.ChooseMerchandisePage;
import tests.event.planner.pages.budget.ProductDetailsPage;

import java.time.Duration;
import java.util.List;

public class PurchaseMerchandiseTest {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private ChooseMerchandisePage chooseMerchandisePage;
    private ProductDetailsPage productDetailsPage;

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
        chooseMerchandisePage = new ChooseMerchandisePage(driver);
        productDetailsPage = new ProductDetailsPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement openMerchandiseDialog = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("td p-button[label='Choose Merchandise']:first-of-type")
        ));
        openMerchandiseDialog.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p-dialog:has(app-merchandise)")));
        Helper.takeScreenshoot(driver, "budget_choose_merchandise");
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
    public void testPurchaseProduct() {
        Assertions.assertTrue(chooseMerchandisePage.isPageLoaded(), "Choose Merchandise page did not load");
        String productName = chooseMerchandisePage.getMerchandiseTitle();
        chooseMerchandisePage.clickSeeMerchandiseButton();

        wait.until(ExpectedConditions.urlContains("/home/product"));
        Assertions.assertTrue(productDetailsPage.isPageLoaded(), "Product details page did not load");
        productDetailsPage.clickPurchaseButton();

        wait.until(ExpectedConditions.urlContains("/home/budget"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p-table")));
        List<WebElement> tableCells = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("p-table tr td")
        ));

        boolean productFound = tableCells.stream().anyMatch(cell -> {
           try {
               return cell.getText().contains(productName);
           } catch (Exception e) {
               return false;
           }
        });
        Assertions.assertTrue(productFound, "Product '" + productName + "' was not found in the table");
    }
}
