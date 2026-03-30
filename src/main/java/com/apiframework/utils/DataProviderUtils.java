package com.apiframework.utils;

import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;

public class DataProviderUtils {
	private static final String EXCEL_PATH = "testdata/PostTestData.xlsx";

    // ── Approach 1: Hardcoded DataProvider (no external file needed) ──
    // Used for quick, stable test data that rarely changes
    @DataProvider(name = "createPostData")
    public static Object[][] createPostData() {
        return new Object[][] {
            { 1, "Java Automation Post",    "Written using RestAssured", 201 },
            { 2, "API Testing Best Practices", "Data driven approach",   201 },
            { 3, "TestNG Framework Guide",  "Using DataProvider",        201 }
        };
    }

    // ── Approach 2: Excel-driven DataProvider (enterprise standard) ──
    // Reads from external Excel — test data managed outside codebase
    @DataProvider(name = "createPostDataFromExcel")
    public static Object[][] createPostDataFromExcel() {
        List<Map<String, String>> rows = ExcelUtils.getTestData(
                EXCEL_PATH, "Posts");

        Object[][] data = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            data[i][0] = rows.get(i); // Each row passed as a Map
        }
        return data;
    }

}
