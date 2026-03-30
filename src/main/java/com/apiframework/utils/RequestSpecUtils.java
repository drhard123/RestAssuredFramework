package com.apiframework.utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecUtils {
	// ── No Auth spec (JSONPlaceholder — open API) ──
    public static RequestSpecification getNoAuthSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getBaseUrl())
                .setContentType(ContentType.JSON)
                .build();
    }

    // ── Bearer Token spec ──
    public static RequestSpecification getBearerTokenSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getAuthBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization",
                           "Bearer " + AuthUtils.getBearerToken())
                .build();
    }

    // ── Basic Auth spec ──
    public static RequestSpecification getBasicAuthSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getAuthBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization",
                        "Basic " + java.util.Base64.getEncoder()
                        .encodeToString((AuthUtils.getBasicAuthUsername()
                        + ":" + AuthUtils.getBasicAuthPassword())
                        .getBytes()))
                .build();
    }

    // ── API Key spec (in header) ──
    public static RequestSpecification getApiKeySpecInHeader() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("x-api-key", AuthUtils.getApiKey())
                .build();
    }

    // ── API Key spec (in query param) ──
    public static RequestSpecification getApiKeySpecInQueryParam() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getBaseUrl())
                .setContentType(ContentType.JSON)
                .addQueryParam("api_key", AuthUtils.getApiKey())
                .build();
    }

}
