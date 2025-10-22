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
        new Thread(() -> speak(text)).start();
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
        
        Process process = pb.start();
        
        // Wait for completion (with timeout)
        try {
            process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            process.destroy();
        }
    }
    
    private void speakMac(String text) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("say", text);
        Process process = pb.start();
        
        try {
            process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            process.destroy();
        }
    }
    
    private void speakLinux(String text) throws IOException {
        // Try espeak first
        try {
            ProcessBuilder pb = new ProcessBuilder("espeak", text);
            Process process = pb.start();
            process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            // If espeak not available, try spd-say
            try {
                ProcessBuilder pb = new ProcessBuilder("spd-say", text);
                Process process = pb.start();
                process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
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

