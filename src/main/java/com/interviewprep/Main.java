package com.interviewprep;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.interviewprep.service.ConfigurationService;
import com.interviewprep.ui.MainFrame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.File;

/**
 * Main entry point for AI Mock Interview Prep Tool
 * Uses free and open-source tools for AI-powered interview preparation
 */
@Slf4j
public class Main {
    
    public static void main(String[] args) {
        log.info("Starting AI Mock Interview Prep Tool...");
        
        // Create necessary directories
        createDirectories();
        
        // Load configuration
        ConfigurationService config = ConfigurationService.getInstance();
        
        // Set Look and Feel
        setLookAndFeel(config);
        
        // Show splash screen
        showSplashScreen();
        
        // Launch application
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                log.info("Application started successfully");
            } catch (Exception e) {
                log.error("Error starting application", e);
                showErrorDialog(e);
            }
        });
    }
    
    private static void createDirectories() {
        String[] directories = {
            "recordings",
            "data",
            "mcp_data",
            "logs",
            "temp"
        };
        
        for (String dir : directories) {
            File directory = new File(dir);
            if (!directory.exists()) {
                directory.mkdirs();
                log.info("Created directory: {}", dir);
            }
        }
    }
    
    private static void setLookAndFeel(ConfigurationService config) {
        try {
            String theme = config.getProperty("ui.theme", "dark");
            if ("dark".equals(theme)) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            
            // Custom UI properties
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            
            log.info("UI theme set to: {}", theme);
        } catch (Exception e) {
            log.warn("Could not set Look and Feel, using default", e);
        }
    }
    
    private static void showSplashScreen() {
        JWindow splash = new JWindow();
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        JLabel title = new JLabel("🎯 AI Interview Prep Tool");
        title.setFont(title.getFont().deriveFont(24f));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Preparing your interview assistant...");
        subtitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setAlignmentX(JProgressBar.CENTER_ALIGNMENT);
        
        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));
        content.add(progress);
        
        splash.setContentPane(content);
        splash.pack();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        
        // Close splash after 2 seconds
        Timer timer = new Timer(2000, e -> splash.dispose());
        timer.setRepeats(false);
        timer.start();
    }
    
    private static void showErrorDialog(Exception e) {
        String message = String.format(
            "Failed to start application:\n\n%s\n\nPlease check logs for details.",
            e.getMessage()
        );
        
        JOptionPane.showMessageDialog(
            null,
            message,
            "Startup Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

