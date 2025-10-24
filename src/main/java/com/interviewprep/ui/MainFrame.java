package com.interviewprep.ui;

import com.interviewprep.model.*;
import com.interviewprep.service.*;
import com.interviewprep.util.IconProvider;
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
        setTitle(IconProvider.getTitle("TARGET", "AI Mock Interview Prep Tool"));
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set recommended font for cross-platform compatibility
        Font recommendedFont = IconProvider.getRecommendedFont();
        UIManager.put("Label.font", recommendedFont);
        UIManager.put("Button.font", recommendedFont);
        UIManager.put("TabbedPane.font", recommendedFont);
        UIManager.put("TextField.font", recommendedFont);
        UIManager.put("TextArea.font", recommendedFont);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(recommendedFont);
        
        // Create panels
        resumePanel = new ResumeUploadPanel(this);
        jobDescriptionPanel = new JobDescriptionPanel(this);
        reviewPanel = new ResumeReviewPanel(this);
        modeSelectorPanel = new InterviewModeSelectorPanel(this);
        interviewPanel = new InterviewPanel(this);
        analyticsPanel = new AnalyticsDashboardPanel(this);
        aiServicePanel = new AIServiceSelectorPanel(this);
        
        // Add tabs with icons
        tabbedPane.addTab(IconProvider.getTitle("DOCUMENT", "Resume"), resumePanel);
        tabbedPane.addTab(IconProvider.getTitle("CLIPBOARD", "Job Description"), jobDescriptionPanel);
        tabbedPane.addTab(IconProvider.getTitle("CHECK", "Review"), reviewPanel);
        tabbedPane.addTab(IconProvider.getTitle("TARGET", "Mode"), modeSelectorPanel);
        tabbedPane.addTab(IconProvider.getTitle("MICROPHONE", "Interview"), interviewPanel);
        tabbedPane.addTab(IconProvider.getTitle("CHART", "Analytics"), analyticsPanel);
        tabbedPane.addTab(IconProvider.getTitle("ROBOT", "AI Service"), aiServicePanel);
        
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
            
            // Add platform debugging information
            log.info("Platform Info: {}", IconProvider.getPlatformInfo());
            status.append("Platform: " + IconProvider.getPlatformInfo() + "\n\n");
            
            // Test all AI services
            aiServiceManager.testAllServices();
            
            if (!aiServiceManager.isCurrentServiceAvailable()) {
                status.append(IconProvider.getStatusMessage("WARNING", "AI service not available. Check Ollama or Bedrock configuration.") + "\n");
            } else {
                status.append(IconProvider.getStatusMessage("CHECK", "AI service is available (" + aiServiceManager.getCurrentServiceName() + ")") + "\n");
            }
            
            if (ttsService.isAvailable()) {
                status.append(IconProvider.getStatusMessage("CHECK", "Text-to-speech is available") + "\n");
            } else {
                status.append(IconProvider.getStatusMessage("WARNING", "Text-to-speech not available on this system.") + "\n");
            }
            
            if (!videoService.isWebcamAvailable()) {
                status.append(IconProvider.getStatusMessage("WARNING", "Webcam not detected.") + "\n");
            } else {
                status.append(IconProvider.getStatusMessage("CHECK", "Webcam is available") + "\n");
            }
            
            if (!audioService.isMicrophoneAvailable()) {
                status.append(IconProvider.getStatusMessage("WARNING", "Microphone not detected.") + "\n");
            } else {
                status.append(IconProvider.getStatusMessage("CHECK", "Microphone is available") + "\n");
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

