package com.apiframework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.response.Response;

public class LogUtils {
	private static final Logger log = LogManager.getLogger(LogUtils.class);
	
	// ── Test lifecycle ──
    public static void testStart(String testName) {
        log.info("╔══════════════════════════════════════════");
        log.info("║  START : " + testName);
        log.info("╚══════════════════════════════════════════");
    }

    public static void testPass(String testName) {
        log.info("╔══════════════════════════════════════════");
        log.info("║  PASSED: " + testName);
        log.info("╚══════════════════════════════════════════");
    }

    public static void testFail(String testName, String reason) {
        log.error("╔══════════════════════════════════════════");
        log.error("║  FAILED: " + testName);
        log.error("║  REASON: " + reason);
        log.error("╚══════════════════════════════════════════");
    }

    // ── Request/Response summary ──
    public static void logRequest(String method, String endpoint) {
        log.info("► REQUEST  : " + method + " " + endpoint);
    }

    public static void logResponse(Response response) {
        log.info("◄ RESPONSE : " + response.getStatusCode()
                + " | Time: " + response.getTime() + "ms"
                + " | Size: " + response.getBody().asByteArray().length + " bytes");
    }

    public static void logResponseBody(Response response) {
        log.debug("◄ BODY     :\n" + response.asPrettyString());
    }

    // ── General purpose ──
    public static void info(String message) {
        log.info(message);
    }

    public static void debug(String message) {
        log.debug(message);
    }

    public static void warn(String message) {
        log.warn(message);
    }

    public static void error(String message) {
        log.error(message);
    }

    public static void error(String message, Throwable t) {
        log.error(message, t);
    }

}
