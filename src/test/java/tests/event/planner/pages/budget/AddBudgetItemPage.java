package tests.event.planner.pages.budget;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AddBudgetItemPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "p-dialog[header='Add Budget Item'] input[formControlName='maxAmount']")
    private WebElement maxAmountInput;

    @FindBy(css = "p-dialog[header='Add Budget Item'] p-dropdown")
    private WebElement categoryDropdown;

    @FindBy(css = "p-dialog[header='Add Budget Item'] p-button")
    private WebElement createBudgetItemButton;

    public AddBudgetItemPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void setMaxAmount(double maxAmount) {
        waitAndSendKeys(maxAmountInput, String.valueOf(maxAmount));
    }

    public void selectCategory(String category) {
        wait.until(ExpectedConditions.elementToBeClickable(categoryDropdown)).click();
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//p-dropdown//li[contains(.,'" + category + "')]")));
        option.click();
    }

    public void clickCreateBudgetItem() {
        wait.until(ExpectedConditions.elementToBeClickable(createBudgetItemButton)).click();
    }

    public boolean isPageLoaded() {
        try {
            return  wait.until(ExpectedConditions.visibilityOf(maxAmountInput)).isDisplayed() &&
                    wait.until(ExpectedConditions.visibilityOf(categoryDropdown)).isDisplayed() &&
                    wait.until(ExpectedConditions.visibilityOf(createBudgetItemButton)).isDisplayed();
        }catch (TimeoutException e) {
            return false;
        }
    }

    private void waitAndSendKeys(WebElement element, String text) {
        WebElement waitElement = wait.until(ExpectedConditions.elementToBeClickable(element));
        waitElement.clear();
        waitElement.sendKeys(text);
    }
}
