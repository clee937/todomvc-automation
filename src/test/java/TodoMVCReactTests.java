import org.openqa.selenium.WebDriver;
import pages.TodoMVCReactPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.chrome.ChromeDriver;

public class TodoMVCReactTests {
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
        void shouldAddTodoItem() {
            todoPage.addTodo("Buy eggs");
            assertTrue(todoPage.wasItemAdded("Buy eggs"));
        }

        @Test
        void shouldNotAddEmptyTodo() {
            int currentTodoCount = todoPage.getTodoCount();
            todoPage.pressEnterOnEmptyInput();
            assertEquals(currentTodoCount, todoPage.getTodoCount());
        }
    }

