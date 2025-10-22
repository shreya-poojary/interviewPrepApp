package com.interviewprep.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Represents a user's resume
 */
@Data
public class Resume {
    private String fileName;
    private String content;
    private LocalDateTime uploadedAt;
    private String extractedSkills;
    private String extractedExperience;
    
    public Resume() {
        this.uploadedAt = LocalDateTime.now();
    }
    
    public Resume(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
        this.uploadedAt = LocalDateTime.now();
    }
}

