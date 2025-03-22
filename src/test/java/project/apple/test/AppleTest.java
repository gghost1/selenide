package project.apple.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import project.apple.TestConfiguration;
import project.apple.page.MainPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppleTest extends TestConfiguration {

    private static final String APPLE_URL = "https://appleinsider.ru/";
    private static final String APPLE_SEARCH_URL = "Чем отличается iPhone 13 от iPhone 12";
    private static final String APPLE_HREF = "13";

    @Disabled
    @Test
    public void searchTest() {
        MainPage mainPage = new MainPage(APPLE_URL);
        String href = mainPage.search(APPLE_SEARCH_URL).getHref(1);
        assertTrue(href.contains(APPLE_HREF));
    }

}
