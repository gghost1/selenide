package project.apple;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.chrome.ChromeOptions;

public abstract class TestConfiguration {

    static {
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10_000; // 10 секунд ожидания элементов
        Configuration.screenshots = true; // автоматические скриншоты при ошибках
        Configuration.headless = false; // когда true блокируется через Cloudflare

        // иммитация человечиских действий
        Selenide.sleep(2000 + (long)(Math.random() * 3000));
        Configuration.browserCapabilities = new ChromeOptions()
                .addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) ...");

        // подмена ip
//        Configuration.proxyEnabled = true;
//        Configuration.proxyHost = "proxy.example.com";
//        Configuration.proxyPort = 8080;
    }

}
