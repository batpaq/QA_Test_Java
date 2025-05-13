package com.book.spring.springboot.umag;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.*;

public class InventoryPage {
    public static final String URL = "/store/0/inventory";

    private final SelenideElement filterButton = $x("//span[text()='Фильтр']/..");
    private final SelenideElement fromDateButton = $x("(//button[@aria-label='Choose Date'])[1]");
    private final SelenideElement toDateButton = $x("(//button[@aria-label='Choose Date'])[2]");
    private final SelenideElement calendarTable = $x("//table[@role='grid']");
    private final SelenideElement userSearchButton = $x("//span[text()='Поиск']/..");
    private final ElementsCollection userSearchResults = $$x("//li[contains(@class, 'dropdown-list__item')]");
    private final SelenideElement statusDocumentDropdown = $x("//div[text()=' Черновик,  ']/..");
    private final ElementsCollection statusDocumentCheckboxes = $$x("//span[contains(@class, 'checkbox__checkmark')]");
    private final SelenideElement applyButton = $x("//span[text()='Применить']/..");
    private final ElementsCollection dateInputs = $$x("//span[contains(@class, 'p-calendar')]/input");

    public InventoryPage() {
        Selenide.open(URL);
    }

    public void filter() {
        filterButton.click();
    }

    public void setDateInputs() {
        LocalDate todayMinus3Days = LocalDate.now().minusDays(3);
        String fromDateFormatted = todayMinus3Days.format(DateTimeFormatter.ofPattern("dd"));
        LocalDate todayPlus13Days = todayMinus3Days.plusDays(13);
        String toDateFormatted = todayPlus13Days.format(DateTimeFormatter.ofPattern("dd"));

        fromDateButton.shouldBe(visible, Duration.ofSeconds(10)).click();
        calendarTable.shouldBe(visible);
        calendarTable.$x(".//span[text()='" + fromDateFormatted + "']").click();
        toDateButton.shouldBe(visible, Duration.ofSeconds(10)).click();
        calendarTable.$x(".//span[text()='" + toDateFormatted + "']").shouldBe(visible, Duration.ofSeconds(5)).click();
    }

    public void user() {
        userSearchButton.click();
        for (SelenideElement userItem : userSearchResults) {
            String text = userItem.getText().trim();
            if ("Миша Понкратов".equals(text)) {
                userItem.click();
                break;
            }
        }
    }

    public void statusDocument() {
        statusDocumentDropdown.click();

        for (SelenideElement checkbox : statusDocumentCheckboxes) {
            if (!checkbox.getAttribute("class").contains("checked")) {
                checkbox.click();
            }
        }
        for (SelenideElement checkbox : statusDocumentCheckboxes) {
            String text = checkbox.parent().getText().trim();
            if ("Черновик".equals(text) && !checkbox.getAttribute("class").contains("checked")) {
                checkbox.click();
            }
        }
    }

    public void setButton() {
        applyButton.click();
    }

    public void assertRowsMatchExpectations(ElementsCollection actualRows, Set<String> allowedStatuses, String expectedCreator, String fromDate, String toDate) {
        assertEquals(2, dateInputs.size(), "Ожидались два поля фильтрации по дате");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.ENGLISH);
        LocalDateTime fromDateTime = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime toDateTime = LocalDateTime.parse(toDate, formatter);

        actualRows.filter(visible).shouldBe(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(15));
        assertFalse(actualRows.isEmpty(), "Ошибка: таблица пуста или не найдена.");

        List<Executable> checks = new ArrayList<>();
        for (SelenideElement row : actualRows) {
            SelenideElement dateCell = row.$x("./td[3]").should(appear);
            SelenideElement creatorCell = row.$x("./td[4]").should(appear);
            SelenideElement statusCell = row.$x("./td[6]").should(appear);

            if (dateCell.getText() != null && creatorCell.getText() != null && statusCell.getText() != null) {
                String dateText = dateCell.getText().trim();
                String creator = creatorCell.getText().trim();
                String status = statusCell.getText().trim();
                LocalDateTime actualDateTime = LocalDateTime.parse(dateText, formatter);

                checks.add(() -> {
                    assertFalse(actualDateTime.isBefore(fromDateTime) || actualDateTime.isAfter(toDateTime), "Дата вне диапазона: " + actualDateTime);
                    assertEquals(expectedCreator, creator, "Создатель не совпадает: " + creator);
                    assertTrue(allowedStatuses.contains(status), "Найден неожиданный статус: " + status + " после фильтрации.");
                });
            } else {
                fail("Одна из ячеек (дата, создатель, статус) имеет значение null в строке таблицы.");
            }
        }
        assertAll("Проверка статусов и создателей строк таблицы", checks);
    }
}