package com.interviewprep.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Analysis results for a resume against a job description
 */
@Data
public class ResumeAnalysis {
    private String overallFeedback;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> suggestions;
    private int matchScore; // 0-100
    private List<String> matchingSkills;
    private List<String> missingSkills;
    
    public ResumeAnalysis() {
        this.strengths = new ArrayList<>();
        this.weaknesses = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.matchingSkills = new ArrayList<>();
        this.missingSkills = new ArrayList<>();
    }
    
    public String getMatchLevel() {
        if (matchScore >= 90) return "Excellent Match";
        if (matchScore >= 75) return "Strong Match";
        if (matchScore >= 60) return "Good Match";
        if (matchScore >= 40) return "Fair Match";
        return "Weak Match";
    }
}

