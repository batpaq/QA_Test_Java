package com.book.spring.springboot.umag;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static com.codeborne.selenide.Selenide.$$x;

class UmagApplicationTests extends BaseTest {
    private final String phone    = "7773939393";
    private final String password = "Gamu2024!";

    @Test
    void contextLoads() {
        LoginPage method = new LoginPage();
        method.setPhone(phone,password);
        Selenide.sleep(6000);
        InventoryPage filter  =new InventoryPage();
        Selenide.sleep(6000);
        filter.filter();
        filter.setDateInputs();
        filter.user();
        filter.statusDocument();
        filter.setButton();
        Selenide.sleep(3000);

        String fromDate = "12.05.2025 00:00";
        String toDate = "25.05.2025 23:59";
        ElementsCollection rows = $$x("//table[contains(@class, 'p-datatable')]/tbody/tr");
        Set<String> allowedStatuses = Set.of("Черновик", "Удален");
        String expectedCreator ="Миша";
        filter.assertRowsMatchExpectations(rows,allowedStatuses,expectedCreator,fromDate,toDate);

    }

}
