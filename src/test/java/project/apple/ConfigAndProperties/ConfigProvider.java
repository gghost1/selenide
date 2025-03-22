package project.apple.ConfigAndProperties;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public interface ConfigProvider {
    Config config = readConfig();

    static Config readConfig() {
        return ConfigFactory.load("application.conf");
    }

    String URL = readConfig().getString("url");
    int AGE = readConfig().getInt("age");
    String ADMIN_NAME = readConfig().getString("user.admin.name");
}
