import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
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

//      Practising individual parameterized tests with @ValueSource. Good for mapping to requirements/spec.
        @DisplayName("Should accept single character todo text")
        @ParameterizedTest(name = "Should accept: \"{0}\"")
        @ValueSource(strings = {"a", "5", "£"})
        void shouldAcceptSingleCharacterTodoText(String todo) {
            todoPage.addTodo(todo);
            assertTrue(todoPage.getTodoTexts().contains(todo));
        }

        @DisplayName("Should accept punctuation")
        @ParameterizedTest(name = "Should accept: \"{0}\"")
        @ValueSource(strings = {"!", ".", "?"})
        void shouldAcceptPunctuation(String todo) {
            todoPage.addTodo(todo);
            assertTrue(todoPage.getTodoTexts().contains(todo), "Expected Todo list to contain: " + todo + ", but it contained: " + todoPage.getTodoTexts());
        }

        @DisplayName("Should accept numbers")
        @ParameterizedTest(name = "Should accept: \"{0}\"")
        @ValueSource(strings = {"0", "1", "10"})
        void shouldAcceptNumbers(String todo) {
            todoPage.addTodo(todo);
            assertTrue(todoPage.getTodoTexts().contains(todo), "Expected Todo list to contain: " + todo + ", but it contained: " + todoPage.getTodoTexts());
        }

        @DisplayName("Should accept accented characters")
        @ParameterizedTest(name = "Should accept: \"{0}\"")
        @ValueSource(strings = {"é", "ñ", "ô"})
        void shouldAcceptAccentedCharacters(String todo) {
            todoPage.addTodo(todo);
            assertTrue(todoPage.getTodoTexts().contains(todo), "Expected Todo list to contain: " + todo + ", but it contained: " + todoPage.getTodoTexts());
        }

        @DisplayName("Should accept non-Latin characters")
        @ParameterizedTest(name = "Should accept: \"{0}\"")
        @ValueSource(strings = {"東京", "مرحبا", "Γειά", "안녕"})
        void shouldAcceptNonLatinCharacters(String todo) {
            todoPage.addTodo(todo);
            assertTrue(todoPage.getTodoTexts().contains(todo), "Expected Todo list to contain: " + todo + ", but it contained: " + todoPage.getTodoTexts());
        }

//      Practising data-driven parameterized test that groups several valid input types together with @CsvSource
        @DisplayName("Test input validation")
        @ParameterizedTest(name = "Should add todo: {0} - {1}")
        @CsvSource({"a, Single character",
                    "!, Punctuation mark",
                    "3, Number",
                    "é, Accented character",
                    "東京, Non-Latin characters"})
        public void shouldAcceptValidTodoText(String todo, String description) {
            todoPage.addTodo(todo);
            assertTrue(todoPage.getTodoTexts().contains(todo), "Expected Todo list to contain: " + todo + ", but it contained: " + todoPage.getTodoTexts());
        }


//        Emoji input could not be verified through WebDriver sendKeys due to ChromeDriver limitation.
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

