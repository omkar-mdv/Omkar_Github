package utils;

import java.io.FileInputStream;
import org.apache.poi.xssf.usermodel.*;

public class ExcelUtils {

    public static String getCellData(String filePath, String sheet, int row, int col) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sh = wb.getSheet(sheet);
            return sh.getRow(row).getCell(col).toString();
        } catch (Exception e) {
            return "";
        }
    }
}