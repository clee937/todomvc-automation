package tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;
import pages.TodoMVCReactPage;

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

        @Nested
        @DisplayName("Adding todos")
        class AddingTodos {
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
            void shouldNotAddTodoWhenPressingEnterMultipleTimesOnEmptyInput() {
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

            @Test
            @Disabled("Blocked by ChromeDriver limitation: sendKeys does not support non-BMP Unicode characters")
            void shouldSupportEmoji() {
                todoPage.addTodo("\uD83D\uDE00");
                assertTrue(todoPage.getTodoTexts().contains("\uD83D\uDE00"), "Expected Todo list to contain '\uD83D\uDE00'");
            }

        }

        @Nested
        @DisplayName("Editing todos")
        class EditingTodos {
            @DisplayName("Should edit an existing todo")
            @ParameterizedTest(name = "Should edit {0} to {1}")
            @CsvSource({
                    "Buy milk, Buy eggs",
                    "東京, 京都",
                    "a, b"
            })
            void shouldEditTodo(String originalTodo, String updatedTodo) {

                todoPage.addTodo(originalTodo);
                todoPage.editTodo(originalTodo, updatedTodo);

                List<String> todoTexts = todoPage.getTodoTexts();

                assertTrue(todoTexts.contains(updatedTodo),
                        "Expected Todo list to contain updated todo: '" + updatedTodo + "' but was " + todoTexts);
                assertFalse(todoTexts.contains(originalTodo),
                        "Expected Todo list not to contain original todo: '" + originalTodo + "'");
                assertEquals(1, todoTexts.size(),
                        "Expected Todo list to contain only the updated todo");
            }

            @Test
            @Disabled("Known issue: Escape does not cancel editing in TodoMVC React implementation")
            void shouldCancelEditWhenEscapePressed() {
                String todo = "Wrap birthday gift";
                todoPage.addTodo(todo);

                todoPage.startEditingTodoAndPressEscape(todo);

                List<String> todoTexts = todoPage.getTodoTexts();

                assertTrue(todoTexts.contains(todo),
                        "Expected Todo list to contain original todo after cancelling edit: '" + todo + "'");
                assertEquals(1, todoTexts.size(),
                        "Expected Todo list to contain only the original todo");
            }
        }

        @Nested
        @DisplayName("Completing todos")
        class CompletingTodos {
            @Test
            void shouldCompleteTodo() {
                String todo = "Wrap birthday gift";
                todoPage.addTodo(todo);
                todoPage.completeTodo(todo);

                assertTrue(todoPage.isTodoCompleted(todo),
                        "Expected todo to be completed: " + todo);
            }

            @Test
            void shouldMarkTodoAsIncomplete() {
                String todo = "Wrap birthday gift";
                todoPage.addTodo(todo);

                assertFalse(todoPage.isTodoCompleted(todo), "The todo: '" + todo + "' is already complete and shouldn't be.");

                todoPage.completeTodo(todo);
                todoPage.uncompleteTodo(todo);

                assertFalse(todoPage.isTodoCompleted(todo),
                        "Expected todo to be incomplete: " + todo);
            }
        }

        @Nested
        @DisplayName("Deleting todos")
        class DeletingTodos {
            @Test
            void shouldDeleteTodo() {
                String todo = "Write birthday card";
                todoPage.addTodo(todo);
                todoPage.deleteTodo(todo);
                List<String> todoTextsAfterDeletion = todoPage.getTodoTexts();

                assertFalse(todoTextsAfterDeletion.contains(todo),
                        "Expected Todo list not to contain deleted todo: '" + todo + "'");
                assertEquals(0, todoTextsAfterDeletion.size(),
                        "Expected Todo list to contain no todos after deletion");
            }

            @Test
            void shouldDeleteOnlySelectedTodo() {
                String firstTodo = "Buy milk";
                String secondTodo = "Buy birthday card";

                todoPage.addTodo(firstTodo);
                todoPage.addTodo(secondTodo);

                todoPage.deleteTodo(firstTodo);

                List<String> todoTextsAfterDeletion = todoPage.getTodoTexts();
                assertFalse(todoTextsAfterDeletion.contains(firstTodo), "Expected deleted todo to be removed from the list");
                assertEquals(1, todoTextsAfterDeletion.size(), "Expected exactly one todo to remain after deletion");
                assertTrue(todoTextsAfterDeletion.contains(secondTodo), "Expected remaining todo to be '" + secondTodo + "'");
            }
        }
}

