package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class TodoMVCReactPage {
    private final WebDriver driver;
    private final By inputBoxLocator = By.cssSelector("[data-testid='text-input']");
    private final By todoItemLabelsLocator = By.cssSelector("[data-testid='todo-item-label']");
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

        List<String> todoTexts = new ArrayList<>();

        for (WebElement label : labels) {
            todoTexts.add(label.getText());
        }

        return todoTexts;
    }

    public int getTodoCount() {
        return driver.findElements(todoItemLabelsLocator).size();
    }

    public void pressEnterOnEmptyInput() {
        driver.findElement(inputBoxLocator).sendKeys(Keys.ENTER);
    }
}
