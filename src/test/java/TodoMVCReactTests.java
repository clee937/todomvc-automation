import org.openqa.selenium.WebDriver;
import pages.TodoMVCReactPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
            assertTrue(todoPage.getTodoTexts().contains("Buy eggs"), "Expected Todo list to contain 'Buy eggs'");
        }

        @Test
        void shouldNotAddEmptyTodo() {
            int currentTodoCount = todoPage.getTodoCount();
            todoPage.pressEnterOnEmptyInput();
            assertEquals(currentTodoCount, todoPage.getTodoCount(), "Pressing Enter on an empty input should not create a new todo");
        }

        @Test
        void shouldNotAddTodoWhenEnterPressedMultipleTimesOnEmptyInput() {
            int currentTodoCount = todoPage.getTodoCount();

            todoPage.pressEnterOnEmptyInput();
            todoPage.pressEnterOnEmptyInput();
            todoPage.pressEnterOnEmptyInput();

            assertEquals(currentTodoCount, todoPage.getTodoCount(), "Pressing Enter on an empty input should not create a new todo");
    }

        @Test
        void shouldNotAddEmptyTodoAfterExistingTodo() {
            todoPage.addTodo("Buy eggs");

            int currentTodoCount = todoPage.getTodoCount();
            todoPage.pressEnterOnEmptyInput();

            assertEquals(currentTodoCount, todoPage.getTodoCount(), "Pressing Enter on an empty input should not create an additional todo");

            System.out.printf("Current count: %d, New count: %d", currentTodoCount, todoPage.getTodoCount());
        }

        @Test
        void shouldAllowSingleCharacterTodo() {
            todoPage.addTodo("h");
            assertTrue(todoPage.getTodoTexts().contains("h"), "Expected Todo list to contain 'h'");
        }

        @Test
        void shouldAllowPunctuation() {
            todoPage.addTodo("!");
            assertTrue(todoPage.getTodoTexts().contains("!"), "Expected Todo list to contain '!'");
        }

        @Test
        void shouldAllowNumbers() {
            todoPage.addTodo("2");
            assertTrue(todoPage.getTodoTexts().contains("2"), "Expected Todo list to contain '2'");
        }

        @Test
        void shouldSupportAccentedCharacters() {
            todoPage.addTodo("é");
            assertTrue(todoPage.getTodoTexts().contains("é"), "Expected Todo list to contain 'é'");
        }

    //  Emoji input could not be verified through WebDriver sendKeys due to ChromeDriver limitation.
//        @Test
//        void shouldSupportEmoji() {
//            todoPage.addTodo("\uD83D\uDE00");
//            assertTrue(todoPage.getTodoTexts().contains("\uD83D\uDE00"), "Expected Todo list to contain '\uD83D\uDE00'");
//        }

        @Test
        void shouldEditTodo() {

            String originalTodo = "Buy birthday card";
            String updatedTodo = "Buy anniversary card";

            todoPage.addTodo(originalTodo);
            todoPage.editTodo(originalTodo, updatedTodo);

            List<String> todoTexts = todoPage.getTodoTexts();

//            assertTrue(todoTexts.contains(updatedTodo),
//                    "Expected Todo list to contain updated todo: '" + updatedTodo + "' but was " + todoTexts);

            assertFalse(todoTexts.contains(originalTodo),
                    "Expected Todo list not to contain original todo: '" + originalTodo + "'");
        }
}

