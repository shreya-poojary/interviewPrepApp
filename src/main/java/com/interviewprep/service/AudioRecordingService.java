package com.interviewprep.service;

import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Service for audio recording using Java Sound API
 */
@Slf4j
public class AudioRecordingService {
    private TargetDataLine targetLine;
    private AudioInputStream audioStream;
    private String outputPath;
    private boolean isRecording = false;
    private Thread recordingThread;
    
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        16000, // Sample rate
        16,    // Sample size in bits
        1,     // Channels (mono)
        2,     // Frame size
        16000, // Frame rate
        false  // Big endian
    );
    
    /**
     * Initialize audio recording
     */
    public void initialize(String outputDirectory) throws LineUnavailableException {
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        outputPath = outputDirectory + "/audio_" + timestamp + ".wav";
        
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
        
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Audio line not supported");
        }
        
        targetLine = (TargetDataLine) AudioSystem.getLine(info);
        targetLine.open(AUDIO_FORMAT);
        
        log.info("Audio recording initialized: {}", outputPath);
    }
    
    /**
     * Start recording
     */
    public void startRecording() {
        if (isRecording) return;
        
        if (targetLine == null) {
            log.error("Audio recording not initialized. Call initialize() first.");
            return;
        }
        
        isRecording = true;
        targetLine.start();
        
        audioStream = new AudioInputStream(targetLine);
        
        recordingThread = new Thread(() -> {
            try {
                log.info("Audio recording started");
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, new File(outputPath));
            } catch (IOException e) {
                log.error("Error writing audio file", e);
            }
        });
        recordingThread.start();
    }
    
    /**
     * Stop recording
     */
    public String stopRecording() throws InterruptedException {
        if (!isRecording) return outputPath;
        
        isRecording = false;
        
        if (targetLine != null) {
            targetLine.stop();
            targetLine.close();
        }
        
        if (recordingThread != null) {
            recordingThread.join(2000);
        }
        
        log.info("Audio recording stopped: {}", outputPath);
        return outputPath;
    }
    
    /**
     * Get current audio level (for visualization)
     */
    public float getAudioLevel() {
        if (targetLine != null && targetLine.isOpen()) {
            return targetLine.getLevel();
        }
        return 0;
    }
    
    /**
     * Check if microphone is available
     */
    public boolean isMicrophoneAvailable() {
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
            return AudioSystem.isLineSupported(info);
        } catch (Exception e) {
            log.warn("Microphone not available: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean isRecording() {
        return isRecording;
    }
    
    public String getOutputPath() {
        return outputPath;
    }
    
    public boolean isInitialized() {
        return targetLine != null && outputPath != null;
    }
}

