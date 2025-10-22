package com.interviewprep.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a recorded interview session with video, audio, and transcript
 */
@Data
public class InterviewRecording {
    private String recordingId;
    private String videoFilePath;
    private String audioFilePath;
    private String transcriptFilePath;
    private LocalDateTime recordingDate;
    private long durationSeconds;
    private InterviewSession session;
    
    public InterviewRecording() {
        this.recordingId = UUID.randomUUID().toString();
        this.recordingDate = LocalDateTime.now();
    }
    
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return recordingDate.format(formatter);
    }
    
    public String getRecordingDirectory() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "recordings/" + recordingDate.format(formatter) + "_" + recordingId.substring(0, 8);
    }
    
    public String getFormattedDuration() {
        long hours = durationSeconds / 3600;
        long minutes = (durationSeconds % 3600) / 60;
        long seconds = durationSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}

