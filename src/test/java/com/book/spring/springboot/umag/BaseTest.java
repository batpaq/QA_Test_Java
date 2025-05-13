package com.book.spring.springboot.umag;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    @BeforeEach
    public void setUp() {
        Configuration.baseUrl = "https://dev-web.umag.kz";
        Configuration.timeout = 10000;
        Configuration.pageLoadTimeout = 100000;
    }

    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }
}
