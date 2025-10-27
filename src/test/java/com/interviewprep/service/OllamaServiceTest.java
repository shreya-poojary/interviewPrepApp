package com.interviewprep.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OllamaServiceTest {

    @Mock
    private OkHttpClient mockHttpClient;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockResponseBody;

    private OllamaService ollamaService;

    @BeforeEach
    void setUp() {
        ollamaService = new OllamaService("llama3.2:latest", "http://localhost:11434");
        // Use reflection to inject mock client for testing
        try {
            var field = OllamaService.class.getDeclaredField("httpClient");
            field.setAccessible(true);
            field.set(ollamaService, mockHttpClient);
        } catch (Exception e) {
            fail("Failed to inject mock client: " + e.getMessage());
        }
    }

    @Test
    void testGenerate_Success() throws IOException {
        // Arrange
        String prompt = "Test prompt";
        String expectedResponse = "Test response";
        
        JsonObject mockJsonResponse = new JsonObject();
        mockJsonResponse.addProperty("model", "llama3.2:latest");
        mockJsonResponse.addProperty("done", true);
        
        JsonObject message = new JsonObject();
        message.addProperty("role", "assistant");
        message.addProperty("content", expectedResponse);
        mockJsonResponse.add("message", message);

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(mockJsonResponse.toString());

        // Act
        String result = ollamaService.generate(prompt);

        // Assert
        assertEquals(expectedResponse, result);
        verify(mockHttpClient).newCall(any(Request.class));
        verify(mockCall).execute();
    }

    @Test
    void testGenerate_HttpError() throws IOException {
        // Arrange
        String prompt = "Test prompt";
        
        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockResponse.code()).thenReturn(404);

        // Act
        String result = ollamaService.generate(prompt);

        // Assert
        assertTrue(result.contains("Ollama request failed: 404"));
    }

    @Test
    void testGenerate_IOException() throws IOException {
        // Arrange
        String prompt = "Test prompt";
        
        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException("Connection failed"));

        // Act
        String result = ollamaService.generate(prompt);

        // Assert
        assertTrue(result.contains("Error calling Ollama API"));
    }

    @Test
    void testIsAvailable_True() throws IOException {
        // Arrange
        JsonObject mockJsonResponse = new JsonObject();
        mockJsonResponse.addProperty("model", "llama3.2:latest");
        mockJsonResponse.addProperty("done", true);
        
        JsonObject message = new JsonObject();
        message.addProperty("role", "assistant");
        message.addProperty("content", "Test successful");
        mockJsonResponse.add("message", message);

        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(mockJsonResponse.toString());

        // Act
        boolean result = ollamaService.isAvailable();

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsAvailable_False() throws IOException {
        // Arrange
        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(false);

        // Act
        boolean result = ollamaService.isAvailable();

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetServiceName() {
        // Act
        String result = ollamaService.getServiceName();

        // Assert
        assertEquals("Ollama", result);
    }

    @Test
    void testClearHistory() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> ollamaService.clearHistory());
    }

    @Test
    void testGetAvailableModels() throws IOException {
        // Act
        List<String> models = ollamaService.getAvailableModels();

        // Assert
        assertNotNull(models);
        assertTrue(models.contains("llama3.2:latest"));
    }
}
