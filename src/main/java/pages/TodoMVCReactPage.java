package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TodoMVCReactPage {
    private final WebDriver driver;
    private final By inputBoxLocator = By.cssSelector("[data-testid='text-input']");
    private final By todoItemLabelsLocator = By.cssSelector("[data-testid='todo-item-label']");
    private final By editInputLocator = By.cssSelector("input.edit");
    private static final String URL =
            "https://todomvc.com/examples/react/dist/";

    public TodoMVCReactPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        driver.get(URL);
    }

    public void addTodo(String todo) {
        WebElement inputBox = driver.findElement(inputBoxLocator);
        inputBox.sendKeys(todo, Keys.ENTER);

//      String currentText = inputBox.getAttribute("value");
//      System.out.println(currentText);
    }

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
        return driver.findElements(todoItemLabelsLocator).size();
    }

    public void pressEnterOnEmptyInput() {
        driver.findElement(inputBoxLocator).sendKeys(Keys.ENTER);
    }

    public void editTodo(String existingTodo, String newTodo) {

        WebElement todoText = driver.findElement(
                By.xpath("//label[text()='" + existingTodo + "']")
        );

        Actions actions = new Actions(driver);
        actions.doubleClick(todoText).perform();

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
        WebElement todoText = driver.findElement(
                By.xpath("//label[text()='" + existingTodo + "']")
        );

        Actions actions = new Actions(driver);
        actions.doubleClick(todoText).perform();

        WebElement editInput = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(editInputLocator));

        editInput.sendKeys(Keys.ESCAPE);
    }

    public void completeTodo(String todo) {
        WebElement todoItem = driver.findElement(
                By.xpath("//label[text()='" + todo + "']/ancestor::li")
        );

        todoItem.findElement(By.cssSelector(".toggle")).click();
    }

    public void uncompleteTodo(String todo) {
        WebElement todoItem = driver.findElement(
                By.xpath("//label[text()='" + todo + "']/ancestor::li")
        );

        todoItem.findElement(By.cssSelector(".toggle")).click();
    }

    public boolean isTodoCompleted(String todo) {
        WebElement todoItem = driver.findElement(
                By.xpath("//label[text()='" + todo + "']/ancestor::li")
        );

        String cssClasses = todoItem.getAttribute("class");

        return cssClasses != null && cssClasses.contains("completed");
    }

    public void deleteTodo(String todo) {
        WebElement todoItem = driver.findElement(
                By.xpath("//label[text()='" + todo + "']/ancestor::li")
        );

        Actions actions = new Actions(driver);
        // hover over the element
        actions.moveToElement(todoItem).perform();

        todoItem.findElement(By.cssSelector(".destroy")).click();
    }
}
