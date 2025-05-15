package com.book.spring.springboot.umag;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

class UmagApplicationTests extends BaseTest {

    public static final SelenideElement SLEEP = $x("//div[contains(@class, 'p-progressbar-indeterminate')]");
    private static final Logger LOGGER = Logger.getLogger(UmagApplicationTests.class.getName());

    @Test
    void contextLoads() {
        String phone = "7773939393";
        String password = "Gamu2024!";
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
        ExportExcelVerifier exportExcelVerifier = new ExportExcelVerifier();
        exportExcelVerifier.downloadExcel();
        File downloadedFile = new File("build/downloads/Список_инвентаризаций.xls");
        System.out.println("Чтение файла: " + downloadedFile.getAbsolutePath());
        try {
            List<List<String>> excelData = exportExcelVerifier.readExcel(downloadedFile);
            ElementsCollection rows = $$x("//table[contains(@class, 'p-datatable')]/tbody/tr");
            exportExcelVerifier.assertTableMatchesExcel(rows, excelData);
        } catch (IOException e) {
            LOGGER.severe("Ошибка при чтении Excel-файла: " + e.getMessage());
            Assertions.fail("Не удалось прочитать Excel-файл: " + e.getMessage());
        }
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