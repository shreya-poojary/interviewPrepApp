package com.interviewprep.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.core.SdkBytes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BedrockServiceTest {

    @Mock
    private BedrockRuntimeClient mockBedrockClient;

    @Mock
    private InvokeModelResponse mockResponse;

    private BedrockService bedrockService;

    @BeforeEach
    void setUp() {
        bedrockService = new BedrockService("amazon.titan-text-express-v1", "us-east-1");
        // Use reflection to inject mock client for testing
        try {
            var field = BedrockService.class.getDeclaredField("bedrockClient");
            field.setAccessible(true);
            field.set(bedrockService, mockBedrockClient);
        } catch (Exception e) {
            fail("Failed to inject mock client: " + e.getMessage());
        }
    }

    @Test
    void testGenerate_Success() {
        // Arrange
        String prompt = "Test prompt";
        String expectedResponse = "Test response";
        
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class)))
            .thenReturn(mockResponse);
        when(mockResponse.body())
            .thenReturn(SdkBytes.fromUtf8String("{\"results\":[{\"outputText\":\"" + expectedResponse + "\"}]}"));

        // Act
        String result = bedrockService.generate(prompt);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockBedrockClient).invokeModel(any(InvokeModelRequest.class));
    }

    @Test
    void testGenerate_Exception() {
        // Arrange
        String prompt = "Test prompt";
        
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class)))
            .thenThrow(new RuntimeException("Bedrock error"));

        // Act
        String result = bedrockService.generate(prompt);

        // Assert
        assertTrue(result.contains("Error calling Bedrock API"));
    }

    @Test
    void testIsAvailable_True() {
        // Arrange
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class)))
            .thenReturn(mockResponse);
        when(mockResponse.body())
            .thenReturn(SdkBytes.fromUtf8String("{\"results\":[{\"outputText\":\"Test successful\"}]}"));

        // Act
        boolean result = bedrockService.isAvailable();

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsAvailable_False() {
        // Arrange
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class)))
            .thenThrow(new RuntimeException("Connection failed"));

        // Act
        boolean result = bedrockService.isAvailable();

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetServiceName() {
        // Act
        String result = bedrockService.getServiceName();

        // Assert
        assertEquals("Bedrock", result);
    }

    @Test
    void testClearHistory() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> bedrockService.clearHistory());
    }

    @Test
    void testGetAvailableModels() {
        // Act
        List<String> models = bedrockService.getAvailableModels();

        // Assert
        assertNotNull(models);
        assertTrue(models.contains("amazon.titan-text-express-v1"));
        assertTrue(models.contains("anthropic.claude-3-haiku-20240307-v1"));
    }

    @Test
    void testChat() {
        // Arrange
        String userMessage = "Hello";
        String expectedResponse = "Hello! How can I help you?";
        
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class)))
            .thenReturn(mockResponse);
        when(mockResponse.body())
            .thenReturn(SdkBytes.fromUtf8String("{\"results\":[{\"outputText\":\"" + expectedResponse + "\"}]}"));

        // Act
        String result = bedrockService.chat(userMessage);

        // Assert
        assertEquals(expectedResponse, result);
    }

    @Test
    void testGenerateWithSystem() {
        // Arrange
        String systemPrompt = "You are a helpful assistant";
        String userPrompt = "What is 2+2?";
        String expectedResponse = "2+2 equals 4";
        
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class)))
            .thenReturn(mockResponse);
        when(mockResponse.body())
            .thenReturn(SdkBytes.fromUtf8String("{\"results\":[{\"outputText\":\"" + expectedResponse + "\"}]}"));

        // Act
        String result = bedrockService.generateWithSystem(systemPrompt, userPrompt);

        // Assert
        assertEquals(expectedResponse, result);
    }

    @Test
    void testTestConnection() {
        // Arrange
        when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class)))
            .thenReturn(mockResponse);
        when(mockResponse.body())
            .thenReturn(SdkBytes.fromUtf8String("{\"results\":[{\"outputText\":\"Test successful\"}]}"));

        // Act
        boolean result = bedrockService.testConnection();

        // Assert
        assertTrue(result);
    }
}
