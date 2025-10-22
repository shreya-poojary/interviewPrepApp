package com.interviewprep.model;

import lombok.Getter;

/**
 * Different interview modes with varying difficulty and time constraints
 */
@Getter
public enum InterviewMode {
    PRACTICE(
        "Practice Mode",
        "📚",
        "Unlimited time, hints available, relaxed environment. Perfect for beginners.",
        0,
        "Easy"
    ),
    
    TIMED(
        "Timed Mode",
        "⏱️",
        "Strict time limits per question. Simulates real interview pressure.",
        120,
        "Medium"
    ),
    
    SURPRISE(
        "Surprise Mode",
        "🎲",
        "Random questions without preview. Tests your adaptability.",
        90,
        "Medium"
    ),
    
    FAANG(
        "FAANG Mode",
        "🏢",
        "Tech giant style: algorithms, system design, and behavioral questions.",
        180,
        "Hard"
    ),
    
    STARTUP(
        "Startup Mode",
        "🚀",
        "Fast-paced, culture fit, and versatile skills assessment.",
        90,
        "Medium"
    ),
    
    BEHAVIORAL(
        "Behavioral Mode",
        "💬",
        "STAR method focus, leadership principles, and soft skills.",
        150,
        "Easy"
    );
    
    private final String displayName;
    private final String icon;
    private final String description;
    private final int secondsPerQuestion;
    private final String difficulty;
    
    InterviewMode(String displayName, String icon, String description, 
                  int secondsPerQuestion, String difficulty) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
        this.secondsPerQuestion = secondsPerQuestion;
        this.difficulty = difficulty;
    }
    
    public boolean isTimeLimited() {
        return secondsPerQuestion > 0;
    }
    
    public String getFormattedTimeLimit() {
        if (secondsPerQuestion == 0) {
            return "No time limit";
        }
        int minutes = secondsPerQuestion / 60;
        return minutes + " min per question";
    }
}

