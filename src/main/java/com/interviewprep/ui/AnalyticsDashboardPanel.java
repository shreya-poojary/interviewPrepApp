package com.interviewprep.ui;

import com.interviewprep.model.*;
import com.interviewprep.service.StorageService;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

@Slf4j
public class AnalyticsDashboardPanel extends JPanel {
    private final MainFrame mainFrame;
    private final StorageService storageService;
    
    private JList<String> sessionList;
    private DefaultListModel<String> listModel;
    private JTextArea detailsArea;
    private JProgressBar overallScoreBar;
    
    public AnalyticsDashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.storageService = mainFrame.getStorageService();
        initializeUI();
        loadSessions();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("📊 Interview Analytics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton generateAnalyticsButton = new JButton("🤖 Generate Analytics");
        generateAnalyticsButton.setToolTipText("Generate AI-powered analytics for selected session");
        generateAnalyticsButton.addActionListener(e -> generateAnalytics());
        headerButtons.add(generateAnalyticsButton);
        
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadSessions());
        headerButtons.add(refreshButton);
        
        headerPanel.add(headerButtons, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left - Session list
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Past Sessions"));
        
        listModel = new DefaultListModel<>();
        sessionList = new JList<>(listModel);
        sessionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedSession();
            }
        });
        
        JScrollPane listScroll = new JScrollPane(sessionList);
        listPanel.add(listScroll, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(listPanel);
        
        // Right - Details
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setBorder(BorderFactory.createTitledBorder("Overall Performance"));
        
        overallScoreBar = new JProgressBar(0, 100);
        overallScoreBar.setStringPainted(true);
        overallScoreBar.setPreferredSize(new Dimension(400, 40));
        overallScoreBar.setFont(new Font("Arial", Font.BOLD, 14));
        scorePanel.add(overallScoreBar, BorderLayout.CENTER);
        
        detailsPanel.add(scorePanel, BorderLayout.NORTH);
        
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 13));
        detailsArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBorder(BorderFactory.createTitledBorder("Session Details"));
        detailsPanel.add(detailsScroll, BorderLayout.CENTER);
        
        splitPane.setRightComponent(detailsPanel);
        splitPane.setDividerLocation(300);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Bottom
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> mainFrame.switchToTab(4));
        add(backButton, BorderLayout.SOUTH);
    }
    
    private void loadSessions() {
        listModel.clear();
        List<InterviewSession> sessions = storageService.getRecentSessions(20);
        
        for (InterviewSession session : sessions) {
            String entry = String.format("%s - %s (%d questions)",
                session.getStartTime().toLocalDate(),
                session.getMode() != null ? session.getMode().getDisplayName() : "Unknown",
                session.getQuestions().size());
            listModel.addElement(entry);
        }
        
        if (sessions.isEmpty()) {
            detailsArea.setText("No sessions found. Complete an interview to see analytics!");
        }
    }
    
    private void loadSelectedSession() {
        int index = sessionList.getSelectedIndex();
        if (index >= 0) {
            List<InterviewSession> sessions = storageService.getRecentSessions(20);
            if (index < sessions.size()) {
                InterviewSession session = sessions.get(index);
                displaySessionDetails(session);
            }
        }
    }
    
    private void displaySessionDetails(InterviewSession session) {
        // Try to load analytics
        InterviewAnalytics analytics = storageService.loadAnalytics(session.getSessionId());
        
        if (analytics != null) {
            overallScoreBar.setValue((int) (analytics.getOverallScore() * 10));
            overallScoreBar.setString(String.format("%.1f / 10.0 - %s", 
                analytics.getOverallScore(), analytics.getPerformanceLevel()));
            
            if (analytics.getOverallScore() >= 8.0) {
                overallScoreBar.setForeground(new Color(40, 167, 69));
            } else if (analytics.getOverallScore() >= 6.0) {
                overallScoreBar.setForeground(Color.ORANGE);
            } else {
                overallScoreBar.setForeground(Color.RED);
            }
        } else {
            overallScoreBar.setValue(0);
            overallScoreBar.setString("Not analyzed yet");
        }
        
        StringBuilder details = new StringBuilder();
        details.append("📅 Date: ").append(session.getStartTime()).append("\n");
        details.append("🎯 Mode: ").append(session.getMode().getDisplayName()).append("\n");
        details.append("⏱️ Duration: ").append(session.getTotalDurationSeconds() / 60).append(" minutes\n");
        details.append("❓ Questions: ").append(session.getQuestions().size()).append("\n");
        details.append("✅ Answered: ").append(session.getUserAnswers().size()).append("\n\n");
        
        details.append("═══════════════════════════════\n\n");
        
        if (analytics != null) {
            details.append("📊 SCORES:\n");
            details.append(String.format("Technical: %.1f/10\n", analytics.getTechnicalScore()));
            details.append(String.format("Behavioral: %.1f/10\n", analytics.getBehavioralScore()));
            details.append(String.format("Communication: %.1f/10\n", analytics.getCommunicationScore()));
            details.append(String.format("Confidence: %.1f/10\n\n", analytics.getConfidenceScore()));
            
            if (!analytics.getStrengths().isEmpty()) {
                details.append("✅ STRENGTHS:\n");
                for (String strength : analytics.getStrengths()) {
                    details.append("  • ").append(strength).append("\n");
                }
                details.append("\n");
            }
            
            if (!analytics.getWeaknesses().isEmpty()) {
                details.append("⚠️ AREAS TO IMPROVE:\n");
                for (String weakness : analytics.getWeaknesses()) {
                    details.append("  • ").append(weakness).append("\n");
                }
            }
        } else {
            details.append("Run AI analysis to see detailed feedback.");
        }
        
        detailsArea.setText(details.toString());
    }
    
    private void generateAnalytics() {
        int index = sessionList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a session first!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<InterviewSession> sessions = storageService.getRecentSessions(20);
        if (index >= sessions.size()) {
            JOptionPane.showMessageDialog(this, "Invalid session selection!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        InterviewSession session = sessions.get(index);
        
        // Check if analytics already exist
        InterviewAnalytics existingAnalytics = storageService.loadAnalytics(session.getSessionId());
        if (existingAnalytics != null) {
            int choice = JOptionPane.showConfirmDialog(this,
                "Analytics already exist for this session. Regenerate?",
                "Analytics Exist",
                JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Show progress dialog
        JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generating Analytics", true);
        progressDialog.setSize(400, 150);
        progressDialog.setLocationRelativeTo(this);
        
        JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
        progressPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel progressLabel = new JLabel("Generating AI-powered analytics...");
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        progressDialog.add(progressPanel);
        progressDialog.setVisible(true);
        
        // Generate analytics in background
        SwingWorker<InterviewAnalytics, Void> worker = new SwingWorker<>() {
            @Override
            protected InterviewAnalytics doInBackground() throws Exception {
                return mainFrame.getInterviewService().generateAnalytics(session);
            }
            
            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    InterviewAnalytics analytics = get();
                    storageService.saveAnalytics(analytics);
                    
                    // Refresh the display
                    loadSelectedSession();
                    
                    JOptionPane.showMessageDialog(AnalyticsDashboardPanel.this,
                        "Analytics generated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (Exception e) {
                    log.error("Error generating analytics", e);
                    JOptionPane.showMessageDialog(AnalyticsDashboardPanel.this,
                        "Error generating analytics: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
}

