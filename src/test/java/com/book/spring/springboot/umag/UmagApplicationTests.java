package com.book.spring.springboot.umag;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

class UmagApplicationTests extends BaseTest {
    public static final SelenideElement SLEEP = $x("//div[contains(@class, 'p-progressbar-indeterminate')]");
    private final String phone = "7773939393";
    private final String password = "Gamu2024!";

    @Test
    void contextLoads() {
        LoginPage loginPage = new LoginPage();
        loginPage.setPhone(phone, password);
        SLEEP.shouldBe(exist, Duration.ofSeconds(10));
        InventoryPage inventoryPage = new InventoryPage();
        SLEEP.shouldBe(exist, Duration.ofSeconds(10));
        inventoryPage.filter();
        inventoryPage.setDateInputs();
        inventoryPage.user();
        inventoryPage.statusDocument();
        inventoryPage.setButton();
        SLEEP.shouldBe(exist, Duration.ofSeconds(10));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.ENGLISH);
        LocalDateTime from = LocalDate.now().minusDays(3).atStartOfDay();
        LocalDateTime to = from.plusDays(13);
        String fromDate = from.format(formatter);
        String toDate = to.format(formatter);
        ElementsCollection rows = $$x("//table[contains(@class, 'p-datatable')]/tbody/tr");
        Set<String> allowedStatuses = Set.of("Черновик", "Удален");
        String expectedCreator = "Миша";
        inventoryPage.assertRowsMatchExpectations(rows, allowedStatuses, expectedCreator, fromDate, toDate);
    }
}