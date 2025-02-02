package tests.event.planner.pages.budget;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EditBudgetItemPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "p-dialog[header='Edit Budget Item'] input[formControlName='maxAmount']")
    private WebElement maxAmountInput;

    @FindBy(css = "p-dialog[header='Edit Budget Item'] p-button")
    private WebElement editBudgetItemButton;

    public EditBudgetItemPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public WebDriverWait getWait() {
        return this.wait;
    }

    public void setMaxAmountInput(double maxAmount) {
        waitAndSendKeys(maxAmountInput, String.valueOf(maxAmount));
    }

    public void clickEditBudgetItemButton() {
        wait.until(ExpectedConditions.elementToBeClickable(editBudgetItemButton))
                .click();
    }

    public boolean isPageLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(maxAmountInput)).isDisplayed();
        }catch (TimeoutException e) {
            return false;
        }
    }

    private void waitAndSendKeys(WebElement maxAmountInput, String text) {
        WebElement waitElement = wait.until(ExpectedConditions.elementToBeClickable(maxAmountInput));
        waitElement.clear();
        waitElement.sendKeys(text);
    }
}
