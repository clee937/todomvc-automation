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
                assertTrue(todoPage.getTodoTexts().contains("Buy eggs"),
                        "Expected Todo list to contain 'Buy eggs'");
            }

            @Test
            void shouldNotAddEmptyTodo() {
                int currentTodoCount = todoPage.getTodoCount();
                todoPage.pressEnterOnEmptyInput();
                assertEquals(currentTodoCount, todoPage.getTodoCount(),
                        "Pressing Enter on an empty input should not create a new todo");
            }

            @Test
            void shouldNotAddTodoWhenPressingEnterMultipleTimesOnEmptyInput() {
                int currentTodoCount = todoPage.getTodoCount();

                todoPage.pressEnterOnEmptyInput();
                todoPage.pressEnterOnEmptyInput();
                todoPage.pressEnterOnEmptyInput();

                assertEquals(currentTodoCount, todoPage.getTodoCount(),
                        "Pressing Enter on an empty input should not create a new todo");
            }

            @Test
            void shouldNotAddEmptyTodoAfterExistingTodo() {
                todoPage.addTodo("Buy eggs");

                int currentTodoCount = todoPage.getTodoCount();
                todoPage.pressEnterOnEmptyInput();

                assertEquals(currentTodoCount, todoPage.getTodoCount(),
                        "Pressing Enter on an empty input should not create an additional todo");

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
                assertTrue(todoPage.getTodoTexts().contains(todo),
                        "Expected Todo list to contain: " + todo + ", but it contained: " + todoPage.getTodoTexts());
            }

            @DisplayName("Should accept numbers")
            @ParameterizedTest(name = "Should accept: \"{0}\"")
            @ValueSource(strings = {"0", "1", "10"})
            void shouldAcceptNumbers(String todo) {
                todoPage.addTodo(todo);
                assertTrue(todoPage.getTodoTexts().contains(todo),
                        "Expected Todo list to contain: " + todo + ", but it contained: " + todoPage.getTodoTexts());
            }

            @DisplayName("Should accept accented characters")
            @ParameterizedTest(name = "Should accept: \"{0}\"")
            @ValueSource(strings = {"é", "ñ", "ô"})
            void shouldAcceptAccentedCharacters(String todo) {
                todoPage.addTodo(todo);
                assertTrue(todoPage.getTodoTexts().contains(todo),
                        "Expected Todo list to contain: " + todo + ", but it contained: " + todoPage.getTodoTexts());
            }

            @DisplayName("Should accept non-Latin characters")
            @ParameterizedTest(name = "Should accept: \"{0}\"")
            @ValueSource(strings = {"東京", "مرحبا", "Γειά", "안녕"})
            void shouldAcceptNonLatinCharacters(String todo) {
                todoPage.addTodo(todo);
                assertTrue(todoPage.getTodoTexts().contains(todo),
                        "Expected Todo list to contain: " + todo + ", but it contained: " + todoPage.getTodoTexts());
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
                assertTrue(todoPage.getTodoTexts().contains(todo),
                        "Expected Todo list to contain: " + todo + ", but it contained: " + todoPage.getTodoTexts());
            }

            @Test
            @Disabled("Blocked by ChromeDriver limitation: sendKeys does not support non-BMP Unicode characters")
            void shouldSupportEmoji() {
                todoPage.addTodo("\uD83D\uDE00");
                assertTrue(todoPage.getTodoTexts().contains("\uD83D\uDE00"),
                        "Expected Todo list to contain '\uD83D\uDE00'");
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
        @DisplayName("Managing todo completion")
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

                assertFalse(todoPage.isTodoCompleted(todo),
                        "The todo: '" + todo + "' is already complete and shouldn't be.");

                todoPage.completeTodo(todo);
                todoPage.uncompleteTodo(todo);

                assertFalse(todoPage.isTodoCompleted(todo),
                        "Expected todo to be incomplete: " + todo);
            }

            @Test
            void shouldCompleteAllTodos() {
                String firstTodo = "Wrap birthday gift";
                String secondTodo = "Order water filters";
                String thirdTodo = "Book MOT";

                todoPage.addTodo(firstTodo);
                todoPage.addTodo(secondTodo);
                todoPage.addTodo(thirdTodo);

                todoPage.completeTodo(firstTodo);
                todoPage.toggleAllTodoCompletion();

                for (String todo: todoPage.getTodoTexts()) {
                    assertTrue(todoPage.isTodoCompleted(todo),
                            "Expected todo '" + todo + "' to be completed");
                }
            }

            @Test
            void shouldMarkAllTodosIncomplete() {
                String firstTodo = "Wrap birthday gift";
                String secondTodo = "Order water filters";
                String thirdTodo = "Book MOT";

                todoPage.addTodo(firstTodo);
                todoPage.addTodo(secondTodo);
                todoPage.addTodo(thirdTodo);

                todoPage.toggleAllTodoCompletion();
                todoPage.toggleAllTodoCompletion();

                for (String todo : todoPage.getTodoTexts()) {
                    assertFalse(todoPage.isTodoCompleted(todo),
                            "Expected todo '" + todo + "' to be incomplete");
                }
            }
        }

        @Nested
        @DisplayName("Deleting todos")
        class DeletingTodos {
            @Test
            void shouldDeleteActiveTodo() {
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
                assertFalse(todoTextsAfterDeletion.contains(firstTodo),
                        "Expected deleted todo to be removed from the list");
                assertEquals(1, todoTextsAfterDeletion.size(),
                        "Expected exactly one todo to remain after deletion");
                assertTrue(todoTextsAfterDeletion.contains(secondTodo),
                        "Expected remaining todo to be '" + secondTodo + "'");
            }

            @Test
            void shouldDeleteCompletedTodo() {
                String completedTodo = "Write birthday card";

                todoPage.addTodo(completedTodo);
                todoPage.completeTodo(completedTodo);
                todoPage.deleteTodo(completedTodo);

                List<String> todoTextsAfterDeletion = todoPage.getTodoTexts();

                assertFalse(todoTextsAfterDeletion.contains(completedTodo),
                        "Expected completed todo '" + completedTodo + "' to be removed");
                assertEquals(0, todoTextsAfterDeletion.size(),
                        "Expected no todos to remain after deletion");
            }
        }

        @Nested
        @DisplayName("Clearing completed todos")
        class ClearingCompletedTodos {

            @Test
            void shouldShowClearCompletedButtonWhenCompletedTodosExist() {
                String completedTodo = "Wash car";

                todoPage.addTodo(completedTodo);
                todoPage.completeTodo(completedTodo);

                assertTrue(todoPage.isClearCompletedButtonVisible(),
                        "Expected Clear completed button to be visible");
            }

            @Test
            void shouldClearAllCompletedTodos() {
                String completedTodo = "Bake cake";
                String activeTodo = "Wash car";

                todoPage.addTodo(completedTodo);
                todoPage.addTodo(activeTodo);
                todoPage.completeTodo(completedTodo);
                todoPage.clearCompletedTodos();

                List<String> todoTexts = todoPage.getTodoTexts();

                assertFalse(todoTexts.contains(completedTodo),
                        "Expected completed todo '" + completedTodo + "' to be removed");
                assertTrue(todoTexts.contains(activeTodo),
                        "Expected active todo '" + activeTodo + "' to remain");
                assertEquals(1, todoTexts.size(),
                        "Expected only active todos to remain after clearing completed todos");
            }

            @Test
            void shouldNotShowClearCompletedButtonWhenNoCompletedTodosExist() {
                String activeTodo = "Wash car";

                todoPage.addTodo(activeTodo);

                assertFalse(todoPage.isClearCompletedButtonVisible(),
                        "Expected Clear completed button not to be visible");
            }
        }

        @Nested
        @DisplayName("Filtering todos")
        class FilteringTodos {

            @Test
            void shouldFilterCompletedTodos() {
                String completedTodo = "Bake cake";
                String activeTodo = "Wash car";

                todoPage.addTodo(completedTodo);
                todoPage.addTodo(activeTodo);

                todoPage.completeTodo(completedTodo);

                todoPage.filterByCompleted();

                List<String> todoTexts = todoPage.getTodoTexts();

                assertTrue(todoTexts.contains(completedTodo),
                        "Expected completed todo '" + completedTodo + "' to be visible");
                assertFalse(todoTexts.contains(activeTodo),
                        "Expected active todo '" + activeTodo + "' not to be visible");
                assertEquals(1, todoTexts.size(),
                        "Expected only completed todos to be visible");
            }

            @Test
            void shouldFilterActiveTodos() {
                String completedTodo = "Wrap birthday gift";
                String activeTodo = "Wash car";

                todoPage.addTodo(completedTodo);
                todoPage.addTodo(activeTodo);

                todoPage.completeTodo(completedTodo);
                todoPage.filterByActive();

                List<String> todoTexts = todoPage.getTodoTexts();

                assertTrue(todoTexts.contains(activeTodo),
                        "Expected active todo '" + activeTodo + "' to be visible");
                assertFalse(todoTexts.contains(completedTodo),
                        "Expected completed todo '" + completedTodo + "' not to be visible");
                assertEquals(1, todoTexts.size(),
                        "Expected only active todos to be visible");
            }

            @Test
            void shouldShowAllTodosAfterRemovingFilter() {
                String completedTodo = "Bake cake";
                String activeTodo = "Make icing";

                todoPage.addTodo(completedTodo);
                todoPage.addTodo(activeTodo);

                todoPage.completeTodo(completedTodo);

                todoPage.filterByCompleted();

                List<String> todoTexts = todoPage.getTodoTexts();

                assertEquals(1, todoTexts.size(),
                        "Expected only completed todos to be visible after applying Completed filter");

                todoPage.filterByAll();

                todoTexts = todoPage.getTodoTexts();

                assertTrue(todoTexts.contains(completedTodo),
                        "Expected completed todo '" + completedTodo + "' to be visible after returning to All filter");
                assertTrue(todoTexts.contains(activeTodo),
                        "Expected active todo '" + activeTodo + "' to be visible after returning to All filter");
                assertEquals(2, todoTexts.size(),
                        "Expected all todos to be visible after returning to All filter");
            }
        }

        @Nested
        @DisplayName("Status bar")
        class StatusBar {

            @Test
            void shouldShowRemainingTodoCount() {
                String todo = "Wash car";

                todoPage.addTodo(todo);

                assertTrue(todoPage.isTodoCountVisible(),
                        "Expected todo count to be visible");
            }

            @Test
            void shouldNotShowRemainingTodoCountWhenNoTodosExist() {
                assertFalse(todoPage.isTodoCountVisible(),
                        "Expected todo count not to be visible when no todos exist");
            }

            @ParameterizedTest
            @CsvSource({
                    "1, 0, 1",
                    "3, 1, 2"
            })
            void shouldDisplayCorrectRemainingTodoCount(int totalTodos, int completedTodos, int expectedRemaining) {
                for (int i = 1; i <= totalTodos; i++) {
                    todoPage.addTodo("Todo " + i);
                }

                for (int i = 1; i <= completedTodos; i++) {
                    todoPage.completeTodo("Todo " + i);
                }

                assertEquals(expectedRemaining, todoPage.getRemainingTodoCount(),
                        "Expected remaining todo count to be " + expectedRemaining);
            }

            // Verify the status bar remains visible and displays 0 remaining todos when all todos are completed.
            // This differs from having no todos, where the footer is hidden.
            @Test
            void shouldDisplayZeroRemainingTodoCountWhenAllTodosCompleted() {
                String firstTodo = "Wash car";
                String secondTodo = "Wash hair";

                todoPage.addTodo(firstTodo);
                todoPage.addTodo(secondTodo);

                todoPage.completeTodo(firstTodo);
                todoPage.completeTodo(secondTodo);

                assertEquals(0, todoPage.getRemainingTodoCount(),
                        "Expected remaining todo count to be 0");
            }
        }
}

