package com.apiframework.utils;

import static io.restassured.RestAssured.given;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AuthUtils {
	private static final Logger log = LogManager.getLogger(AuthUtils.class);

    // Cached token — fetched once, reused across all tests
    private static String cachedToken = null;

    // ── Bearer Token (Login → extract token → reuse) ──
    @Step("Fetch Bearer token from {0}")
    public static String getBearerToken() {

        if (cachedToken != null) {
            log.info("Returning cached Bearer token.");
            return cachedToken;
        }

        log.info("Fetching new Bearer token from: "
                + ConfigReader.getAuthBaseUrl()
                + ConfigReader.get("auth.login.endpoint"));

        String requestBody = "{"
                + "\"username\": \"" + ConfigReader.getAuthUsername() + "\","
                + "\"password\": \"" + ConfigReader.getAuthPassword() + "\","
                + "\"expiresInMins\": 30"
                + "}";

        Response response = given()
                .baseUri(ConfigReader.getAuthBaseUrl())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ConfigReader.get("auth.login.endpoint"))
                .then()
                .statusCode(200)
                .extract().response();

        cachedToken = response.jsonPath().getString("accessToken");
        log.info("Bearer token fetched and cached successfully.");
        return cachedToken;
    }

    // ── Basic Auth ──
    // Returns base64 encoded "username:password"
    // RestAssured handles encoding automatically via .auth().basic()
    public static String getBasicAuthUsername() {
        return ConfigReader.getAuthUsername();
    }

    public static String getBasicAuthPassword() {
        return ConfigReader.getAuthPassword();
    }

    // ── API Key ──
    public static String getApiKey() {
        return ConfigReader.getApiKey();
    }

    // Reset cached token (call between test suites if needed)
    public static void clearCachedToken() {
        cachedToken = null;
        log.info("Cached token cleared.");
    }

}
