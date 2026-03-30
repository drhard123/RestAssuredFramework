package com.apiframework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigReader {
	private static final Logger log = LogManager.getLogger(ConfigReader.class);
	private static Properties properties;
	
	//Static block runs once when class is loaded — loads the file into memory
	
	static {
		try {
			String configPath = "src/main/resources/config.properties";
			FileInputStream	fis = new FileInputStream(configPath);
			properties = new Properties();
			properties.load(fis);
			log.info("config.properties loaded successfully.");
		} catch (IOException e) {
			log.error("Failed to load config.properties: " + e.getMessage());
			throw new RuntimeException("config.properties not found. Check the path.");
		}
	}
	
	//Returns any value from the properties file by key
	public static String get(String key) {
		String value = properties.getProperty(key);
		if(value == null) {
			log.warn("Property key not found: "+ key);
		}
		return value;
		
	}
	
	// Convenience method used in BaseTest
	public static String getBaseUrl() {
		return get("base.url");
	}
	
	public static String getBasePath() {
		return get("base.path");
	}
	
	public static String getContentType() {
		return get("default.content.type");
	}
	
	
	
	public static String getAuthBaseUrl() {
	    return get("auth.base.url");
	}

	public static String getAuthUsername() {
	    return get("auth.username");
	}

	public static String getAuthPassword() {
	    return get("auth.password");
	}

	public static String getApiKey() {
	    return get("api.key");
	}

}
