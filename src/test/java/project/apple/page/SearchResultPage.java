package project.apple.page;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$x;

public class SearchResultPage {

    private final ElementsCollection searchResultItems = $$x("//h2[@class='entry-title']//a");

    public String getHref(int index) {
        if (searchResultItems.size() <= index) {
            throw new RuntimeException("Index out of range");
        }
        return searchResultItems.get(index).getAttribute("href");
    }

}
