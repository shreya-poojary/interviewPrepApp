package com.interviewprep.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton service for managing application configuration
 */
@Slf4j
public class ConfigurationService {
    private static ConfigurationService instance;
    private final Properties properties;
    
    private ConfigurationService() {
        properties = new Properties();
        loadProperties();
    }
    
    public static synchronized ConfigurationService getInstance() {
        if (instance == null) {
            instance = new ConfigurationService();
        }
        return instance;
    }
    
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                log.warn("Unable to find application.properties, using defaults");
                setDefaults();
                return;
            }
            properties.load(input);
            log.info("Configuration loaded successfully");
        } catch (IOException e) {
            log.error("Error loading configuration", e);
            setDefaults();
        }
    }
    
    private void setDefaults() {
        properties.setProperty("ai.service", "ollama");
        properties.setProperty("ai.ollama.url", "http://localhost:11434");
        properties.setProperty("ai.ollama.model", "llama3.1:8b");
        properties.setProperty("ui.theme", "dark");
        properties.setProperty("database.path", "data/interview_prep.db");
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

