package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.TodoMVCReactPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void shouldManageMultipleTodoItems() {
        String firstTodo = "Wash car";
        String secondTodo = "Call Grandma";
        String updatedTodo = "Call Grandpa";

        todoPage.addTodo(firstTodo);
        todoPage.addTodo(secondTodo);

        todoPage.completeTodo(firstTodo);
        assertTrue(todoPage.isTodoCompleted(firstTodo));

        todoPage.editTodo(secondTodo, updatedTodo);
        assertTrue(todoPage.getTodoTexts().contains(updatedTodo));

        todoPage.deleteTodo(updatedTodo);
        List<String> todoTexts = todoPage.getTodoTexts();

        assertFalse(todoTexts.contains(updatedTodo), "Expected Todo list not to contain '" + updatedTodo + "' after deletion");
        assertEquals(1, todoTexts.size(), "Expected Todo list to contain only the first todo after deletion");
        assertTrue(todoTexts.contains(firstTodo),
                "Expected Todo list to contain remaining todo: '" + firstTodo + "'");
    }

    @Test
    void shouldToggleCompletionBeforeDeletingTodo() {
        String todo = "Book dentist appointment";

        todoPage.addTodo(todo);
        todoPage.completeTodo(todo);
        todoPage.uncompleteTodo(todo);
        todoPage.completeTodo(todo);
        todoPage.deleteTodo(todo);

        assertFalse(todoPage.getTodoTexts().contains(todo),
                "Expected todo '" + todo + "' to be removed after deletion");
    }
}
