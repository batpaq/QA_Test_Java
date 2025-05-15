package com.book.spring.springboot.umag;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.awaitility.Awaitility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExportExcelVerifier {

    private static final Logger LOGGER = Logger.getLogger(ExportExcelVerifier.class.getName());

    public void downloadExcel() {
        File downloadDir = new File(Configuration.downloadsFolder);
        cleanDownloadFolder(downloadDir);

        List<String> beforeFiles = Arrays.stream(Objects.requireNonNull(downloadDir.listFiles()))
                .map(File::getName)
                .toList();

        select500PerPage();

        SelenideElement export = $x("//button[@class='small withDropdown']");
        SelenideElement getExport = $x("//li[@class='p-element p-menuitem ng-star-inserted']");

        export.click();
        getExport.shouldBe(visible, Duration.ofSeconds(10)).click();

        Awaitility.await()
                .atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(1))
                .until(() -> {
                    File[] files = downloadDir.listFiles((dir, name) ->
                            name.matches(".*\\.(xls|xlsx)$") && !beforeFiles.contains(name));
                    return files != null && files.length > 0;
                });

        try {
            Thread.sleep(500); // Ждём, пока обновятся файлы
        } catch (InterruptedException e) {
            LOGGER.severe("Поток был прерван: " + e.getMessage());
        }

        File[] afterFiles = downloadDir.listFiles();
        if (afterFiles != null) {
            for (File file : afterFiles) {
                if (!beforeFiles.contains(file.getName()) && file.getName().matches(".*\\.(xls|xlsx)$")) {
                    System.out.println("🟢 New downloaded file: " + file.getName());
                    System.out.println("File size: " + file.length() + " bytes");
                }
            }
        }
    }

    private void cleanDownloadFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    LOGGER.warning("Не удалось удалить файл: " + file.getName());
                }
            }
        }
    }

    public List<List<String>> readExcel(File file) throws IOException {
        List<List<String>> data = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook;

            if (file.getName().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (file.getName().endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("Неизвестный формат файла: " + file.getName());
            }

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    String cellValue = formatter.formatCellValue(cell);
                    rowData.add(cellValue);
                }
                data.add(rowData);
            }

            workbook.close();
        }

        return data;
    }

    public void assertTableMatchesExcel(ElementsCollection actualRows, List<List<String>> excelData) {
        List<List<String>> uiData = actualRows.stream()
                .map(row -> {
                    String id = row.$x("./td[2]").getText().trim();
                    String status = row.$x("./td[6]").getText().trim();
                    String creator = row.$x("./td[4]").getText().trim();

                    if (status.equals("Удален")) {
                        status = "Удалена";
                    }

                    return List.of(id, status, creator);
                })
                .toList();

        if (!excelData.isEmpty() && !excelData.get(0).isEmpty() && excelData.get(0).get(0).equalsIgnoreCase("ID")) {
            excelData = excelData.subList(1, excelData.size());
        }

        List<List<String>> trimmedExcel = excelData.stream()
                .map(row -> {
                    String id = !row.isEmpty() ? row.get(0).trim() : "";
                    String status = row.size() > 1 ? row.get(1).trim() : "";
                    String creator = row.size() > 2 ? row.get(2).trim() : "";
                    return List.of(id, status, creator);
                })
                .toList();

        assertEquals(trimmedExcel.size(), uiData.size(), "Размеры таблиц не совпадают");

        for (int i = 0; i < trimmedExcel.size(); i++) {
            List<String> expectedRow = trimmedExcel.get(i);
            List<String> actualRow = uiData.get(i);

            System.out.println("Expected Row: " + expectedRow);
            System.out.println("Actual Row:   " + actualRow);

            assertEquals(expectedRow, actualRow, "Несовпадение в строке " + (i + 1));
        }
    }

    public void select500PerPage() {
        SelenideElement dropdown = $x("//*[local-name()='svg' and contains(@class, 'p-dropdown-trigger-icon')]");
        dropdown.click();
        SelenideElement option500 = $x("//span[contains(text(),'500')]");
        option500.shouldBe(visible, Duration.ofSeconds(5)).click();

        $$x("//table[contains(@class, 'p-datatable')]/tbody/tr")
                .shouldHave(CollectionCondition.sizeGreaterThan(100), Duration.ofSeconds(10));
    }
}
