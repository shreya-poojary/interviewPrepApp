package com.interviewprep.service;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for text-to-speech using Piper (local, free, neural voices)
 * Requires piper to be installed
 */
@Slf4j
public class PiperTTSService {
    private final String model;
    
    public PiperTTSService(String model) {
        this.model = model;
    }
    
    /**
     * Convert text to speech and save to file
     * @param text Text to convert
     * @param outputFile Output audio file path
     */
    public void synthesize(String text, File outputFile) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("piper");
        command.add("--model");
        command.add(model);
        command.add("--output_file");
        command.add(outputFile.getAbsolutePath());
        
        log.info("Synthesizing speech with Piper: {} chars", text.length());
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        
        // Write text to stdin
        try (OutputStream stdin = process.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin))) {
            writer.write(text);
        }
        
        // Read output
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("Piper: {}", line);
            }
        }
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Piper TTS failed with exit code " + exitCode);
        }
        
        log.info("Speech synthesis completed: {}", outputFile);
    }
    
    /**
     * Synthesize and play immediately
     */
    public void synthesizeAndPlay(String text) throws IOException, InterruptedException {
        File tempFile = File.createTempFile("piper_", ".wav");
        tempFile.deleteOnExit();
        
        synthesize(text, tempFile);
        playAudio(tempFile);
    }
    
    /**
     * Play audio file using system default player
     */
    private void playAudio(File audioFile) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        
        ProcessBuilder pb;
        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd", "/c", "start", audioFile.getAbsolutePath());
        } else if (os.contains("mac")) {
            pb = new ProcessBuilder("afplay", audioFile.getAbsolutePath());
        } else {
            // Linux
            pb = new ProcessBuilder("aplay", audioFile.getAbsolutePath());
        }
        
        pb.start();
    }
    
    /**
     * Check if Piper is installed
     */
    public boolean isAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("piper", "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            log.warn("Piper TTS not available: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get available voice models
     */
    public static List<String> getAvailableModels() {
        return List.of(
            "en_US-lessac-medium",
            "en_US-libritts-high",
            "en_GB-alan-medium",
            "en_GB-southern_english_female-low"
        );
    }
}

