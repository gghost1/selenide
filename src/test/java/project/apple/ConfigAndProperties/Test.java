package project.apple.ConfigAndProperties;

import org.junit.jupiter.api.Disabled;

import java.io.IOException;

public class Test {

    @Disabled
    @org.junit.jupiter.api.Test
    void propertiesTest() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("application.properties"));
        String url = System.getProperty("url");
    }

    @Disabled
    @org.junit.jupiter.api.Test
    void confTest() {
        String name = ConfigProvider.ADMIN_NAME;
    }

}
