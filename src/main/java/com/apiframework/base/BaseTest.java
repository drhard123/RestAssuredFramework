package com.apiframework.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.apiframework.utils.ConfigReader;
import com.apiframework.utils.LogUtils;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class BaseTest {
	
	protected static final Logger log = LogManager.getLogger(BaseTest.class);
	
	//Shared across all tests --built once, reused everywhere
	protected static RequestSpecification requestSpec;
	protected static ResponseSpecification responseSpec;
	
	@BeforeSuite
	public void setUp() {
		LogUtils.info("=================================================");
		LogUtils.info("=======Initialising Rest Assured Framework=======");
		LogUtils.info("=================================================");
		
		//RequestSpecBuilder sets common config applied to every request
		requestSpec = new RequestSpecBuilder()
				.setBaseUri(ConfigReader.getBaseUrl())
                .setBasePath(ConfigReader.getBasePath())
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)   // logs every request to console/file
                .build();
		
		// ResponseSpecBuilder sets common validations applied to every response
		responseSpec = new ResponseSpecBuilder()
				.log(LogDetail.ALL)  // logs every response to console/file
				.build();
		
		// Apply specs globally so you don't repeat them in every test
		RestAssured.requestSpecification = requestSpec;
		RestAssured.responseSpecification = responseSpec;
		
		LogUtils.info("Base URL		: " + ConfigReader.getBaseUrl());
		LogUtils.info("Base Path		: " + ConfigReader.getBasePath());
		LogUtils.info("Content Type	: " + ConfigReader.getContentType());
		LogUtils.info("==============================================");
        LogUtils.info("  Setup Complete — Suite ready to run");
        LogUtils.info("==============================================");
		
	}
	
	
	// Runs automatically after EVERY test method
    // Logs PASS or FAIL without any code in test classes
    @AfterMethod
    public void afterMethod(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        if (result.getStatus() == ITestResult.SUCCESS) {
            LogUtils.testPass(testName);
        } else if (result.getStatus() == ITestResult.FAILURE) {
            LogUtils.testFail(testName,
                    result.getThrowable() != null
                    ? result.getThrowable().getMessage()
                    : "Unknown failure");
        } else if (result.getStatus() == ITestResult.SKIP) {
            LogUtils.warn("SKIPPED: " + testName);
        }
    }

    @AfterSuite
    public void tearDown() {
        LogUtils.info("==============================================");
        LogUtils.info("  Suite execution complete");
        LogUtils.info("==============================================");
    }
	
}
