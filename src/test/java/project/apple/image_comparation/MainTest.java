package project.apple.image_comparation;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.OutputType;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

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
