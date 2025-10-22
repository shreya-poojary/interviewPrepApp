package com.interviewprep.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Represents a job description for interview preparation
 */
@Data
public class JobDescription {
    private String fileName;
    private String content;
    private String jobTitle;
    private String company;
    private String requiredSkills;
    private LocalDateTime uploadedAt;
    
    public JobDescription() {
        this.uploadedAt = LocalDateTime.now();
    }
    
    public JobDescription(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
        this.uploadedAt = LocalDateTime.now();
    }
}

