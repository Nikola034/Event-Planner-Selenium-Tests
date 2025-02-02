package tests.event.planner.pages.budget;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ChooseMerchandisePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "app-merchandise-card p-panel p-button[label='See details']")
    private WebElement seeMerchandiseDetailsButton;

    @FindBy(css = "p.text-xl.font-semibold")
    private WebElement merchandiseTitleElement;

    public ChooseMerchandisePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void clickSeeMerchandiseButton() {
        wait.until(ExpectedConditions.elementToBeClickable(seeMerchandiseDetailsButton)).click();
    }

    public String getMerchandiseTitle() {
        return wait.until(ExpectedConditions.visibilityOf(merchandiseTitleElement)).getText();
    }

    public boolean isPageLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(seeMerchandiseDetailsButton)).isDisplayed();
        }catch (TimeoutException e) {
            return false;
        }
    }
}
