package com.apiframework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

	private static final Logger log = LogManager.getLogger(ExcelUtils.class);

	// Reads all rows from a sheet and returns as List of Maps
	// Each Map = one row, key=column header, value=cell value

	public static List<Map<String, String>> getTestData(String filePath, String sheetName){
		
		List<Map<String, String>> testData = new ArrayList<>();
		
		try (
				FileInputStream fis = new FileInputStream(filePath);
				Workbook workbook = new XSSFWorkbook(fis)
			){
			Sheet sheet = workbook.getSheet(sheetName);
			
			if (sheet == null) {
				throw new RuntimeException("Sheet '" + sheetName + "' not found in " + filePath);	
			}
			
			// Row 0 = headers
            Row headerRow = sheet.getRow(0);
            int columnCount = headerRow.getLastCellNum();
            
            // Read headers into a list
            List<String> headers = new ArrayList<>();
            for (int col = 0; col < columnCount; col++) {
                headers.add(headerRow.getCell(col).getStringCellValue().trim());
            }
            
            log.info("Excel headers found: " + headers);

            // Read data rows (start from row 1)
            for (int row = 1; row <= sheet.getLastRowNum(); row++) {
                Row dataRow = sheet.getRow(row);
                if (dataRow == null) continue;

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int col = 0; col < columnCount; col++) {
                    Cell cell = dataRow.getCell(col);
                    rowData.put(headers.get(col), getCellValueAsString(cell));
                }

                testData.add(rowData);
                log.info("Read row " + row + ": " + rowData);
            }
			
		} catch (IOException e) {
			log.error("Failed to read Excel file: " + e.getMessage());
            throw new RuntimeException("Excel file not found at: " + filePath);
		}
		
		return testData;
	}

	// Safely reads any cell type as String
	private static String getCellValueAsString(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			return String.valueOf((int) cell.getNumericCellValue());
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		default:
			return "";
		}
	}

}
