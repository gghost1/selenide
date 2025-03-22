package project.apple.logs;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {

    private final Supplier<ConditionFactory> Waiter = () -> Awaitility.given()
            .ignoreException(Exception.class)
            .pollInterval(3, TimeUnit.SECONDS)
            .await()
            .dontCatchUncaughtExceptions()
            .atMost(100, TimeUnit.SECONDS);

    private boolean waitLogs(String expectedMessage) {
        WebDriver driver = WebDriverRunner.getWebDriver();
        AtomicBoolean isLogContains = new AtomicBoolean(false);

        Waiter.get().until(() -> {
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            logEntries.forEach(log -> System.out.println(log.getMessage()));
            isLogContains.set(logEntries.getAll().stream().anyMatch(log -> log.getMessage().contains(expectedMessage)));
            return isLogContains.get();
        });

        return isLogContains.get();
    }

    @Disabled
    @Test
    void test() {
        Selenide.open("http://85.192.34.140/logdelay/");
        boolean isLogContains = waitLogs("ThreadQA secret message after 5 sec");
        assertTrue(isLogContains);
    }

    @BeforeAll
    static void beforeAll() {
        DesiredCapabilities caps = new DesiredCapabilities();
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences prefs = new LoggingPreferences();

        // enable logs
        prefs.enable(LogType.PERFORMANCE, Level.ALL);
        prefs.enable(LogType.BROWSER, Level.ALL);

        // set logs enabling
        caps.setCapability("goog:loggingPrefs", prefs);
        caps.setCapability(ChromeOptions.CAPABILITY, options);


        Configuration.browserCapabilities = caps;
        Configuration.timeout = 10000;
        Configuration.pageLoadTimeout = 10000;


    }

}
