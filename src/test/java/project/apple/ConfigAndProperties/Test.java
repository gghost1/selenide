package project.apple.ConfigAndProperties;

import java.io.IOException;

public class Test {

    @org.junit.jupiter.api.Test
    void propertiesTest() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("application.properties"));
        String url = System.getProperty("url");
    }

    @org.junit.jupiter.api.Test
    void confTest() {
        String name = ConfigProvider.ADMIN_NAME;
    }

}
