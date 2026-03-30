package com.apiframework.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.apiframework.base.BaseTest;
import com.apiframework.constants.Endpoints;
import com.apiframework.models.Post;
import com.apiframework.utils.DataProviderUtils;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import java.util.Map;

public class DataDrivenTest extends BaseTest {
	
	private static final Logger log = LogManager.getLogger(DataDrivenTest.class);

    // ─────────────────────────────────────────────
    // Approach 1 — DataProvider with hardcoded data
    // Runs 3 times, once per row in createPostData()
    // ─────────────────────────────────────────────
    @Test(dataProvider = "createPostData",
          dataProviderClass = DataProviderUtils.class,
          priority = 1)
    @Story("Data Driven Testing")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create posts using hardcoded DataProvider — runs once per data row")
    public void testCreatePostWithDataProvider(int userId,
                                               String title,
                                               String body,
                                               int expectedStatus) {

        log.info(">>> DATA PROVIDER TEST | userId=" + userId
                + " | title=" + title
                + " | expectedStatus=" + expectedStatus);

        Post post = new Post(userId, title, body);

        Response response = given()
            .body(post)
        .when()
            .post(Endpoints.CREATE_POST)
        .then()
            .statusCode(expectedStatus)
            .extract().response();

        // Verify response fields match what we sent
        Assert.assertEquals(response.jsonPath().getString("title"), title,
                "Title in response does not match sent title");
        Assert.assertEquals(response.jsonPath().getInt("userId"), userId,
                "UserId in response does not match sent userId");
        Assert.assertNotNull(response.jsonPath().getString("id"),
                "Response should contain an ID");

        log.info("<<< PASSED | Created post id=" + response.jsonPath().getInt("id"));
    }

    // ─────────────────────────────────────────────
    // Approach 2 — DataProvider reading from Excel
    // Runs once per row in PostTestData.xlsx
    // ─────────────────────────────────────────────
    @Test(dataProvider = "createPostDataFromExcel",
          dataProviderClass = DataProviderUtils.class,
          priority = 2)
    @Story("Data Driven Testing")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create posts using Excel DataProvider — driven by external file")
    public void testCreatePostFromExcel(Map<String, String> testData) {

        int userId         = Integer.parseInt(testData.get("userId"));
        String title       = testData.get("title");
        String body        = testData.get("body");
        int expectedStatus = Integer.parseInt(testData.get("expected_status"));

        log.info(">>> EXCEL TEST | userId=" + userId
                + " | title=" + title
                + " | expectedStatus=" + expectedStatus);

        Post post = new Post(userId, title, body);

        Response response = given()
            .body(post)
        .when()
            .post(Endpoints.CREATE_POST)
        .then()
            .statusCode(expectedStatus)
            .extract().response();

        Assert.assertEquals(response.jsonPath().getString("title"), title,
                "Title mismatch for Excel row: " + testData);
        Assert.assertNotNull(response.jsonPath().getString("id"),
                "ID should be present in response");

        log.info("<<< PASSED | Excel row processed: " + testData);
    }
	

}
