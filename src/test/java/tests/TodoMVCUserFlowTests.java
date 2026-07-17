package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.TodoMVCReactPage;

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
}
