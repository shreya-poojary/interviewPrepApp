package com.interviewprep.ui;

import com.interviewprep.model.JobDescription;
import com.interviewprep.service.DocumentService;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

@Slf4j
public class JobDescriptionPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DocumentService documentService;
    private JTextArea jobDescriptionArea;
    private JButton uploadButton;
    private JButton saveButton;
    private JButton nextButton;
    private JButton backButton;
    
    public JobDescriptionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.documentService = mainFrame.getDocumentService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("📋 Job Description");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Paste or upload the job description you're applying for");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        uploadButton = new JButton("Upload File");
        uploadButton.setFont(new Font("Arial", Font.BOLD, 14));
        uploadButton.setPreferredSize(new Dimension(150, 40));
        uploadButton.setFocusPainted(false);
        uploadButton.addActionListener(e -> chooseFile());
        headerPanel.add(uploadButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Text Area
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        jobDescriptionArea = new JTextArea();
        jobDescriptionArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        jobDescriptionArea.setLineWrap(true);
        jobDescriptionArea.setWrapStyleWord(true);
        jobDescriptionArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(jobDescriptionArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Job Description"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Navigation
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainFrame.switchToTab(0));
        bottomPanel.add(backButton, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(100, 40));
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveJobDescription());
        rightPanel.add(saveButton);
        
        nextButton = new JButton("Next: Review →");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setPreferredSize(new Dimension(160, 40));
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(e -> mainFrame.switchToTab(2));
        rightPanel.add(nextButton);
        
        bottomPanel.add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadJobDescription(selectedFile);
        }
    }
    
    private void loadJobDescription(File file) {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return documentService.extractText(file);
            }
            
            @Override
            protected void done() {
                try {
                    jobDescriptionArea.setText(get());
                    saveJobDescription();
                } catch (Exception e) {
                    log.error("Error loading job description", e);
                    JOptionPane.showMessageDialog(JobDescriptionPanel.this,
                        "Error loading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void saveJobDescription() {
        String content = jobDescriptionArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter or upload a job description.",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JobDescription jobDescription = new JobDescription("manual_entry.txt", content);
        mainFrame.setCurrentJobDescription(jobDescription);
        
        JOptionPane.showMessageDialog(this, "Job description saved!", "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
}

