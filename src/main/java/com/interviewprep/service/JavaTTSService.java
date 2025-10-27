package com.interviewprep.service;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Java-only Text-to-Speech service
 * Uses system commands (no Python required)
 */
@Slf4j
public class JavaTTSService {
    private final boolean enabled;
    private Process currentProcess;
    private Thread currentThread;
    
    public JavaTTSService(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Speak text using system TTS
     */
    public void speak(String text) {
        if (!enabled) return;
        
        try {
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("win")) {
                // Windows: Use PowerShell with built-in TTS
                speakWindows(text);
            } else if (os.contains("mac")) {
                // macOS: Use 'say' command
                speakMac(text);
            } else {
                // Linux: Use espeak if available
                speakLinux(text);
            }
        } catch (Exception e) {
            log.warn("TTS not available: {}", e.getMessage());
        }
    }
    
    /**
     * Speak text asynchronously (non-blocking)
     */
    public void speakAsync(String text) {
        // Stop any current speech
        stopSpeaking();
        
        currentThread = new Thread(() -> speak(text));
        currentThread.start();
    }
    
    /**
     * Stop current speech
     */
    public void stopSpeaking() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
            try {
                currentProcess.waitFor(1, java.util.concurrent.TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        if (currentThread != null && currentThread.isAlive()) {
            currentThread.interrupt();
        }
        
        currentProcess = null;
        currentThread = null;
    }
    
    private void speakWindows(String text) throws IOException {
        // Escape single quotes
        String escapedText = text.replace("'", "''");
        
        String psCommand = String.format(
            "Add-Type -AssemblyName System.Speech; " +
            "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
            "$speak.Speak('%s')",
            escapedText
        );
        
        ProcessBuilder pb = new ProcessBuilder(
            "powershell.exe",
            "-Command",
            psCommand
        );
        
        currentProcess = pb.start();
        
        // Wait for completion (with timeout)
        try {
            currentProcess.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            currentProcess.destroy();
            Thread.currentThread().interrupt();
        }
    }
    
    private void speakMac(String text) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("say", text);
        currentProcess = pb.start();
        
        try {
            currentProcess.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            currentProcess.destroy();
            Thread.currentThread().interrupt();
        }
    }
    
    private void speakLinux(String text) throws IOException {
        // Try espeak first
        try {
            ProcessBuilder pb = new ProcessBuilder("espeak", text);
            currentProcess = pb.start();
            currentProcess.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            // If espeak not available, try spd-say
            try {
                ProcessBuilder pb = new ProcessBuilder("spd-say", text);
                currentProcess = pb.start();
                currentProcess.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
            } catch (Exception ex) {
                log.warn("No Linux TTS available. Install espeak or speech-dispatcher");
            }
        }
    }
    
    /**
     * Check if TTS is available on this system
     */
    public boolean isAvailable() {
        if (!enabled) return false;
        
        try {
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("win")) {
                // Windows always has PowerShell TTS
                return true;
            } else if (os.contains("mac")) {
                // macOS always has 'say' command
                return true;
            } else {
                // Check if espeak is installed on Linux
                Process process = new ProcessBuilder("which", "espeak").start();
                return process.waitFor() == 0;
            }
        } catch (Exception e) {
            return false;
        }
    }
}

