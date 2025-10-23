package com.interviewprep.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for interacting with Ollama (local LLM)
 * Free and runs entirely on your machine
 */
@Slf4j
public class OllamaService implements AIService {
    private final OkHttpClient client;
    private final Gson gson;
    private final String baseUrl;
    private final String model;
    private List<Message> conversationHistory;
    
    public OllamaService(String baseUrl, String model) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.baseUrl = baseUrl;
        this.model = model;
        this.conversationHistory = new ArrayList<>();
    }
    
    /**
     * Generate a response from Ollama
     */
    public String generate(String prompt) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", model);
            requestBody.addProperty("stream", false);
            
            // Create messages array for chat API
            com.google.gson.JsonArray messages = new com.google.gson.JsonArray();
            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);
            messages.add(message);
            requestBody.add("messages", messages);
            
            RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
            );
            
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/chat")
                    .post(body)
                    .build();
            
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Ollama API Error: {} - {}", response.code(), response.message());
                    String errorBody = response.body() != null ? response.body().string() : "No error details";
                    log.error("Error response: {}", errorBody);
                    throw new IOException("Ollama request failed: " + response.code() + " - " + errorBody);
                }
                
                String responseBody = response.body().string();
                log.debug("Ollama response: {}", responseBody);
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                
                // For chat API, the response is in message.content
                if (jsonResponse.has("message")) {
                    JsonObject messageObj = jsonResponse.getAsJsonObject("message");
                    return messageObj.get("content").getAsString();
                } else {
                    // Fallback to old format
                    return jsonResponse.get("response").getAsString();
                }
            }
        } catch (IOException e) {
            log.error("Error calling Ollama API", e);
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Chat with conversation context (MCP-style)
     */
    public String chat(String userMessage) throws IOException {
        conversationHistory.add(new Message("user", userMessage));
        
        // Build context-aware prompt
        StringBuilder contextPrompt = new StringBuilder();
        for (Message msg : conversationHistory) {
            contextPrompt.append(msg.role).append(": ").append(msg.content).append("\n");
        }
        contextPrompt.append("assistant: ");
        
        String response = generate(contextPrompt.toString());
        conversationHistory.add(new Message("assistant", response));
        
        return response;
    }
    
    /**
     * Generate with system prompt (for specialized tasks)
     */
    public String generateWithSystem(String systemPrompt, String userPrompt) throws IOException {
        String fullPrompt = String.format(
            "System: %s\n\nUser: %s\n\nAssistant: ",
            systemPrompt,
            userPrompt
        );
        return generate(fullPrompt);
    }
    
    /**
     * Clear conversation history
     */
    public void clearHistory() {
        conversationHistory.clear();
    }
    
    
    /**
     * Test the model with a simple prompt
     */
    public boolean testModel() {
        try {
            String testResponse = generate("Hello");
            log.info("Model test successful: {}", testResponse.substring(0, Math.min(50, testResponse.length())));
            return true;
        } catch (Exception e) {
            log.error("Model test failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get list of available models
     */
    public List<String> getAvailableModels() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/tags")
                .get()
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            
            List<String> models = new ArrayList<>();
            jsonResponse.getAsJsonArray("models").forEach(element -> {
                models.add(element.getAsJsonObject().get("name").getAsString());
            });
            return models;
        }
    }
    
    @Override
    public boolean isAvailable() {
        try {
            return testConnection();
        } catch (Exception e) {
            log.warn("Ollama service not available: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getServiceName() {
        return "Ollama (" + model + ")";
    }
    
    @Override
    public boolean testConnection() {
        try {
            // Test with a simple prompt
            String testResponse = generate("Hello, this is a test. Please respond with 'Test successful'.");
            return testResponse != null && !testResponse.contains("Error:");
        } catch (Exception e) {
            log.error("Ollama connection test failed", e);
            return false;
        }
    }
    
    private static class Message {
        String role;
        String content;
        
        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}

