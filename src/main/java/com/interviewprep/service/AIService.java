package com.interviewprep.service;

/**
 * Interface for AI services to enable plug-and-play between different providers
 */
public interface AIService {
    
    /**
     * Generate a response from the AI service
     * @param prompt The input prompt
     * @return The AI response
     */
    String generate(String prompt);
    
    /**
     * Check if the AI service is available
     * @return true if available, false otherwise
     */
    boolean isAvailable();
    
    /**
     * Get the name of the AI service
     * @return Service name
     */
    String getServiceName();
    
    /**
     * Test the AI service connection
     * @return true if test successful, false otherwise
     */
    boolean testConnection();
}
