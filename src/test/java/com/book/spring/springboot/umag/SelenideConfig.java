package com.book.spring.springboot.umag;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.HashMap;

public class SelenideConfig {
    public static void setup() {
        Configuration.browser = "chrome";
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> prefs = new HashMap<>();
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);
        prefs.put("profile.default_content_setting_values.automatic_downloads", 1);
        prefs.put("download.default_directory", new File("build/downloads").getAbsolutePath());
        options.setExperimentalOption("prefs", prefs);
        Configuration.browserCapabilities = options;
    }
}

