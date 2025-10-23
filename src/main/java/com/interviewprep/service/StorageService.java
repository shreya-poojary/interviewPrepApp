package com.interviewprep.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.interviewprep.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * File-based storage service using JSON
 * No database required - all data stored as JSON files
 */
@Slf4j
public class StorageService {
    private final Gson gson;
    private final String dataDirectory;
    
    public StorageService(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        initializeDirectories();
    }
    
    private void initializeDirectories() {
        try {
            Files.createDirectories(Paths.get(dataDirectory));
            Files.createDirectories(Paths.get(dataDirectory, "sessions"));
            Files.createDirectories(Paths.get(dataDirectory, "analytics"));
            Files.createDirectories(Paths.get(dataDirectory, "mcp"));
            log.info("Storage directories initialized: {}", dataDirectory);
        } catch (IOException e) {
            log.error("Failed to create storage directories", e);
        }
    }
    
    /**
     * Save interview session
     */
    public void saveSession(InterviewSession session) {
        try {
            String fileName = session.getSessionId() + ".json";
            Path filePath = Paths.get(dataDirectory, "sessions", fileName);
            String json = gson.toJson(session);
            Files.writeString(filePath, json);
            log.info("Session saved: {}", session.getSessionId());
        } catch (IOException e) {
            log.error("Error saving session", e);
        }
    }
    
    /**
     * Load interview session
     */
    public InterviewSession loadSession(String sessionId) {
        try {
            Path filePath = Paths.get(dataDirectory, "sessions", sessionId + ".json");
            if (Files.exists(filePath)) {
                String json = Files.readString(filePath);
                return gson.fromJson(json, InterviewSession.class);
            }
        } catch (IOException e) {
            log.error("Error loading session: {}", sessionId, e);
        }
        return null;
    }
    
    /**
     * Get recent sessions
     */
    public List<InterviewSession> getRecentSessions(int limit) {
        try {
            Path sessionsDir = Paths.get(dataDirectory, "sessions");
            if (!Files.exists(sessionsDir)) return new ArrayList<>();
            
            return Files.list(sessionsDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .sorted(Comparator.comparing((Path path) -> {
                        try {
                            return Files.getLastModifiedTime(path);
                        } catch (IOException e) {
                            return FileTime.fromMillis(0);
                        }
                    }).reversed())
                    .limit(limit)
                    .map(path -> {
                        try {
                            String json = Files.readString(path);
                            return gson.fromJson(json, InterviewSession.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error loading recent sessions", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Save analytics
     */
    public void saveAnalytics(InterviewAnalytics analytics) {
        try {
            String fileName = analytics.getSessionId() + "_analytics.json";
            Path filePath = Paths.get(dataDirectory, "analytics", fileName);
            String json = gson.toJson(analytics);
            Files.writeString(filePath, json);
            log.info("Analytics saved: {}", analytics.getSessionId());
        } catch (IOException e) {
            log.error("Error saving analytics", e);
        }
    }
    
    /**
     * Load analytics
     */
    public InterviewAnalytics loadAnalytics(String sessionId) {
        try {
            Path filePath = Paths.get(dataDirectory, "analytics", sessionId + "_analytics.json");
            if (Files.exists(filePath)) {
                String json = Files.readString(filePath);
                return gson.fromJson(json, InterviewAnalytics.class);
            }
        } catch (IOException e) {
            log.error("Error loading analytics: {}", sessionId, e);
        }
        return null;
    }
    
    /**
     * Get all analytics
     */
    public List<InterviewAnalytics> getAllAnalytics() {
        try {
            Path analyticsDir = Paths.get(dataDirectory, "analytics");
            if (!Files.exists(analyticsDir)) return new ArrayList<>();
            
            return Files.list(analyticsDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> {
                        try {
                            String json = Files.readString(path);
                            return gson.fromJson(json, InterviewAnalytics.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(InterviewAnalytics::getSessionDate).reversed())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error loading all analytics", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Save MCP context
     */
    public void saveMCPContext(MCPContext context) {
        try {
            String fileName = context.getUserId() + "_mcp.json";
            Path filePath = Paths.get(dataDirectory, "mcp", fileName);
            String json = gson.toJson(context);
            Files.writeString(filePath, json);
            log.info("MCP context saved: {}", context.getUserId());
        } catch (IOException e) {
            log.error("Error saving MCP context", e);
        }
    }
    
    /**
     * Load MCP context
     */
    public MCPContext loadMCPContext(String userId) {
        try {
            Path filePath = Paths.get(dataDirectory, "mcp", userId + "_mcp.json");
            if (Files.exists(filePath)) {
                String json = Files.readString(filePath);
                return gson.fromJson(json, MCPContext.class);
            }
        } catch (IOException e) {
            log.error("Error loading MCP context: {}", userId, e);
        }
        return new MCPContext(userId);
    }
    
    /**
     * Delete old recordings (cleanup)
     */
    public void cleanupOldRecordings(int daysToKeep) {
        try {
            Path recordingsDir = Paths.get("recordings");
            if (!Files.exists(recordingsDir)) return;
            
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            
            Files.list(recordingsDir)
                    .filter(Files::isDirectory)
                    .filter(dir -> {
                        try {
                            FileTime fileTime = Files.getLastModifiedTime(dir);
                            LocalDateTime modifiedDate = LocalDateTime.ofInstant(
                                fileTime.toInstant(), 
                                java.time.ZoneId.systemDefault()
                            );
                            return modifiedDate.isBefore(cutoffDate);
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(dir -> {
                        try {
                            deleteDirectory(dir);
                            log.info("Deleted old recording: {}", dir.getFileName());
                        } catch (IOException e) {
                            log.warn("Failed to delete directory: {}", dir, e);
                        }
                    });
        } catch (IOException e) {
            log.error("Error cleaning up recordings", e);
        }
    }
    
    private void deleteDirectory(Path directory) throws IOException {
        Files.walk(directory)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.warn("Failed to delete: {}", path, e);
                    }
                });
    }
    
    /**
     * LocalDateTime adapter for Gson
     */
    private static class LocalDateTimeAdapter implements com.google.gson.JsonSerializer<LocalDateTime>,
            com.google.gson.JsonDeserializer<LocalDateTime> {
        
        @Override
        public com.google.gson.JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc,
                                                      com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(src.toString());
        }
        
        @Override
        public LocalDateTime deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT,
                                        com.google.gson.JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString());
        }
    }
}

