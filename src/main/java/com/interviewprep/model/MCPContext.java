package com.interviewprep.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Model Context Protocol - Maintains conversation context across sessions
 * Enables adaptive difficulty and personalized interview experiences
 */
@Data
public class MCPContext {
    private String userId;
    private List<InterviewAnalytics> historicalSessions;
    private Map<String, Double> skillLevels; // skill -> score (0-10)
    private List<String> focusAreas; // Areas needing improvement
    private Map<String, Integer> questionHistory; // questionId -> times asked
    private int currentDifficultyLevel; // 1-5
    private LocalDateTime lastSessionDate;
    
    public MCPContext() {
        this.historicalSessions = new ArrayList<>();
        this.skillLevels = new HashMap<>();
        this.focusAreas = new ArrayList<>();
        this.questionHistory = new HashMap<>();
        this.currentDifficultyLevel = 3; // Medium
    }
    
    public MCPContext(String userId) {
        this();
        this.userId = userId;
    }
    
    /**
     * Adapts difficulty based on recent performance
     */
    public void adaptDifficulty() {
        if (historicalSessions.isEmpty()) return;
        
        // Get last 3 sessions
        List<InterviewAnalytics> recent = historicalSessions.stream()
            .sorted(Comparator.comparing(InterviewAnalytics::getSessionDate).reversed())
            .limit(3)
            .toList();
        
        double avgScore = recent.stream()
            .mapToDouble(InterviewAnalytics::getOverallScore)
            .average()
            .orElse(5.0);
        
        if (avgScore >= 8.5) {
            currentDifficultyLevel = Math.min(5, currentDifficultyLevel + 1);
        } else if (avgScore < 6.0) {
            currentDifficultyLevel = Math.max(1, currentDifficultyLevel - 1);
        }
    }
    
    /**
     * Identifies top areas needing improvement
     */
    public List<String> getTopImprovementAreas() {
        Map<String, Double> avgScores = new HashMap<>();
        
        for (InterviewAnalytics session : historicalSessions) {
            session.getCategoryScores().forEach((category, score) -> {
                avgScores.merge(category, score, (a, b) -> (a + b) / 2);
            });
        }
        
        return avgScores.entrySet().stream()
            .filter(e -> e.getValue() < 7.0)
            .sorted(Map.Entry.comparingByValue())
            .limit(3)
            .map(Map.Entry::getKey)
            .toList();
    }
    
    /**
     * Updates context after a session
     */
    public void updateAfterSession(InterviewAnalytics analytics) {
        historicalSessions.add(analytics);
        lastSessionDate = analytics.getSessionDate();
        
        // Update skill levels
        analytics.getCategoryScores().forEach((category, score) -> {
            skillLevels.merge(category, score, (old, newScore) -> (old + newScore) / 2);
        });
        
        // Adapt difficulty
        adaptDifficulty();
        
        // Update focus areas
        focusAreas = getTopImprovementAreas();
    }
}

