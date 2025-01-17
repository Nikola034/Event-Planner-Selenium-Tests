package tests.event.planner.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;


public class SearchPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Updated selectors for the sort dropdowns based on their position and text context
    @FindBy(xpath = "//span[contains(text(), 'Sort events by')]/following-sibling::p-dropdown")
    private WebElement eventSortDropdown;

    // Updated selector for Filters button
    @FindBy(css = "p-button[label='Filters'] button.p-button")
    private WebElement filtersButton;

    // Event cards with more specific selector
    @FindBy(css = "app-event-card p-panel")
    private List<WebElement> eventCards;

    @FindBy(xpath = "//app-events//button[@aria-label='Next Page']")
    private WebElement nextPageButton;

    @FindBy(xpath = "//app-events//button[@aria-label='Previous Page']")
    private WebElement prevPageButton;

    @FindBy(css = ".p-inputtext")
    private WebElement searchInput;

    @FindBy(css = "app-event-card .event-date")
    private List<WebElement> eventDates;


    public SearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }



    public List<String> getSortedBy(String sortBy) {
        List<WebElement> eventTitles=driver.findElements(By.xpath("//p[contains(@class, 'event-"+sortBy+"')]"));
        return eventTitles.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public boolean areEventsSortedBy(String sortBy) {
        List<String> result = getSortedBy(sortBy);
        return isSortedAscending(result);
    }

    private <T> boolean isSortedAscending(List<T> list) {
        if (list == null || list.size() <= 1) {
            return true;
        }

        for (int i = 0; i < list.size() - 1; i++) {
            T current = list.get(i);
            T next = list.get(i + 1);

            int comparison;
            if (current instanceof String && next instanceof String) {
                // Case-insensitive comparison for strings, with trimming
                String currentStr = ((String) current).trim().toLowerCase();
                String nextStr = ((String) next).trim().toLowerCase();
                comparison = currentStr.compareTo(nextStr);

            } else {
                comparison = ((Comparable) current).compareTo(next);
            }

            if (comparison > 0) {
                return false;
            }
        }
        return true;
    }


    public void selectEventSortOption(String option) {
        // Click dropdown to open
        wait.until(ExpectedConditions.elementToBeClickable(eventSortDropdown)).click();

        // Wait for dropdown panel to be visible
        WebElement dropdownPanel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("p-dropdown-panel")
        ));

        // Find the option by looking for the span containing the text
        WebElement optionElement = wait.until(ExpectedConditions.elementToBeClickable(
                dropdownPanel.findElement(By.xpath(".//li[contains(@class, 'p-dropdown-item')]//span[text()='" + option + "']//ancestor::li[1]"))
        ));

        // Click the option
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", optionElement);

        // Wait for dropdown to close
        wait.until(ExpectedConditions.invisibilityOf(dropdownPanel));
    }

    public void enterSearchTerm(String searchTerm) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        input.clear();
        input.sendKeys(searchTerm);
        input.sendKeys(Keys.ENTER);

    }


    private void waitForLoadingComplete() {
        try {
            // First, give a very short wait for spinner to appear
            new WebDriverWait(driver, Duration.ofMillis(500))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("p-progressSpinner")));
        } catch (TimeoutException e) {
            // If spinner doesn't appear within 500ms, content might be already loaded
            // or loading might be too fast to catch the spinner
        }

        // Then wait for either:
        // 1. Spinner to disappear (if it was present)
        // 2. Content to be visible and stable
        wait.until(ExpectedConditions.or(
                ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("p-progressSpinner")),
                ExpectedConditions.and(
                        ExpectedConditions.visibilityOfElementLocated(By.id("displayed-events")),
                        driver -> !isStale(driver.findElement(By.id("displayed-events")))
                )
        ));
    }

    // Helper method to check if element is stale
    private boolean isStale(WebElement element) {
        try {
            // Try to check if element is still attached to DOM
            element.isDisplayed();
            return false;
        } catch (StaleElementReferenceException e) {
            return true;
        }
    }



    public FilterPanel openFilters() {
        wait.until(ExpectedConditions.elementToBeClickable(filtersButton)).click();
        // Wait for PrimeNG sidebar to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".p-sidebar")));
        return new FilterPanel(driver);
    }

    public void nextPage() {
        wait.until(ExpectedConditions.elementToBeClickable(nextPageButton)).click();
    }

    public void prevPage() {
        wait.until(ExpectedConditions.elementToBeClickable(prevPageButton)).click();
    }

    public int getEventCount() {
        // Make sure loading is complete before counting events
        waitForLoadingComplete();

        // Get all visible event cards
        return driver.findElements(By.cssSelector("app-event-card p-panel"))
                .stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList())
                .size();
    }


}