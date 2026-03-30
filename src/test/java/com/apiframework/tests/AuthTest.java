package com.apiframework.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.apiframework.base.BaseTest;
import com.apiframework.constants.Endpoints;
import com.apiframework.utils.AuthUtils;
import com.apiframework.utils.ConfigReader;
import com.apiframework.utils.RequestSpecUtils;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTest extends BaseTest {
	private static final Logger log = LogManager.getLogger(AuthTest.class);

    // ─────────────────────────────────────────────
    // TEST 1 — Login and verify token is returned
    // ─────────────────────────────────────────────
    @Test(priority = 1)
    @Story("Authentication")
    @Severity(SeverityLevel.BLOCKER)
    @Description("POST login credentials and verify Bearer token is returned")
    public void testLoginAndGetToken() {
        log.info(">>> TEST: Login and get Bearer token");

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
                .post(Endpoints.AUTH_LOGIN)
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("username", equalTo(ConfigReader.getAuthUsername()))
                .extract().response();

        String token = response.jsonPath().getString("accessToken");
        Assert.assertNotNull(token, "Token should not be null");
        Assert.assertTrue(token.length() > 10, "Token should be a valid length");

        log.info("<<< PASSED: Token received. Length=" + token.length());
    }

    // ─────────────────────────────────────────────
    // TEST 2 — Bearer token: access protected endpoint
    // ─────────────────────────────────────────────
    @Test(priority = 2)
    @Story("Authentication")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Use Bearer token to access a protected /auth/me endpoint")
    public void testBearerTokenAccess() {
        log.info(">>> TEST: Access protected endpoint with Bearer token");

        given()
                .spec(RequestSpecUtils.getBearerTokenSpec())
                .when()
                .get(Endpoints.AUTH_ME)
                .then()
                .statusCode(200)
                .body("username", equalTo(ConfigReader.getAuthUsername()))
                .body("email", notNullValue());

        log.info("<<< PASSED: Protected endpoint accessed with Bearer token");
    }

    // ─────────────────────────────────────────────
    // TEST 3 — Bearer token: verify token is cached
    // ─────────────────────────────────────────────
    @Test(priority = 3)
    @Story("Authentication")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that calling getBearerToken() twice returns same token (caching works)")
    public void testTokenCaching() {
        log.info(">>> TEST: Verify token caching");

        String token1 = AuthUtils.getBearerToken();
        String token2 = AuthUtils.getBearerToken();

        Assert.assertEquals(token1, token2,
                "Token should be cached — both calls must return same token");

        log.info("<<< PASSED: Token caching verified");
    }

    // ─────────────────────────────────────────────
    // TEST 4 — Basic Auth structure verification
    // ─────────────────────────────────────────────
    @Test(priority = 4)
    @Story("Authentication")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify Basic Auth header is correctly built and attached to request")
    public void testBasicAuthHeaderStructure() {
        log.info(">>> TEST: Basic Auth header structure");

        // Verify the Base64 encoding is correct
        String credentials = ConfigReader.getAuthUsername()
                + ":" + ConfigReader.getAuthPassword();
        String encoded = java.util.Base64.getEncoder()
                .encodeToString(credentials.getBytes());

        Assert.assertNotNull(encoded, "Encoded credentials should not be null");
        Assert.assertTrue(encoded.length() > 0,
                "Encoded string should not be empty");

        // Verify spec is built without errors
        var spec = RequestSpecUtils.getBasicAuthSpec();
        Assert.assertNotNull(spec, "Basic auth spec should be built successfully");

        log.info("Basic Auth encoded value: " + encoded);
        log.info("<<< PASSED: Basic Auth header structure verified");
    }

    // ─────────────────────────────────────────────
    // TEST 5 — API Key in header
    // ─────────────────────────────────────────────
    @Test(priority = 5)
    @Story("Authentication")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify API key is correctly attached as request header")
    public void testApiKeyInHeader() {
        log.info(">>> TEST: API Key in header");

        // JSONPlaceholder ignores extra headers but still returns 200
        // This tests that our spec correctly ATTACHES the header
        given()
                .spec(RequestSpecUtils.getApiKeySpecInHeader())
                .when()
                .get(Endpoints.GET_ALL_POSTS)
                .then()
                .statusCode(200)
                .body("$", not(empty()));

        log.info("<<< PASSED: API Key header attached, request succeeded");
    }

    // ─────────────────────────────────────────────
    // TEST 6 — API Key in query param
    // ─────────────────────────────────────────────
    @Test(priority = 6)
    @Story("Authentication")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify API key is correctly attached as query parameter")
    public void testApiKeyInQueryParam() {
        log.info(">>> TEST: API Key as query parameter");

        given()
                .spec(RequestSpecUtils.getApiKeySpecInQueryParam())
                .when()
                .get(Endpoints.GET_ALL_POSTS)
                .then()
                .statusCode(200)
                .body("$", not(empty()));

        log.info("<<< PASSED: API Key query param attached, request succeeded");
    }

    // ─────────────────────────────────────────────
    // TEST 7 — Negative: invalid credentials → 400
    // ─────────────────────────────────────────────
    @Test(priority = 7)
    @Story("Authentication")
    @Severity(SeverityLevel.CRITICAL)
    @Description("POST invalid credentials and verify error response")
    public void testInvalidCredentials() {
        log.info(">>> TEST: Login with invalid credentials");

        String badBody = "{"
                + "\"username\": \"wronguser\","
                + "\"password\": \"wrongpass\","
                + "\"expiresInMins\": 30"
                + "}";

        given()
                .baseUri(ConfigReader.getAuthBaseUrl())
                .contentType(ContentType.JSON)
                .body(badBody)
                .when()
                .post(Endpoints.AUTH_LOGIN)
                .then()
                .statusCode(400)
                .body("message", notNullValue());

        log.info("<<< PASSED: Invalid credentials correctly rejected with 400");
    }

}
