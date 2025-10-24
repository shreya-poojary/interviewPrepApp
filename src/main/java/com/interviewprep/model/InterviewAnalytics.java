package com.interviewprep.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Comprehensive analytics for an interview session
 */
@Data
public class InterviewAnalytics {
    private String sessionId;
    private LocalDateTime sessionDate;
    private InterviewMode mode;
    
    // Overall scores (0-10)
    private double overallScore;
    private double technicalScore;
    private double behavioralScore;
    private double communicationScore;
    private double confidenceScore;
    
    // Detailed metrics
    private int totalQuestions;
    private int questionsAnswered;
    private long totalDurationSeconds;
    private double averageAnswerLength; // seconds
    
    // Voice analytics
    private int fillerWordCount;
    private double wordsPerMinute;
    private double volumeConsistency; // 0-1
    private double paceConsistency; // 0-1
    
    // Content quality
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> improvementSuggestions;
    private Map<String, Double> categoryScores;
    
    // Additional fields for AI analytics
    private LocalDateTime generatedAt;
    private String performanceLevel;
    private String detailedFeedback;
    
    // Question-level details
    private List<QuestionAnalysis> questionAnalyses;
    
    public InterviewAnalytics() {
        this.strengths = new ArrayList<>();
        this.weaknesses = new ArrayList<>();
        this.improvementSuggestions = new ArrayList<>();
        this.categoryScores = new HashMap<>();
        this.questionAnalyses = new ArrayList<>();
    }
    
    public double calculateImprovement(InterviewAnalytics previous) {
        if (previous == null) return 0;
        return this.overallScore - previous.overallScore;
    }
    
    public String getPerformanceLevel() {
        if (overallScore >= 9.0) return "Excellent ⭐⭐⭐⭐⭐";
        if (overallScore >= 8.0) return "Very Good ⭐⭐⭐⭐";
        if (overallScore >= 7.0) return "Good ⭐⭐⭐";
        if (overallScore >= 6.0) return "Fair ⭐⭐";
        return "Needs Improvement ⭐";
    }
    
    public int getCompletionRate() {
        if (totalQuestions == 0) return 0;
        return (int) ((questionsAnswered / (double) totalQuestions) * 100);
    }
}

