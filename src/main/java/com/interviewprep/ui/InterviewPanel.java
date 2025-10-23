package com.interviewprep.ui;

import com.interviewprep.model.*;
import com.interviewprep.service.*;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class InterviewPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTextArea questionArea;
    private JTextArea answerArea;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private JProgressBar progressBar;
    private JButton startButton;
    private JButton stopButton;
    private JButton nextQuestionButton;
    private JButton submitAnswerButton;
    
    private InterviewSession currentSession;
    private InterviewRecording currentRecording;
    private int currentQuestionIndex = 0;
    private LocalDateTime questionStartTime;
    private Timer countdownTimer;
    
    public InterviewPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("🎤 Mock Interview");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusLabel = new JLabel("Ready to start");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        statusLabel.setForeground(Color.BLUE);
        statusPanel.add(statusLabel);
        
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(timerLabel);
        
        headerPanel.add(statusPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Q&A
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setFont(new Font("Arial", Font.BOLD, 16));
        questionArea.setMargin(new Insets(15, 15, 15, 15));
        questionArea.setBackground(Color.WHITE);
        questionArea.setForeground(Color.BLACK);
        questionArea.setOpaque(true);
        questionArea.setText("Click 'Start Interview' to begin");
        
        JScrollPane questionScroll = new JScrollPane(questionArea);
        questionScroll.setBorder(BorderFactory.createTitledBorder("Question"));
        questionPanel.add(questionScroll, BorderLayout.CENTER);
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        questionPanel.add(progressBar, BorderLayout.SOUTH);
        
        splitPane.setTopComponent(questionPanel);
        
        JPanel answerPanel = new JPanel(new BorderLayout());
        answerArea = new JTextArea();
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);
        answerArea.setFont(new Font("Arial", Font.PLAIN, 14));
        answerArea.setMargin(new Insets(15, 15, 15, 15));
        answerArea.setEnabled(false);
        
        // Add focus listener to ensure proper text input
        answerArea.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                answerArea.setCaretPosition(answerArea.getText().length());
            }
        });
        
        JScrollPane answerScroll = new JScrollPane(answerArea);
        answerScroll.setBorder(BorderFactory.createTitledBorder("Your Answer (Type or speak)"));
        answerPanel.add(answerScroll, BorderLayout.CENTER);
        
        splitPane.setBottomComponent(answerPanel);
        splitPane.setDividerLocation(250);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Bottom - Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> mainFrame.switchToTab(3));
        controlPanel.add(backButton);
        
        startButton = new JButton("▶ Start Interview");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(40, 167, 69));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(180, 40));
        startButton.addActionListener(e -> startInterview());
        controlPanel.add(startButton);
        
        submitAnswerButton = new JButton("Submit Answer");
        submitAnswerButton.setEnabled(false);
        submitAnswerButton.setPreferredSize(new Dimension(150, 40));
        submitAnswerButton.addActionListener(e -> submitAnswer());
        controlPanel.add(submitAnswerButton);
        
        nextQuestionButton = new JButton("Next Question →");
        nextQuestionButton.setEnabled(false);
        nextQuestionButton.setPreferredSize(new Dimension(150, 40));
        nextQuestionButton.addActionListener(e -> nextQuestion());
        controlPanel.add(nextQuestionButton);
        
        stopButton = new JButton("■ Stop Interview");
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setBackground(new Color(220, 53, 69));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.setEnabled(false);
        stopButton.setPreferredSize(new Dimension(180, 40));
        stopButton.addActionListener(e -> stopInterview());
        controlPanel.add(stopButton);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        // Recording Controls Panel
        JPanel recordingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        recordingPanel.setBorder(BorderFactory.createTitledBorder("Recording Controls"));
        
        // Video recording button
        JButton videoButton = new JButton("📹 Video");
        videoButton.setToolTipText("Toggle video recording");
        videoButton.setPreferredSize(new Dimension(100, 35));
        videoButton.addActionListener(e -> toggleVideoRecording());
        recordingPanel.add(videoButton);
        
        // Audio recording button  
        JButton audioButton = new JButton("🎤 Audio");
        audioButton.setToolTipText("Toggle audio recording");
        audioButton.setPreferredSize(new Dimension(100, 35));
        audioButton.addActionListener(e -> toggleAudioRecording());
        recordingPanel.add(audioButton);
        
        // Playback button
        JButton playbackButton = new JButton("▶️ Playback");
        playbackButton.setToolTipText("Review recordings");
        playbackButton.setPreferredSize(new Dimension(120, 35));
        playbackButton.addActionListener(e -> showPlayback());
        recordingPanel.add(playbackButton);
        
        // Add recording panel above controls
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(recordingPanel, BorderLayout.NORTH);
        bottomPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Replace the old control panel
        remove(controlPanel);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void startInterview() {
        try {
            List<InterviewQuestion> questions = mainFrame.getCurrentQuestions();
            
            if (questions == null || questions.isEmpty()) {
                generateQuestions();
                return;
            }
            
            currentSession = new InterviewSession();
            currentSession.setMode(mainFrame.getSelectedMode());
            currentSession.setQuestions(questions);
            
            currentRecording = new InterviewRecording();
            currentRecording.setSession(currentSession);
            
            // Start recording
            String recordingDir = currentRecording.getRecordingDirectory();
            startRecording(recordingDir);
            
            currentQuestionIndex = 0;
            displayCurrentQuestion();
            
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            submitAnswerButton.setEnabled(true);
            answerArea.setEnabled(true);
            statusLabel.setText("● Recording");
            statusLabel.setForeground(Color.RED);
            
            log.info("Interview started");
        } catch (Exception e) {
            log.error("Error starting interview", e);
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void generateQuestions() {
        Resume resume = mainFrame.getCurrentResume();
        JobDescription jobDesc = mainFrame.getCurrentJobDescription();
        InterviewMode mode = mainFrame.getSelectedMode();
        
        if (resume == null || jobDesc == null) {
            JOptionPane.showMessageDialog(this, "Please complete resume and job description first!");
            return;
        }
        
        startButton.setEnabled(false);
        statusLabel.setText("Generating questions...");
        
        SwingWorker<List<InterviewQuestion>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<InterviewQuestion> doInBackground() throws Exception {
                return mainFrame.getInterviewService().generateQuestions(resume, jobDesc, mode, 5);
            }
            
            @Override
            protected void done() {
                try {
                    List<InterviewQuestion> questions = get();
                    mainFrame.setCurrentQuestions(questions);
                    JOptionPane.showMessageDialog(InterviewPanel.this, 
                        "Questions generated! Click Start Interview again.");
                    startButton.setEnabled(true);
                    statusLabel.setText("Ready");
                } catch (Exception e) {
                    log.error("Error generating questions", e);
                    JOptionPane.showMessageDialog(InterviewPanel.this, "Error: " + e.getMessage());
                    startButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    private void startRecording(String recordingDir) {
        try {
            if (mainFrame.getConfig().getBooleanProperty("video.enabled", true) &&
                mainFrame.getVideoService().isWebcamAvailable()) {
                int width = mainFrame.getConfig().getIntProperty("video.width", 640);
                int height = mainFrame.getConfig().getIntProperty("video.height", 480);
                int fps = mainFrame.getConfig().getIntProperty("video.fps", 30);
                mainFrame.getVideoService().initialize(recordingDir, width, height, fps);
                mainFrame.getVideoService().startRecording();
            }
            
            if (mainFrame.getConfig().getBooleanProperty("audio.enabled", true) &&
                mainFrame.getAudioService().isMicrophoneAvailable()) {
                mainFrame.getAudioService().initialize(recordingDir);
                mainFrame.getAudioService().startRecording();
            }
        } catch (Exception e) {
            log.warn("Could not start recording: " + e.getMessage());
        }
    }
    
    private void displayCurrentQuestion() {
        if (currentQuestionIndex < currentSession.getQuestions().size()) {
            InterviewQuestion question = currentSession.getQuestions().get(currentQuestionIndex);
            String questionText = String.format("Question %d/%d:\n\n%s",
                currentQuestionIndex + 1,
                currentSession.getQuestions().size(),
                question.getQuestion());
            
            log.info("Displaying question: {}", questionText);
            questionArea.setText(questionText);
            questionArea.setCaretPosition(0); // Scroll to top
            
            // Force repaint to ensure visibility
            questionArea.repaint();
            questionArea.revalidate();
            
            answerArea.setText("");
            answerArea.requestFocus();
            
            progressBar.setValue(((currentQuestionIndex + 1) * 100) / currentSession.getQuestions().size());
            progressBar.setString((currentQuestionIndex + 1) + " / " + currentSession.getQuestions().size());
            
            questionStartTime = LocalDateTime.now();
            startQuestionTimer(question);
            
            // Optional: Speak question
            if (mainFrame.getConfig().getBooleanProperty("tts.enabled", true)) {
                speakQuestion(question.getQuestion());
            }
        }
    }
    
    private void startQuestionTimer(InterviewQuestion question) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        
        if (question.getTimeLimit() > 0) {
            final int[] secondsLeft = {question.getTimeLimit()};
            countdownTimer = new Timer(1000, e -> {
                secondsLeft[0]--;
                int minutes = secondsLeft[0] / 60;
                int seconds = secondsLeft[0] % 60;
                timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
                
                if (secondsLeft[0] <= 0) {
                    ((Timer) e.getSource()).stop();
                    JOptionPane.showMessageDialog(InterviewPanel.this, "Time's up!");
                    submitAnswer();
                }
            });
            countdownTimer.start();
        }
    }
    
    private void speakQuestion(String text) {
        if (mainFrame.getTtsService() != null) {
            mainFrame.getTtsService().speakAsync(text);
        }
    }
    
    private void submitAnswer() {
        String answer = answerArea.getText().trim();
        if (answer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide an answer!");
            return;
        }
        
        InterviewQuestion question = currentSession.getQuestions().get(currentQuestionIndex);
        long duration = Duration.between(questionStartTime, LocalDateTime.now()).getSeconds();
        
        currentSession.addQuestionAnswer(question, answer, duration);
        
        submitAnswerButton.setEnabled(false);
        nextQuestionButton.setEnabled(true);
        answerArea.setEnabled(false);
        
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
    }
    
    public void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < currentSession.getQuestions().size()) {
            displayCurrentQuestion();
            submitAnswerButton.setEnabled(true);
            nextQuestionButton.setEnabled(false);
            answerArea.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "All questions answered! Click Stop Interview.");
            nextQuestionButton.setEnabled(false);
        }
    }
    
    public void toggleRecording() {
        if (currentSession != null) {
            if (submitAnswerButton.isEnabled()) {
                submitAnswer();
            }
        }
    }
    
    private void toggleVideoRecording() {
        try {
            VideoRecordingService videoService = mainFrame.getVideoService();
            if (videoService.isRecording()) {
                videoService.stopRecording();
                log.info("Video recording stopped");
            } else {
                // Initialize if not already done
                if (!videoService.isInitialized()) {
                    String sessionDir = "recordings/" + java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + 
                            java.util.UUID.randomUUID().toString().substring(0, 8);
                    videoService.initialize(sessionDir, 640, 480, 30);
                }
                videoService.startRecording();
                log.info("Video recording started");
            }
        } catch (Exception e) {
            log.error("Error toggling video recording", e);
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void toggleAudioRecording() {
        try {
            AudioRecordingService audioService = mainFrame.getAudioService();
            if (audioService.isRecording()) {
                audioService.stopRecording();
                log.info("Audio recording stopped");
            } else {
                // Initialize if not already done
                if (!audioService.isInitialized()) {
                    String sessionDir = "recordings/" + java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + 
                            java.util.UUID.randomUUID().toString().substring(0, 8);
                    audioService.initialize(sessionDir);
                }
                audioService.startRecording();
                log.info("Audio recording started");
            }
        } catch (Exception e) {
            log.error("Error toggling audio recording", e);
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void showPlayback() {
        try {
            // Switch to analytics tab to show recordings
            mainFrame.switchToTab(5); // Analytics tab
            JOptionPane.showMessageDialog(this, "Check the Analytics tab to review your recordings!");
        } catch (Exception e) {
            log.error("Error showing playback", e);
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void stopInterview() {
        try {
            currentSession.complete();
            
            // Stop recordings
            if (mainFrame.getVideoService().isRecording()) {
                String videoPath = mainFrame.getVideoService().stopRecording();
                currentRecording.setVideoFilePath(videoPath);
            }
            
            if (mainFrame.getAudioService().isRecording()) {
                String audioPath = mainFrame.getAudioService().stopRecording();
                currentRecording.setAudioFilePath(audioPath);
            }
            
            currentRecording.setDurationSeconds(currentSession.getTotalDurationSeconds());
            
            // Save session
            mainFrame.getStorageService().saveSession(currentSession);
            
            statusLabel.setText("Interview completed!");
            statusLabel.setForeground(new Color(40, 167, 69));
            
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            submitAnswerButton.setEnabled(false);
            nextQuestionButton.setEnabled(false);
            
            int choice = JOptionPane.showConfirmDialog(this,
                "Interview saved! View analytics?",
                "Success",
                JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                mainFrame.switchToTab(5); // Analytics tab
            }
            
            log.info("Interview stopped and saved");
        } catch (Exception e) {
            log.error("Error stopping interview", e);
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}

