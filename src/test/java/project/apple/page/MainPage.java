package project.apple.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class MainPage {

    public MainPage(String url) {
        Selenide.open(url);
    }

    private final SelenideElement searchInputField = $x("//input[@name='s']");

    public SearchResultPage search(String text) {
        searchInputField.setValue(text);
        searchInputField.pressEnter();
        return new SearchResultPage();
    }

}
