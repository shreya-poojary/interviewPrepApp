package com.interviewprep.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;

/**
 * Service for reading documents using Apache Tika
 * Supports PDF, DOCX, TXT, and many other formats
 */
@Slf4j
public class DocumentService {
    private final Tika tika;
    
    public DocumentService() {
        this.tika = new Tika();
    }
    
    /**
     * Extract text from any supported document format
     */
    public String extractText(File file) throws IOException {
        try {
            log.info("Extracting text from: {}", file.getName());
            String text = tika.parseToString(file);
            log.info("Extracted {} characters from {}", text.length(), file.getName());
            return text;
        } catch (TikaException e) {
            throw new IOException("Failed to parse document: " + e.getMessage(), e);
        }
    }
    
    /**
     * Detect document type
     */
    public String detectFileType(File file) throws IOException {
        return tika.detect(file);
    }
    
    /**
     * Check if file is supported
     */
    public boolean isSupported(File file) {
        try {
            String mimeType = tika.detect(file);
            return mimeType.contains("text") || 
                   mimeType.contains("pdf") || 
                   mimeType.contains("word") ||
                   mimeType.contains("document");
        } catch (Exception e) {
            return false;
        }
    }
}

