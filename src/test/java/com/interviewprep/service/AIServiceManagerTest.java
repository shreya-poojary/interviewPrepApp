package com.interviewprep.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceManagerTest {

    @Mock
    private OllamaService mockOllamaService;

    @Mock
    private BedrockService mockBedrockService;

    private AIServiceManager aiServiceManager;

    @BeforeEach
    void setUp() {
        aiServiceManager = new AIServiceManager();
        // Use reflection to inject mock services for testing
        try {
            var ollamaField = AIServiceManager.class.getDeclaredField("ollamaService");
            ollamaField.setAccessible(true);
            ollamaField.set(aiServiceManager, mockOllamaService);

            var bedrockField = AIServiceManager.class.getDeclaredField("bedrockService");
            bedrockField.setAccessible(true);
            bedrockField.set(aiServiceManager, mockBedrockService);
        } catch (Exception e) {
            fail("Failed to inject mock services: " + e.getMessage());
        }
    }

    @Test
    void testSetPreferredService_Ollama() {
        // Arrange
        when(mockOllamaService.isAvailable()).thenReturn(true);
        when(mockBedrockService.isAvailable()).thenReturn(false);

        // Act
        aiServiceManager.setPreferredService("ollama");

        // Assert
        assertEquals("Ollama", aiServiceManager.getCurrentServiceName());
    }

    @Test
    void testSetPreferredService_Bedrock() {
        // Arrange
        when(mockOllamaService.isAvailable()).thenReturn(false);
        when(mockBedrockService.isAvailable()).thenReturn(true);

        // Act
        aiServiceManager.setPreferredService("bedrock");

        // Assert
        assertEquals("Bedrock", aiServiceManager.getCurrentServiceName());
    }

    @Test
    void testSetPreferredService_FallbackToAvailable() {
        // Arrange
        when(mockOllamaService.isAvailable()).thenReturn(false);
        when(mockBedrockService.isAvailable()).thenReturn(true);

        // Act
        aiServiceManager.setPreferredService("ollama"); // Requested Ollama but it's not available

        // Assert
        assertEquals("Bedrock", aiServiceManager.getCurrentServiceName());
    }

    @Test
    void testGenerate_WithOllama() throws IOException {
        // Arrange
        String prompt = "Test prompt";
        String expectedResponse = "Test response from Ollama";
        
        when(mockOllamaService.isAvailable()).thenReturn(true);
        when(mockBedrockService.isAvailable()).thenReturn(false);
        when(mockOllamaService.generate(prompt)).thenReturn(expectedResponse);

        aiServiceManager.setPreferredService("ollama");

        // Act
        String result = aiServiceManager.generate(prompt);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockOllamaService).generate(prompt);
        verify(mockBedrockService, never()).generate(anyString());
    }

    @Test
    void testGenerate_WithBedrock() throws IOException {
        // Arrange
        String prompt = "Test prompt";
        String expectedResponse = "Test response from Bedrock";
        
        when(mockOllamaService.isAvailable()).thenReturn(false);
        when(mockBedrockService.isAvailable()).thenReturn(true);
        when(mockBedrockService.generate(prompt)).thenReturn(expectedResponse);

        aiServiceManager.setPreferredService("bedrock");

        // Act
        String result = aiServiceManager.generate(prompt);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockBedrockService).generate(prompt);
        verify(mockOllamaService, never()).generate(anyString());
    }

    @Test
    void testChat_WithOllama() throws IOException {
        // Arrange
        String userMessage = "Hello";
        String expectedResponse = "Hello! How can I help you?";
        
        when(mockOllamaService.isAvailable()).thenReturn(true);
        when(mockBedrockService.isAvailable()).thenReturn(false);
        when(mockOllamaService.chat(userMessage)).thenReturn(expectedResponse);

        aiServiceManager.setPreferredService("ollama");

        // Act
        String result = aiServiceManager.chat(userMessage);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockOllamaService).chat(userMessage);
    }

    @Test
    void testGenerateWithSystem_WithBedrock() throws IOException {
        // Arrange
        String systemPrompt = "You are a helpful assistant";
        String userPrompt = "What is 2+2?";
        String expectedResponse = "2+2 equals 4";
        
        when(mockOllamaService.isAvailable()).thenReturn(false);
        when(mockBedrockService.isAvailable()).thenReturn(true);
        when(mockBedrockService.generateWithSystem(systemPrompt, userPrompt)).thenReturn(expectedResponse);

        aiServiceManager.setPreferredService("bedrock");

        // Act
        String result = aiServiceManager.generateWithSystem(systemPrompt, userPrompt);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockBedrockService).generateWithSystem(systemPrompt, userPrompt);
    }

    @Test
    void testClearHistory() {
        // Act
        aiServiceManager.clearHistory();

        // Assert
        verify(mockOllamaService).clearHistory();
        verify(mockBedrockService).clearHistory();
    }

    @Test
    void testIsAvailable_WithAvailableService() {
        // Arrange
        when(mockOllamaService.isAvailable()).thenReturn(true);
        when(mockBedrockService.isAvailable()).thenReturn(false);

        aiServiceManager.setPreferredService("ollama");

        // Act
        boolean result = aiServiceManager.isAvailable();

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsAvailable_WithNoAvailableService() {
        // Arrange
        when(mockOllamaService.isAvailable()).thenReturn(false);
        when(mockBedrockService.isAvailable()).thenReturn(false);

        // Act
        boolean result = aiServiceManager.isAvailable();

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetServiceName() {
        // Arrange
        when(mockOllamaService.isAvailable()).thenReturn(true);
        when(mockBedrockService.isAvailable()).thenReturn(false);

        aiServiceManager.setPreferredService("ollama");

        // Act
        String result = aiServiceManager.getServiceName();

        // Assert
        assertEquals("Ollama", result);
    }

    @Test
    void testTestConnection() {
        // Arrange
        when(mockOllamaService.isAvailable()).thenReturn(true);
        when(mockBedrockService.isAvailable()).thenReturn(false);

        aiServiceManager.setPreferredService("ollama");

        // Act
        boolean result = aiServiceManager.testConnection();

        // Assert
        assertTrue(result);
        verify(mockOllamaService).testConnection();
    }

    @Test
    void testGetAvailableModels() throws IOException {
        // Arrange
        when(mockOllamaService.isAvailable()).thenReturn(true);
        when(mockBedrockService.isAvailable()).thenReturn(false);
        when(mockOllamaService.getAvailableModels()).thenReturn(List.of("llama3.2:latest"));

        aiServiceManager.setPreferredService("ollama");

        // Act
        List<String> models = aiServiceManager.getAvailableModels();

        // Assert
        assertNotNull(models);
        assertTrue(models.contains("llama3.2:latest"));
        verify(mockOllamaService).getAvailableModels();
    }

    @Test
    void testTestAllServices() {
        // Arrange
        when(mockOllamaService.isAvailable()).thenReturn(true);
        when(mockBedrockService.isAvailable()).thenReturn(false);

        // Act
        aiServiceManager.testAllServices();

        // Assert
        verify(mockOllamaService).isAvailable();
        verify(mockBedrockService).isAvailable();
    }

    @Test
    void testGetAvailableServices() {
        // Arrange
        when(mockOllamaService.isAvailable()).thenReturn(true);
        when(mockBedrockService.isAvailable()).thenReturn(false);

        // Act
        List<String> services = aiServiceManager.getAvailableServices();

        // Assert
        assertNotNull(services);
        assertTrue(services.contains("Ollama"));
        assertFalse(services.contains("Bedrock"));
    }
}
