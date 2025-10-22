package com.interviewprep.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a complete interview session
 */
@Data
public class InterviewSession {
    private String sessionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private InterviewMode mode;
    private List<InterviewQuestion> questions;
    private Map<String, String> userAnswers; // questionId -> answer
    private Map<String, String> feedback; // questionId -> feedback
    private Map<String, Long> answerDurations; // questionId -> seconds
    private String overallFeedback;
    
    public InterviewSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.startTime = LocalDateTime.now();
        this.questions = new ArrayList<>();
        this.userAnswers = new HashMap<>();
        this.feedback = new HashMap<>();
        this.answerDurations = new HashMap<>();
    }
    
    public void addQuestionAnswer(InterviewQuestion question, String answer, long durationSeconds) {
        userAnswers.put(question.getId(), answer);
        answerDurations.put(question.getId(), durationSeconds);
    }
    
    public void addFeedback(String questionId, String feedbackText) {
        feedback.put(questionId, feedbackText);
    }
    
    public void complete() {
        this.endTime = LocalDateTime.now();
    }
    
    public long getTotalDurationSeconds() {
        if (endTime == null) return 0;
        return java.time.Duration.between(startTime, endTime).getSeconds();
    }
}

