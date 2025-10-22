package com.interviewprep.model;

import lombok.Data;

/**
 * Represents an interview question
 */
@Data
public class InterviewQuestion {
    private String id;
    private String question;
    private String category; // Technical, Behavioral, Situational
    private String difficulty; // Easy, Medium, Hard
    private String suggestedAnswer;
    private String feedback;
    private int timeLimit; // seconds
    
    public InterviewQuestion() {
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    public InterviewQuestion(String question, String category, String difficulty) {
        this();
        this.question = question;
        this.category = category;
        this.difficulty = difficulty;
    }
}

