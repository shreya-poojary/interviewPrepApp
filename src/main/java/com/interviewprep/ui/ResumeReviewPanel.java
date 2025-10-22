package com.interviewprep.ui;

import com.interviewprep.model.*;
import com.interviewprep.service.InterviewService;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Slf4j
public class ResumeReviewPanel extends JPanel {
    private final MainFrame mainFrame;
    private final InterviewService interviewService;
    
    private JTextArea feedbackArea;
    private JProgressBar matchScoreBar;
    private JButton analyzeButton;
    private JButton nextButton;
    
    public ResumeReviewPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.interviewService = mainFrame.getInterviewService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("✅ Resume Analysis");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel scorePanel = new JPanel(new BorderLayout(10, 10));
        scorePanel.setBorder(BorderFactory.createTitledBorder("Match Score"));
        matchScoreBar = new JProgressBar(0, 100);
        matchScoreBar.setStringPainted(true);
        matchScoreBar.setPreferredSize(new Dimension(400, 40));
        matchScoreBar.setFont(new Font("Arial", Font.BOLD, 14));
        scorePanel.add(matchScoreBar, BorderLayout.CENTER);
        centerPanel.add(scorePanel, BorderLayout.NORTH);
        
        feedbackArea = new JTextArea();
        feedbackArea.setEditable(false);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setFont(new Font("Arial", Font.PLAIN, 13));
        feedbackArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("AI Feedback"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> mainFrame.switchToTab(1));
        bottomPanel.add(backButton, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        analyzeButton = new JButton("🤖 Analyze with AI");
        analyzeButton.setFont(new Font("Arial", Font.BOLD, 14));
        analyzeButton.setPreferredSize(new Dimension(180, 40));
        analyzeButton.addActionListener(e -> analyzeResume());
        rightPanel.add(analyzeButton);
        
        nextButton = new JButton("Next: Select Mode →");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setPreferredSize(new Dimension(200, 40));
        nextButton.addActionListener(e -> mainFrame.switchToTab(3));
        rightPanel.add(nextButton);
        
        bottomPanel.add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void onDataUpdated() {
        Resume resume = mainFrame.getCurrentResume();
        JobDescription jobDesc = mainFrame.getCurrentJobDescription();
        
        if (resume != null && jobDesc != null) {
            analyzeButton.setEnabled(true);
        }
    }
    
    private void analyzeResume() {
        Resume resume = mainFrame.getCurrentResume();
        JobDescription jobDesc = mainFrame.getCurrentJobDescription();
        
        if (resume == null || jobDesc == null) {
            JOptionPane.showMessageDialog(this, "Please upload both resume and job description first!");
            return;
        }
        
        analyzeButton.setEnabled(false);
        feedbackArea.setText("Analyzing with AI... Please wait.");
        
        SwingWorker<ResumeAnalysis, Void> worker = new SwingWorker<>() {
            @Override
            protected ResumeAnalysis doInBackground() throws Exception {
                return interviewService.analyzeResume(resume, jobDesc);
            }
            
            @Override
            protected void done() {
                try {
                    ResumeAnalysis analysis = get();
                    displayAnalysis(analysis);
                } catch (Exception e) {
                    log.error("Error analyzing resume", e);
                    feedbackArea.setText("Error: " + e.getMessage());
                } finally {
                    analyzeButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    private void displayAnalysis(ResumeAnalysis analysis) {
        matchScoreBar.setValue(analysis.getMatchScore());
        matchScoreBar.setString(analysis.getMatchScore() + "% - " + analysis.getMatchLevel());
        
        if (analysis.getMatchScore() >= 75) {
            matchScoreBar.setForeground(new Color(40, 167, 69));
        } else if (analysis.getMatchScore() >= 50) {
            matchScoreBar.setForeground(Color.ORANGE);
        } else {
            matchScoreBar.setForeground(Color.RED);
        }
        
        StringBuilder feedback = new StringBuilder();
        feedback.append("📊 OVERALL FEEDBACK:\n");
        feedback.append(analysis.getOverallFeedback()).append("\n\n");
        
        if (!analysis.getStrengths().isEmpty()) {
            feedback.append("✅ STRENGTHS:\n");
            for (String strength : analysis.getStrengths()) {
                feedback.append("  • ").append(strength).append("\n");
            }
            feedback.append("\n");
        }
        
        if (!analysis.getWeaknesses().isEmpty()) {
            feedback.append("⚠️ AREAS TO IMPROVE:\n");
            for (String weakness : analysis.getWeaknesses()) {
                feedback.append("  • ").append(weakness).append("\n");
            }
            feedback.append("\n");
        }
        
        if (!analysis.getSuggestions().isEmpty()) {
            feedback.append("💡 SUGGESTIONS:\n");
            for (String suggestion : analysis.getSuggestions()) {
                feedback.append("  • ").append(suggestion).append("\n");
            }
        }
        
        feedbackArea.setText(feedback.toString());
    }
}

