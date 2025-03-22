package project.apple.image_comparation;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @BeforeAll
    static void beforeAll() {
        Configuration.browser = "chrome";
    }

    @BeforeEach
    void setUp() {
        // Уникальная директория для данных пользователя Chrome
        String timestamp = LocalDateTime.now().toString().replaceAll("[:.]", "-");
        String userDataDir = Paths.get(System.getProperty("user.dir"), "target", "chrome_data", timestamp).toString();

        // Удаление директории, если существует
        FileUtils.deleteQuietly(new File(userDataDir));

        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=990,844",
                "--user-data-dir=" + userDataDir,
                "--remote-allow-origins=*"
        );

        options.addArguments(
                "--headless=new",
                "--disable-gpu",
                "--disable-software-rasterizer"
        );

        // Настройка загрузки файлов
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", System.getProperty("user.dir") + "/target/downloads");
        prefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", prefs);

        Configuration.browserCapabilities = options;
        Configuration.browserSize = "990x844";
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }

    @Test
    void test_iphon12Pro(TestInfo info) {
        Configuration.browserSize = "990x844";
        Selenide.open("https://threadqa.ru");
        assertScreen(info);
    }

    private void assertScreen(TestInfo info) {
        String expectedName = info.getTestMethod().get().getName();
        String expectedDir = "src/test/resources/sreens";

        File actualFile = Selenide.screenshot(OutputType.FILE);
        File expectedFile = new File(expectedDir + "/" + expectedName + ".png");

        if (!expectedFile.exists()) {
            addImageToAllure(expectedName, actualFile);
            throw new RuntimeException("Expected image not found");
        }

        BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(expectedDir + "/" + expectedName + ".png");
        BufferedImage actualImage = ImageComparisonUtil.readImageFromResources(actualFile.toPath().toString());

        File resultImage = new File("build/diffs/diff_" + expectedName + ".png");
        ImageComparison imageComparison = new ImageComparison(expectedImage, actualImage, resultImage);
        ImageComparisonResult result = imageComparison.compareImages();

        if (!result.getImageComparisonState().equals(ImageComparisonState.MATCH)) {
            addImageToAllure("actual", actualFile);
            addImageToAllure("expected", expectedFile);
            addImageToAllure("diff", resultImage);
        }

        assertEquals(ImageComparisonState.MATCH, result.getImageComparisonState());
    }

    private void addImageToAllure(String name, File file) {
        try {
            byte[] image = Files.readAllBytes(file.toPath());
            saveScreenshot(name, image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Attachment(value = "{name}", type = "image/png")
    private static byte[] saveScreenshot(String name, byte[] image) {
        return image;
    }


}
