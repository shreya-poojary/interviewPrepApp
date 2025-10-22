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
public class OllamaService {
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
    public String generate(String prompt) throws IOException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("prompt", prompt);
        requestBody.addProperty("stream", false);
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.parse("application/json")
        );
        
        Request request = new Request.Builder()
                .url(baseUrl + "/api/generate")
                .post(body)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Ollama request failed: " + response.code());
            }
            
            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            return jsonResponse.get("response").getAsString();
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
     * Check if Ollama is available
     */
    public boolean isAvailable() {
        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/tags")
                    .get()
                    .build();
            
            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            log.warn("Ollama not available: {}", e.getMessage());
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
    
    private static class Message {
        String role;
        String content;
        
        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}

