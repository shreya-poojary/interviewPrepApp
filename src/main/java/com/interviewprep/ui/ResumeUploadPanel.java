package com.interviewprep.ui;

import com.interviewprep.model.Resume;
import com.interviewprep.service.DocumentService;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Panel for uploading and previewing resume
 */
@Slf4j
public class ResumeUploadPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DocumentService documentService;
    
    private JTextArea resumePreview;
    private JLabel fileNameLabel;
    private JLabel statusLabel;
    private JButton uploadButton;
    private JButton nextButton;
    
    public ResumeUploadPanel(MainFrame mainFrame) {
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
        
        JLabel titleLabel = new JLabel("📄 Upload Your Resume");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Supports PDF, DOCX, and TXT formats");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        uploadButton = new JButton("Choose File");
        uploadButton.setFont(new Font("Arial", Font.BOLD, 16));
        uploadButton.setPreferredSize(new Dimension(150, 40));
        uploadButton.setFocusPainted(false);
        uploadButton.addActionListener(e -> chooseFile());
        headerPanel.add(uploadButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Preview
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        fileNameLabel = new JLabel("No file selected");
        fileNameLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        fileNameLabel.setForeground(Color.GRAY);
        statusPanel.add(fileNameLabel, BorderLayout.WEST);
        
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusPanel.add(statusLabel, BorderLayout.EAST);
        
        centerPanel.add(statusPanel, BorderLayout.NORTH);
        
        resumePreview = new JTextArea();
        resumePreview.setEditable(false);
        resumePreview.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resumePreview.setLineWrap(true);
        resumePreview.setWrapStyleWord(true);
        resumePreview.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(resumePreview);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Resume Preview"));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Navigation
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        nextButton = new JButton("Next: Job Description →");
        nextButton.setEnabled(false);
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.setPreferredSize(new Dimension(220, 40));
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(e -> mainFrame.switchToTab(1));
        bottomPanel.add(nextButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().matches(".*\\.(pdf|docx|txt)$");
            }
            public String getDescription() {
                return "Supported Documents (*.pdf, *.docx, *.txt)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadResume(selectedFile);
        }
    }
    
    private void loadResume(File file) {
        uploadButton.setEnabled(false);
        statusLabel.setText("Loading...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return documentService.extractText(file);
            }
            
            @Override
            protected void done() {
                try {
                    String content = get();
                    Resume resume = new Resume(file.getName(), content);
                    mainFrame.setCurrentResume(resume);
                    
                    fileNameLabel.setText("📄 " + file.getName());
                    fileNameLabel.setForeground(new Color(40, 167, 69));
                    resumePreview.setText(content);
                    nextButton.setEnabled(true);
                    
                    statusLabel.setText("✓ Loaded successfully");
                    statusLabel.setForeground(new Color(40, 167, 69));
                    
                    log.info("Resume loaded: {}", file.getName());
                } catch (Exception e) {
                    log.error("Error loading resume", e);
                    JOptionPane.showMessageDialog(
                        ResumeUploadPanel.this,
                        "Error loading resume: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    statusLabel.setText("✗ Error loading file");
                    statusLabel.setForeground(Color.RED);
                } finally {
                    uploadButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}

