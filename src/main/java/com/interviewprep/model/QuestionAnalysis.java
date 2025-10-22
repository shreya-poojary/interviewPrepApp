package com.interviewprep.model;

import lombok.Data;

/**
 * Detailed analysis of a single question-answer pair
 */
@Data
public class QuestionAnalysis {
    private InterviewQuestion question;
    private String userAnswer;
    private double score; // 0-10
    private long answerDurationSeconds;
    private int wordCount;
    private int fillerWords;
    private String feedback;
    private String suggestedImprovement;
    private boolean usedSTARMethod; // For behavioral questions
    
    public QuestionAnalysis() {}
    
    public QuestionAnalysis(InterviewQuestion question, String userAnswer) {
        this.question = question;
        this.userAnswer = userAnswer;
    }
    
    public double getFillerWordRate() {
        if (wordCount == 0) return 0;
        return (fillerWords / (double) wordCount) * 100;
    }
    
    public double getWordsPerMinute() {
        if (answerDurationSeconds == 0) return 0;
        return (wordCount / (answerDurationSeconds / 60.0));
    }
}

