package com.book.spring.springboot.umag;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.function.Executable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.files.DownloadActions.click;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InventoryPage {
    public static final String URL = "/store/0/inventory";

    private final SelenideElement filter = $x("//span[text()='Фильтр']/..");
    private final SelenideElement buttonFromeDate = $x("(//button[@aria-label='Choose Date'])[1]");
    private final SelenideElement buttonToDate = $x("(//button[@aria-label='Choose Date'])[2]");
    private final SelenideElement calendarTable = $x("//table[@role='grid']");
    private final SelenideElement getUser = $x("//span[text()='Поиск']/..");
    private final ElementsCollection searchUser = $$x("//li[contains(@class, 'dropdown-list__item')]");
    private final SelenideElement getStatusDocument = $x("//div[text()=' Черновик,  ']/..");
    private final ElementsCollection searchStatusDocument = $$x("//span[contains(@class, 'checkbox__checkmark')]");
    private final SelenideElement button = $x("//span[text()='Применить']/..");
    private final ElementsCollection rows = $$x("//table[contains(@class, 'p-datatable')]/tbody/tr");
    private final ElementsCollection dateInputs = $$x("//span[contains(@class, 'p-calendar')]/input");

    public InventoryPage() {
        Selenide.open(URL);
    }


    public void filter() {
        filter.click();
    }

//    public void setDateInputs(){
//        buttonDate.shouldBe(Condition.visible, Duration.ofSeconds(10)).click();
//        calendarTable.shouldBe(Condition.visible);
//        calendarTable.$x(".//span[text()='12']").click();
//        buttonDate.shouldBe(Condition.visible, Duration.ofSeconds(10)).click();
//        if (!calendarTable.isDisplayed()) {
//            buttonDate.click();
//            calendarTable.shouldBe(visible);
//        }
//        calendarTable.$x(".//span[text()='25']").click();
//
//
//
//    }

    public void setDateInputs() {
        LocalDate today = LocalDate.now();
        String fromDate = today.format(DateTimeFormatter.ofPattern("dd"));

        LocalDate to = today.plusDays(13);
        String toDate = to.format(DateTimeFormatter.ofPattern("dd"));

        buttonFromeDate.shouldBe(Condition.visible, Duration.ofSeconds(10)).click();
        calendarTable.shouldBe(Condition.visible);
        calendarTable.$x(".//span[text()='" + fromDate + "']").click();

        buttonToDate.shouldBe(Condition.visible, Duration.ofSeconds(10)).click();

        calendarTable.$x(".//span[text()='" + toDate + "']").click();
    }

    public void user() {
        getUser.click();
        for (SelenideElement item : searchUser) {
            String text = item.getText().trim();
            if (text.equals("Миша Понкратов")) {
                item.click();
                break;
            }
        }

    }

    public void statusDocument() {
        getStatusDocument.click();

        for (SelenideElement item : searchStatusDocument) {
            if (!item.getAttribute("class").contains("checked")) {
                item.click();
            }
        }
        for (SelenideElement item : searchStatusDocument) {
            String text = item.parent().getText().trim();
            if (text.equals("Черновик")) {
                if (!item.getAttribute("class").contains("checked")) {
                    item.click();
                }
            }
        }

    }

    public void setButton() {
        button.click();
    }

    public void assertRowsMatchExpectations(
            ElementsCollection ro,
            Set<String> allowedStatuses,
            String expectedCreator,
            String fromDate, String toDate

    ) {
        assertEquals(2, dateInputs.size(), "Ожидались два поля фильтрации по дате");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.ENGLISH);
        LocalDateTime from = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime to = LocalDateTime.parse(toDate, formatter);

        rows.filter(visible).shouldBe(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(15));
        assertTrue(!rows.isEmpty(), "Ошибка: таблица пуста или не найдена.");

        List<Executable> checks = new ArrayList<>();
        for (SelenideElement row : rows) {
            String dateText = row.$x("./td[3]").getText().trim(); // колонка "Дата"
            String creator = row.$x("./td[4]").getText().trim(); // колонка "Имя создателя"
            String status = row.$x("./td[6]").getText().trim(); // Статус
            System.out.println(dateText);
            System.out.println(creator);
            System.out.println(status);
            LocalDateTime actualDate = LocalDateTime.parse(dateText, formatter);
            checks.add(() -> {
            assertTrue(!actualDate.isBefore(from) && !actualDate.isAfter(to),
                    "Дата вне диапазона: " + actualDate);
            assertEquals("Миша", creator, "Создатель не совпадает: " + creator);
            assertTrue(status.equals("Черновик") || status.equals("Удален"),
                    "Найден неожиданный статус: " + status + " после фильтрации 'Черновик' и 'Удален'.");
            });

        }
        assertAll("Проверка статусов и создателей строк таблицы", checks);


    }
}
