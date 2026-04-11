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
		DataFormatter formatter = new DataFormatter();

		Row headerRow = sheet.getRow(0);
		Row dataRow = sheet.getRow(rowNum);

		for (int i = 0; i < headerRow.getLastCellNum(); i++) {

			String key = headerRow.getCell(i).getStringCellValue();

			Cell cell = dataRow.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

			String value = "";

			if (cell != null) {
				value = formatter.formatCellValue(cell).trim();
			}

			data.put(key, value);
		}

		return data;
	}
}