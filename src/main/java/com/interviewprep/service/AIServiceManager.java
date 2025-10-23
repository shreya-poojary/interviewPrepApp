package com.interviewprep.service;

import lombok.extern.slf4j.Slf4j;
import com.interviewprep.service.ConfigurationService;

/**
 * Manages AI service selection and switching between Bedrock and Ollama
 */
@Slf4j
public class AIServiceManager {
    
    private AIService currentService;
    private final OllamaService ollamaService;
    private final BedrockService bedrockService;
    private final ConfigurationService config;
    
    public AIServiceManager(ConfigurationService config) {
        this.config = config;
        
        // Initialize Ollama service
        String ollamaUrl = config.getProperty("ai.ollama.url", "http://localhost:11434");
        String ollamaModel = config.getProperty("ai.ollama.model", "llama3.2:latest");
        this.ollamaService = new OllamaService(ollamaUrl, ollamaModel);
        
        // Initialize Bedrock service
        String bedrockModel = config.getProperty("ai.bedrock.model", "anthropic.claude-3-5-sonnet-20241022-v2:0");
        String bedrockRegion = config.getProperty("ai.bedrock.region", "us-east-1");
        
        // Check for custom credentials
        String accessKeyId = config.getProperty("ai.bedrock.access.key.id", "");
        String secretAccessKey = config.getProperty("ai.bedrock.secret.access.key", "");
        
        if (!accessKeyId.isEmpty() && !secretAccessKey.isEmpty()) {
            this.bedrockService = new BedrockService(bedrockModel, bedrockRegion, accessKeyId, secretAccessKey);
        } else {
            this.bedrockService = new BedrockService(bedrockModel, bedrockRegion);
        }
        
        // Set default service based on configuration
        String preferredService = config.getProperty("ai.preferred.service", "ollama");
        setPreferredService(preferredService);
    }
    
    /**
     * Get the current AI service
     */
    public AIService getCurrentService() {
        return currentService;
    }
    
    /**
     * Switch to Ollama service
     */
    public boolean switchToOllama() {
        if (ollamaService.isAvailable()) {
            currentService = ollamaService;
            log.info("Switched to Ollama service");
            return true;
        } else {
            log.warn("Ollama service not available");
            return false;
        }
    }
    
    /**
     * Switch to Bedrock service
     */
    public boolean switchToBedrock() {
        if (bedrockService.isAvailable()) {
            currentService = bedrockService;
            log.info("Switched to Bedrock service");
            return true;
        } else {
            log.warn("Bedrock service not available");
            return false;
        }
    }
    
    /**
     * Set preferred service based on string
     */
    public boolean setPreferredService(String serviceName) {
        switch (serviceName.toLowerCase()) {
            case "bedrock":
                return switchToBedrock();
            case "ollama":
            default:
                return switchToOllama();
        }
    }
    
    /**
     * Get available services
     */
    public String[] getAvailableServices() {
        java.util.List<String> available = new java.util.ArrayList<>();
        
        if (ollamaService.isAvailable()) {
            available.add("Ollama");
        }
        
        if (bedrockService.isAvailable()) {
            available.add("Bedrock");
        }
        
        return available.toArray(new String[0]);
    }
    
    /**
     * Get current service name
     */
    public String getCurrentServiceName() {
        return currentService != null ? currentService.getServiceName() : "None";
    }
    
    /**
     * Test all services
     */
    public void testAllServices() {
        log.info("Testing AI services...");
        
        if (ollamaService.isAvailable()) {
            log.info("✅ Ollama: Available");
        } else {
            log.warn("❌ Ollama: Not available");
        }
        
        if (bedrockService.isAvailable()) {
            log.info("✅ Bedrock: Available");
        } else {
            log.warn("❌ Bedrock: Not available");
        }
    }
    
    /**
     * Generate response using current service
     */
    public String generate(String prompt) {
        if (currentService == null) {
            log.error("No AI service selected");
            return "Error: No AI service available";
        }
        
        return currentService.generate(prompt);
    }
    
    /**
     * Check if current service is available
     */
    public boolean isCurrentServiceAvailable() {
        return currentService != null && currentService.isAvailable();
    }
}
