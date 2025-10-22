package com.interviewprep.service;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for speech-to-text using OpenAI Whisper (local, free)
 * Requires whisper to be installed: pip install openai-whisper
 */
@Slf4j
public class WhisperService {
    private final String model;
    private final String language;
    
    public WhisperService(String model, String language) {
        this.model = model;
        this.language = language;
    }
    
    /**
     * Transcribe audio file to text
     * @param audioFile Path to audio file (wav, mp3, m4a, etc.)
     * @return Transcribed text
     */
    public String transcribe(File audioFile) throws IOException, InterruptedException {
        if (!audioFile.exists()) {
            throw new FileNotFoundException("Audio file not found: " + audioFile);
        }
        
        // Build whisper command
        List<String> command = new ArrayList<>();
        command.add("whisper");
        command.add(audioFile.getAbsolutePath());
        command.add("--model");
        command.add(model);
        command.add("--language");
        command.add(language);
        command.add("--output_format");
        command.add("txt");
        command.add("--output_dir");
        command.add(audioFile.getParent());
        
        log.info("Running Whisper transcription: {}", String.join(" ", command));
        
        // Execute whisper
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        
        // Read output
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
        );
        
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
            log.debug("Whisper: {}", line);
        }
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Whisper failed with exit code " + exitCode + ": " + output);
        }
        
        // Read transcription file
        String baseName = audioFile.getName().replaceFirst("[.][^.]+$", "");
        File transcriptFile = new File(audioFile.getParent(), baseName + ".txt");
        
        if (transcriptFile.exists()) {
            String transcript = Files.readString(transcriptFile.toPath());
            log.info("Transcription completed: {} characters", transcript.length());
            return transcript.trim();
        } else {
            throw new IOException("Transcript file not generated: " + transcriptFile);
        }
    }
    
    /**
     * Transcribe with real-time progress callback
     */
    public String transcribeWithProgress(File audioFile, ProgressCallback callback) 
            throws IOException, InterruptedException {
        
        List<String> command = new ArrayList<>();
        command.add("whisper");
        command.add(audioFile.getAbsolutePath());
        command.add("--model");
        command.add(model);
        command.add("--language");
        command.add(language);
        command.add("--output_format");
        command.add("txt");
        command.add("--output_dir");
        command.add(audioFile.getParent());
        command.add("--verbose");
        command.add("False");
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
        );
        
        String line;
        while ((line = reader.readLine()) != null) {
            if (callback != null) {
                callback.onProgress(line);
            }
        }
        
        process.waitFor();
        
        String baseName = audioFile.getName().replaceFirst("[.][^.]+$", "");
        File transcriptFile = new File(audioFile.getParent(), baseName + ".txt");
        
        return transcriptFile.exists() ? Files.readString(transcriptFile.toPath()).trim() : "";
    }
    
    /**
     * Check if Whisper is installed and available
     */
    public boolean isAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("whisper", "--help");
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            log.warn("Whisper not available: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get supported audio formats
     */
    public static List<String> getSupportedFormats() {
        return List.of("wav", "mp3", "m4a", "flac", "ogg", "opus");
    }
    
    @FunctionalInterface
    public interface ProgressCallback {
        void onProgress(String message);
    }
}

