package tests.event.planner.pages.budget;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProductDetailsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "p-button[severity='primary']")
    private WebElement purchaseButton;

    public ProductDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void clickPurchaseButton() {
        wait.until(ExpectedConditions.elementToBeClickable(purchaseButton)).click();
    }

    public boolean isPageLoaded() {
        try {
            return purchaseButton.isDisplayed();
        }catch (TimeoutException e) {
            return false;
        }
    }
}
