package com.interviewprep.ui;

import com.interviewprep.model.*;
import com.interviewprep.service.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main application frame
 */
@Slf4j
@Getter
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    
    // Panels
    private ResumeUploadPanel resumePanel;
    private JobDescriptionPanel jobDescriptionPanel;
    private ResumeReviewPanel reviewPanel;
    private InterviewModeSelectorPanel modeSelectorPanel;
    private InterviewPanel interviewPanel;
    private AnalyticsDashboardPanel analyticsPanel;
    private AIServiceSelectorPanel aiServicePanel;
    
    // Services
    private final ConfigurationService config;
    private final AIServiceManager aiServiceManager;
    private final JavaTTSService ttsService;
    private final DocumentService documentService;
    private final StorageService storageService;
    private final InterviewService interviewService;
    private final VideoRecordingService videoService;
    private final AudioRecordingService audioService;
    
    // Current data
    private Resume currentResume;
    private JobDescription currentJobDescription;
    private InterviewMode selectedMode;
    private List<InterviewQuestion> currentQuestions;
    private MCPContext mcpContext;
    
    // Keyboard shortcuts
    private KeyboardShortcutManager shortcutManager;
    
    public MainFrame() {
        // Initialize services
        config = ConfigurationService.getInstance();
        
        // Initialize AI Service Manager (handles both Ollama and Bedrock)
        aiServiceManager = new AIServiceManager(config);
        
        boolean ttsEnabled = config.getBooleanProperty("tts.enabled", true);
        ttsService = new JavaTTSService(ttsEnabled);
        
        documentService = new DocumentService();
        
        String storagePath = config.getProperty("storage.path", "data");
        storageService = new StorageService(storagePath);
        
        interviewService = new InterviewService(aiServiceManager, ttsService, storageService);
        videoService = new VideoRecordingService();
        audioService = new AudioRecordingService();
        
        // Load MCP context
        mcpContext = storageService.loadMCPContext("default_user");
        
        // Initialize UI
        initializeUI();
        
        // Check service availability
        checkServiceAvailability();
    }
    
    private void initializeUI() {
        setTitle("🎯 AI Mock Interview Prep Tool");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Create panels
        resumePanel = new ResumeUploadPanel(this);
        jobDescriptionPanel = new JobDescriptionPanel(this);
        reviewPanel = new ResumeReviewPanel(this);
        modeSelectorPanel = new InterviewModeSelectorPanel(this);
        interviewPanel = new InterviewPanel(this);
        analyticsPanel = new AnalyticsDashboardPanel(this);
        aiServicePanel = new AIServiceSelectorPanel(this);
        
        // Add tabs with icons
        tabbedPane.addTab("📄 Resume", resumePanel);
        tabbedPane.addTab("📋 Job Description", jobDescriptionPanel);
        tabbedPane.addTab("✅ Review", reviewPanel);
        tabbedPane.addTab("🎯 Mode", modeSelectorPanel);
        tabbedPane.addTab("🎤 Interview", interviewPanel);
        tabbedPane.addTab("📊 Analytics", analyticsPanel);
        tabbedPane.addTab("🤖 AI Service", aiServicePanel);
        
        add(tabbedPane);
        
        // Initialize keyboard shortcuts
        if (config.getBooleanProperty("ui.keyboard.shortcuts", true)) {
            shortcutManager = new KeyboardShortcutManager(this);
        }
        
        // Add window listener for cleanup
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cleanup();
            }
        });
    }
    
    private void checkServiceAvailability() {
        SwingUtilities.invokeLater(() -> {
            StringBuilder status = new StringBuilder("Service Status:\n\n");
            
            // Test all AI services
            aiServiceManager.testAllServices();
            
            if (!aiServiceManager.isCurrentServiceAvailable()) {
                status.append("⚠️ AI service not available. Check Ollama or Bedrock configuration.\n");
            } else {
                status.append("✅ AI service is available (").append(aiServiceManager.getCurrentServiceName()).append(")\n");
            }
            
            if (ttsService.isAvailable()) {
                status.append("✅ Text-to-speech is available\n");
            } else {
                status.append("⚠️ Text-to-speech not available on this system.\n");
            }
            
            if (!videoService.isWebcamAvailable()) {
                status.append("⚠️ Webcam not detected.\n");
            } else {
                status.append("✅ Webcam is available\n");
            }
            
            if (!audioService.isMicrophoneAvailable()) {
                status.append("⚠️ Microphone not detected.\n");
            } else {
                status.append("✅ Microphone is available\n");
            }
            
            log.info(status.toString());
        });
    }
    
    public void switchToTab(int index) {
        tabbedPane.setSelectedIndex(index);
    }
    
    public void toggleRecording() {
        if (interviewPanel != null) {
            interviewPanel.toggleRecording();
        }
    }
    
    public void nextQuestion() {
        if (interviewPanel != null) {
            interviewPanel.nextQuestion();
        }
    }
    
    public void showModeSelector() {
        switchToTab(3); // Mode selector tab
    }
    
    public void startQuickPractice() {
        if (currentResume != null && currentJobDescription != null) {
            selectedMode = InterviewMode.PRACTICE;
            switchToTab(4); // Interview tab
        } else {
            JOptionPane.showMessageDialog(this,
                "Please upload resume and job description first!",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void cleanup() {
        try {
            // Save MCP context
            if (mcpContext != null) {
                storageService.saveMCPContext(mcpContext);
            }
            
            // Stop any recording
            if (videoService.isRecording()) {
                videoService.stopRecording();
            }
            if (audioService.isRecording()) {
                audioService.stopRecording();
            }
            
            log.info("Application cleanup completed");
        } catch (Exception e) {
            log.error("Error during cleanup", e);
        }
    }
    
    // Setters for current data
    public void setCurrentResume(Resume resume) {
        this.currentResume = resume;
        reviewPanel.onDataUpdated();
    }
    
    public void setCurrentJobDescription(JobDescription jobDescription) {
        this.currentJobDescription = jobDescription;
        reviewPanel.onDataUpdated();
    }
    
    public void setSelectedMode(InterviewMode mode) {
        this.selectedMode = mode;
    }
    
    public void setCurrentQuestions(List<InterviewQuestion> questions) {
        this.currentQuestions = questions;
    }
    
    // Getters for services
    public VideoRecordingService getVideoService() {
        return videoService;
    }
    
    public AudioRecordingService getAudioService() {
        return audioService;
    }
    
    public AIServiceManager getAIServiceManager() {
        return aiServiceManager;
    }
    
    public DocumentService getDocumentService() {
        return documentService;
    }
    
    public InterviewService getInterviewService() {
        return interviewService;
    }
    
    public StorageService getStorageService() {
        return storageService;
    }
    
    public JavaTTSService getTtsService() {
        return ttsService;
    }
    
    public ConfigurationService getConfig() {
        return config;
    }
    
    public Resume getCurrentResume() {
        return currentResume;
    }
    
    public JobDescription getCurrentJobDescription() {
        return currentJobDescription;
    }
    
    public InterviewMode getSelectedMode() {
        return selectedMode;
    }
    
    public List<InterviewQuestion> getCurrentQuestions() {
        return currentQuestions;
    }
}

