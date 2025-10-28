package com.interviewprep.service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.util.List;
import java.util.ArrayList;

/**
 * AWS Bedrock service using Converse API
 */
@Slf4j
public class BedrockService implements AIService {
    
    private final BedrockRuntimeClient bedrockClient;
    private final String modelId;
    private final String region;
    private final Gson gson;
    
    public BedrockService(String modelId, String region) {
        this.modelId = modelId;
        this.region = region;
        this.gson = new Gson();
        
        try {
            // Try to get credentials from environment variables first
            String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
            String secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
            String sessionToken = System.getenv("AWS_SESSION_TOKEN");
            
            log.info("Environment variables - AccessKeyId: {}, SecretAccessKey: {}, SessionToken: {}", 
                    accessKeyId != null ? "SET" : "NULL", 
                    secretAccessKey != null ? "SET" : "NULL", 
                    sessionToken != null ? "SET" : "NULL");
            
            if (accessKeyId != null && secretAccessKey != null) {
                log.info("Using environment variables for AWS credentials");
                if (sessionToken != null && !sessionToken.isEmpty()) {
                    log.info("Using session credentials (temporary credentials)");
                    this.bedrockClient = BedrockRuntimeClient.builder()
                            .region(Region.of(region))
                            .credentialsProvider(() -> AwsSessionCredentials.create(accessKeyId, secretAccessKey, sessionToken))
                            .build();
                } else {
                    log.info("Using basic credentials (permanent credentials)");
                    this.bedrockClient = BedrockRuntimeClient.builder()
                            .region(Region.of(region))
                            .credentialsProvider(() -> AwsBasicCredentials.create(accessKeyId, secretAccessKey))
                            .build();
                }
            } else {
                log.info("Using default credentials provider");
                this.bedrockClient = BedrockRuntimeClient.builder()
                        .region(Region.of(region))
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .build();
            }
            
            // Test the credentials immediately
            log.info("Testing Bedrock client initialization...");
            try {
                // Try a simple test call to see if credentials work
                String testResponse = generate("Test");
                log.info("Bedrock test successful: {}", testResponse.substring(0, Math.min(50, testResponse.length())));
            } catch (Exception e) {
                log.error("Bedrock test failed during initialization", e);
            }
            log.info("Bedrock client initialized for model: {} in region: {}", modelId, this.region);
        } catch (Exception e) {
            log.error("Failed to initialize Bedrock client", e);
            throw new RuntimeException("Failed to initialize Bedrock client", e);
        }
    }
    
    public BedrockService(String modelId, String region, String accessKeyId, String secretAccessKey) {
        this.modelId = modelId;
        this.region = region;
        this.gson = new Gson();
        
        try {
            this.bedrockClient = BedrockRuntimeClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(() -> AwsBasicCredentials.create(accessKeyId, secretAccessKey))
                    .build();
            log.info("Bedrock client initialized with custom credentials for model: {} in region: {}", modelId, this.region);
        } catch (Exception e) {
            log.error("Failed to initialize Bedrock client with custom credentials", e);
            throw new RuntimeException("Failed to initialize Bedrock client", e);
        }
    }
    
    @Override
    public String generate(String prompt) {
        try {
            // Use InvokeModel API for Claude models
            if (modelId.startsWith("anthropic.claude")) {
                return invokeClaudeModel(prompt);
            } else if (modelId.startsWith("amazon.titan")) {
                return invokeTitanModel(prompt);
            } else {
                // Default to Claude
                return invokeClaudeModel(prompt);
            }
            
        } catch (Exception e) {
            log.error("Error calling Bedrock API", e);
            return "Error: " + e.getMessage();
        }
    }
    
    private String invokeClaudeModel(String prompt) throws Exception {
        // Validate and truncate prompt if necessary
        String processedPrompt = validateAndTruncatePrompt(prompt);
        
        // Create the request body for Claude using Gson for proper JSON construction
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("anthropic_version", "bedrock-2023-05-31");
        requestBody.addProperty("max_tokens", 1000);
        
        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", processedPrompt);
        messages.add(message);
        requestBody.add("messages", messages);
        
        String requestBodyString = gson.toJson(requestBody);
        log.debug("Claude request body: {}", requestBodyString);
        
        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(modelId)
                .body(SdkBytes.fromUtf8String(requestBodyString))
                .contentType("application/json")
                .build();
        
        InvokeModelResponse response = bedrockClient.invokeModel(request);
        String responseBody = response.body().asUtf8String();
        
        // Parse the response
        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
        if (jsonResponse.has("content") && jsonResponse.get("content").isJsonArray()) {
            JsonObject content = jsonResponse.getAsJsonArray("content").get(0).getAsJsonObject();
            if (content.has("text")) {
                String text = content.get("text").getAsString();
                log.debug("Bedrock Claude response: {}", text);
                return text;
            }
        }
        
        log.warn("Unexpected response format from Claude");
        return "No response generated";
    }
    
    private String invokeTitanModel(String prompt) throws Exception {
        // Validate and truncate prompt if necessary
        String processedPrompt = validateAndTruncatePrompt(prompt);
        
        // Create the request body for Titan using Gson for proper JSON construction
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("inputText", processedPrompt);
        
        JsonObject textGenerationConfig = new JsonObject();
        textGenerationConfig.addProperty("maxTokenCount", 1000);
        textGenerationConfig.addProperty("temperature", 0.7);
        textGenerationConfig.addProperty("topP", 0.9);
        requestBody.add("textGenerationConfig", textGenerationConfig);
        
        String requestBodyString = gson.toJson(requestBody);
        log.debug("Titan request body: {}", requestBodyString);
        
        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(modelId)
                .body(SdkBytes.fromUtf8String(requestBodyString))
                .contentType("application/json")
                .build();
        
        InvokeModelResponse response = bedrockClient.invokeModel(request);
        String responseBody = response.body().asUtf8String();
        
        // Parse the response
        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
        if (jsonResponse.has("results") && jsonResponse.get("results").isJsonArray()) {
            JsonObject result = jsonResponse.getAsJsonArray("results").get(0).getAsJsonObject();
            if (result.has("outputText")) {
                String text = result.get("outputText").getAsString();
                log.debug("Bedrock Titan response: {}", text);
                return text;
            }
        }
        
        log.warn("Unexpected response format from Titan");
        return "No response generated";
    }
    
    @Override
    public boolean isAvailable() {
        try {
            return testConnection();
        } catch (Exception e) {
            log.warn("Bedrock service not available: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getServiceName() {
        return "AWS Bedrock (" + modelId + ")";
    }
    
    @Override
    public boolean testConnection() {
        try {
            // Test with a simple prompt
            String testResponse = generate("Hello, this is a test. Please respond with 'Test successful'.");
            return testResponse != null && !testResponse.contains("Error:");
        } catch (Exception e) {
            log.error("Bedrock connection test failed", e);
            return false;
        }
    }
    
    /**
     * Get available models for the current region
     */
    public List<String> getAvailableModels() {
        List<String> models = new ArrayList<>();
        // Add some common models
        models.add("anthropic.claude-3-5-sonnet-20241022-v2:0");
        models.add("anthropic.claude-3-haiku-20240307-v1:0");
        models.add("anthropic.claude-3-opus-20240229-v1:0");
        models.add("amazon.titan-text-express-v1");
        models.add("amazon.titan-text-lite-v1");
        return models;
    }
    
    /**
     * Validate and truncate prompt to ensure it meets Bedrock requirements
     */
    private String validateAndTruncatePrompt(String prompt) {
        if (prompt == null) {
            return "";
        }
        
        // Bedrock has input limits - typically around 100,000 characters for Titan
        // We'll be conservative and limit to 50,000 characters to leave room for response
        final int MAX_PROMPT_LENGTH = 50000;
        
        if (prompt.length() <= MAX_PROMPT_LENGTH) {
            return prompt;
        }
        
        log.warn("Prompt too long ({} chars), truncating to {} chars", prompt.length(), MAX_PROMPT_LENGTH);
        
        // Truncate and add indication that content was truncated
        String truncated = prompt.substring(0, MAX_PROMPT_LENGTH - 100);
        return truncated + "\n\n[Note: Content truncated due to length limits]";
    }
    
    /**
     * Close the Bedrock client
     */
    public void close() {
        if (bedrockClient != null) {
            bedrockClient.close();
        }
    }
}
