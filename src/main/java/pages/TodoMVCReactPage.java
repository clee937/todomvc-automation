package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class TodoMVCReactPage {
    // ---------- Locators ----------

    private final WebDriver driver;
    private final By inputBoxLocator = By.cssSelector("[data-testid='text-input']");
    private final By todoItemLabelsLocator = By.cssSelector("[data-testid='todo-item-label']");
    private final By editInputLocator = By.cssSelector("input.edit");
    private final By todoCompletionToggleLocator = By.cssSelector("[data-testid='todo-item-toggle']");
    private final By toggleAllCompletionLocator = By.id("toggle-all");
    private final By completedFilterLocator = By.cssSelector("a[href='#/completed']");
    private final By activeFilterLocator = By.cssSelector("a[href='#/active']");
    private final By allFilterLocator = By.cssSelector("a[href='#/']");
    private final By clearCompletedLocator = By.cssSelector(".clear-completed");
    private final By todoCountLocator = By.cssSelector(".todo-count");
    private static final String URL =
            "https://todomvc.com/examples/react/dist/";

    // ---------- Constructor ----------

    public TodoMVCReactPage(WebDriver driver) {
        this.driver = driver;
    }

    // ---------- Actions ----------

    public void open() {
        driver.get(URL);
    }

    public void addTodo(String todo) {
        WebElement inputBox = driver.findElement(inputBoxLocator);
        inputBox.sendKeys(todo, Keys.ENTER);

//      String currentText = inputBox.getAttribute("value");
//      System.out.println(currentText);
    }

    public void pressEnterOnEmptyInput() {
        driver.findElement(inputBoxLocator).sendKeys(Keys.ENTER);
    }

    public void editTodo(String existingTodo, String newTodo) {
        Actions actions = new Actions(driver);
        actions.doubleClick(getTodoLabel(existingTodo)).perform();

        WebElement editInput = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(editInputLocator));

        editInput.sendKeys(
                Keys.chord(Keys.COMMAND, "a"),
                Keys.BACK_SPACE,
                newTodo,
                Keys.ENTER
        );
    }

    public void startEditingTodoAndPressEscape(String existingTodo) {
        Actions actions = new Actions(driver);
        actions.doubleClick(getTodoLabel(existingTodo)).perform();

        WebElement editInput = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(editInputLocator));

        editInput.sendKeys(Keys.ESCAPE);
    }

    public void completeTodo(String todo) {
        clickTodoCompletionToggle(todo);
    }

    public void uncompleteTodo(String todo) {
        clickTodoCompletionToggle(todo);
    }

    public void toggleAllTodoCompletion() {
        WebElement todoCompletionToggle = driver.findElement(toggleAllCompletionLocator);
        todoCompletionToggle.click();
    }

    public void deleteTodo(String todo) {
        WebElement todoItem = getTodoItem(todo);

        Actions actions = new Actions(driver);
        // hover over the element
        actions.moveToElement(todoItem).perform();

        todoItem.findElement(By.cssSelector(".destroy")).click();

    }

    public void clearCompletedTodos() {
        driver.findElement(clearCompletedLocator).click();
    }

    public void filterByCompleted() {
        driver.findElement(completedFilterLocator).click();
    }

    public void filterByActive() {
        driver.findElement(activeFilterLocator).click();
    }

    public void filterByAll() {
        driver.findElement(allFilterLocator).click();
    }

    // ---------- Assertions / Queries ----------

    public List<String> getTodoTexts() {

        List<WebElement> labels = driver.findElements(todoItemLabelsLocator);

        return labels.stream()
                .map(WebElement::getText)
                .toList();

//        List<String> todoTexts = new ArrayList<>();
//
//        for (WebElement label : labels) {
//            todoTexts.add(label.getText());
//        }
//        return todoTexts;
    }

    public int getTodoCount() {
        return getTodoTexts().size();
    }

    public int getRemainingTodoCount() {
        WebElement todoCount = driver.findElement(todoCountLocator);

        String todoCountText = todoCount.getText();

        String[] parts = todoCountText.split(" ");

        return Integer.parseInt(parts[0]);
    }

    public boolean isTodoCompleted(String todo) {
        WebElement todoItem = getTodoItem(todo);

        String cssClasses = todoItem.getAttribute("class");

        return cssClasses != null && cssClasses.contains("completed");
    }

    public boolean isClearCompletedButtonVisible() {
        List<WebElement> buttons = driver.findElements(clearCompletedLocator);
        return !buttons.isEmpty() && buttons.get(0).isDisplayed();
    }

    public boolean isTodoCountVisible() {
        List<WebElement> todoCounts = driver.findElements(todoCountLocator);
        return !todoCounts.isEmpty() && todoCounts.get(0).isDisplayed();
    }

    // ---------- Helpers ----------

    private WebElement getTodoItem(String todo) {
        return driver.findElement(
                By.xpath("//label[text()='" + todo + "']/ancestor::li")
        );
    }

    private WebElement getTodoLabel(String todo) {
        return getTodoItem(todo)
                .findElement(todoItemLabelsLocator);
    }

    private void clickTodoCompletionToggle(String todo) {
        getTodoItem(todo)
                .findElement(todoCompletionToggleLocator)
                .click();
    }
}
