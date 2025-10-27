package com.interviewprep.ui;

import com.interviewprep.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainFrameTest {

    @Mock
    private AIServiceManager mockAIServiceManager;

    @Mock
    private StorageService mockStorageService;

    @Mock
    private InterviewService mockInterviewService;

    @Mock
    private DocumentService mockDocumentService;

    @Mock
    private JavaTTSService mockTtsService;

    @Mock
    private VideoRecordingService mockVideoService;

    @Mock
    private AudioRecordingService mockAudioService;

    private MainFrame mainFrame;

    @BeforeEach
    void setUp() {
        // Create MainFrame with mocked services
        mainFrame = new MainFrame();
        
        // Use reflection to inject mock services
        try {
            var aiServiceField = MainFrame.class.getDeclaredField("aiServiceManager");
            aiServiceField.setAccessible(true);
            aiServiceField.set(mainFrame, mockAIServiceManager);

            var storageField = MainFrame.class.getDeclaredField("storageService");
            storageField.setAccessible(true);
            storageField.set(mainFrame, mockStorageService);

            var interviewField = MainFrame.class.getDeclaredField("interviewService");
            interviewField.setAccessible(true);
            interviewField.set(mainFrame, mockInterviewService);

            var documentField = MainFrame.class.getDeclaredField("documentService");
            documentField.setAccessible(true);
            documentField.set(mainFrame, mockDocumentService);

            var ttsField = MainFrame.class.getDeclaredField("ttsService");
            ttsField.setAccessible(true);
            ttsField.set(mainFrame, mockTtsService);

            var videoField = MainFrame.class.getDeclaredField("videoService");
            videoField.setAccessible(true);
            videoField.set(mainFrame, mockVideoService);

            var audioField = MainFrame.class.getDeclaredField("audioService");
            audioField.setAccessible(true);
            audioField.set(mainFrame, mockAudioService);
        } catch (Exception e) {
            fail("Failed to inject mock services: " + e.getMessage());
        }
    }

    @Test
    void testMainFrameInitialization() {
        // Act
        MainFrame frame = new MainFrame();

        // Assert
        assertNotNull(frame);
        assertTrue(frame.isVisible());
        assertEquals("AI Mock Interview Prep Tool", frame.getTitle());
    }

    @Test
    void testGetAIServiceManager() {
        // Act
        AIServiceManager service = mainFrame.getAIServiceManager();

        // Assert
        assertNotNull(service);
        assertEquals(mockAIServiceManager, service);
    }

    @Test
    void testGetStorageService() {
        // Act
        StorageService service = mainFrame.getStorageService();

        // Assert
        assertNotNull(service);
        assertEquals(mockStorageService, service);
    }

    @Test
    void testGetInterviewService() {
        // Act
        InterviewService service = mainFrame.getInterviewService();

        // Assert
        assertNotNull(service);
        assertEquals(mockInterviewService, service);
    }

    @Test
    void testGetDocumentService() {
        // Act
        DocumentService service = mainFrame.getDocumentService();

        // Assert
        assertNotNull(service);
        assertEquals(mockDocumentService, service);
    }

    @Test
    void testGetTtsService() {
        // Act
        JavaTTSService service = mainFrame.getTtsService();

        // Assert
        assertNotNull(service);
        assertEquals(mockTtsService, service);
    }

    @Test
    void testGetVideoService() {
        // Act
        VideoRecordingService service = mainFrame.getVideoService();

        // Assert
        assertNotNull(service);
        assertEquals(mockVideoService, service);
    }

    @Test
    void testGetAudioService() {
        // Act
        AudioRecordingService service = mainFrame.getAudioService();

        // Assert
        assertNotNull(service);
        assertEquals(mockAudioService, service);
    }

    @Test
    void testSwitchToTab() {
        // Arrange
        MainFrame frame = new MainFrame();

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> frame.switchToTab(0));
        assertDoesNotThrow(() -> frame.switchToTab(1));
        assertDoesNotThrow(() -> frame.switchToTab(2));
        assertDoesNotThrow(() -> frame.switchToTab(3));
        assertDoesNotThrow(() -> frame.switchToTab(4));
        assertDoesNotThrow(() -> frame.switchToTab(5));
    }

    @Test
    void testSwitchToTab_InvalidIndex() {
        // Arrange
        MainFrame frame = new MainFrame();

        // Act & Assert - should handle invalid index gracefully
        assertDoesNotThrow(() -> frame.switchToTab(-1));
        assertDoesNotThrow(() -> frame.switchToTab(100));
    }

    @Test
    void testWindowClosing() {
        // Arrange
        MainFrame frame = new MainFrame();
        WindowEvent event = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> {
            frame.dispatchEvent(event);
        });
    }

    @Test
    void testCheckServiceAvailability() {
        // Arrange
        when(mockAIServiceManager.testAllServices()).thenReturn(true);
        when(mockAIServiceManager.getCurrentServiceName()).thenReturn("Ollama");
        when(mockTtsService.isAvailable()).thenReturn(true);
        when(mockVideoService.isAvailable()).thenReturn(true);
        when(mockAudioService.isAvailable()).thenReturn(true);

        // Act
        mainFrame.checkServiceAvailability();

        // Assert
        verify(mockAIServiceManager).testAllServices();
        verify(mockTtsService).isAvailable();
        verify(mockVideoService).isAvailable();
        verify(mockAudioService).isAvailable();
    }

    @Test
    void testGetPreferredAIService() {
        // Arrange
        when(mockAIServiceManager.getCurrentServiceName()).thenReturn("Ollama");

        // Act
        String service = mainFrame.getPreferredAIService();

        // Assert
        assertEquals("Ollama", service);
    }

    @Test
    void testGetCurrentAIModel() {
        // Arrange
        when(mockAIServiceManager.getCurrentServiceName()).thenReturn("Ollama");

        // Act
        String model = mainFrame.getCurrentAIModel();

        // Assert
        assertNotNull(model);
    }

    @Test
    void testGetAvailableAIModels() {
        // Arrange
        when(mockAIServiceManager.getAvailableModels()).thenReturn(java.util.List.of("llama3.2:latest"));

        // Act
        java.util.List<String> models = mainFrame.getAvailableAIModels();

        // Assert
        assertNotNull(models);
        assertTrue(models.contains("llama3.2:latest"));
    }

    @Test
    void testGetTabbedPane() {
        // Act
        JTabbedPane tabbedPane = mainFrame.getTabbedPane();

        // Assert
        assertNotNull(tabbedPane);
        assertTrue(tabbedPane.getTabCount() > 0);
    }

    @Test
    void testGetTabCount() {
        // Act
        int tabCount = mainFrame.getTabCount();

        // Assert
        assertTrue(tabCount > 0);
    }

    @Test
    void testIsServiceAvailable() {
        // Arrange
        when(mockAIServiceManager.isAvailable()).thenReturn(true);

        // Act
        boolean available = mainFrame.isServiceAvailable();

        // Assert
        assertTrue(available);
        verify(mockAIServiceManager).isAvailable();
    }

    @Test
    void testGetServiceStatus() {
        // Arrange
        when(mockAIServiceManager.isAvailable()).thenReturn(true);
        when(mockAIServiceManager.getCurrentServiceName()).thenReturn("Ollama");
        when(mockTtsService.isAvailable()).thenReturn(true);
        when(mockVideoService.isAvailable()).thenReturn(true);
        when(mockAudioService.isAvailable()).thenReturn(true);

        // Act
        String status = mainFrame.getServiceStatus();

        // Assert
        assertNotNull(status);
        assertTrue(status.contains("Ollama"));
    }
}
