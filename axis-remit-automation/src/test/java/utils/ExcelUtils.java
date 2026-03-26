package utils;

import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.util.HashMap;

public class ExcelUtils {

    static Sheet sheet;

    public static void loadExcel(String path, String sheetName) throws Exception {
        FileInputStream fis = new FileInputStream(path);
        Workbook workbook = WorkbookFactory.create(fis);
        sheet = workbook.getSheet(sheetName);
    }

    public static HashMap<String, String> getTestData(int rowNum) {
        HashMap<String, String> data = new HashMap<>();

        Row header = sheet.getRow(0);
        Row row = sheet.getRow(rowNum);

        for (int i = 0; i < header.getLastCellNum(); i++) {
            data.put(
                header.getCell(i).getStringCellValue(),
                row.getCell(i).toString()
            );
        }
        return data;
    }
}