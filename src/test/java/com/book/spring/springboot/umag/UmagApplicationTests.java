package com.book.spring.springboot.umag;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

class UmagApplicationTests extends BaseTest {
    private final String phone = "7773939393";
    private final String password = "Gamu2024!";
    public static final SelenideElement SLEEP = $x("//div[contains(@class, 'p-progressbar-indeterminate')]");

    @Test
    void contextLoads() {
        LoginPage method = new LoginPage();
        method.setPhone(phone, password);
        SLEEP.shouldBe(disappear, Duration.ofSeconds(10));
        InventoryPage filter = new InventoryPage();
        SLEEP.shouldBe(disappear, Duration.ofSeconds(10));
        filter.filter();
        filter.setDateInputs();
        filter.user();
        filter.statusDocument();
        filter.setButton();
        SLEEP.shouldBe(disappear, Duration.ofSeconds(10));
        String fromDate = "10.05.2025 00:00";
        String toDate = "25.05.2025 23:59";
        ElementsCollection rows = $$x("//table[contains(@class, 'p-datatable')]/tbody/tr");
        Set<String> allowedStatuses = Set.of("Черновик", "Удален");
        String expectedCreator = "Миша";
        filter.assertRowsMatchExpectations(rows, allowedStatuses, expectedCreator, fromDate, toDate);

    }

}
