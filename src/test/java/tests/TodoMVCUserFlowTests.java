package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.TodoMVCReactPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TodoMVCUserFlowTests {
    private WebDriver driver;
    private TodoMVCReactPage todoPage;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        todoPage = new TodoMVCReactPage(driver);
        todoPage.open();
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void shouldCompleteTodoLifeCycle() {

        String originalTodo = "Buy birthday gift";
        String updatedTodo = "Buy anniversary gift";

        todoPage.addTodo(originalTodo);

        todoPage.editTodo(originalTodo, updatedTodo);
        assertTrue(todoPage.getTodoTexts().contains(updatedTodo));

        todoPage.completeTodo(updatedTodo);
        assertTrue(todoPage.isTodoCompleted(updatedTodo));

        todoPage.deleteTodo(updatedTodo);

        List<String> todoTexts = todoPage.getTodoTexts();

        assertTrue(todoTexts.isEmpty(), "Expected Todo list to contain no todos after deletion");
    }
}
