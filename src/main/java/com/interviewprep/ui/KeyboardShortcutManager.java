package com.interviewprep.ui;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.*;

/**
 * Global keyboard shortcut manager
 */
@Slf4j
public class KeyboardShortcutManager {
    private final MainFrame mainFrame;
    
    public KeyboardShortcutManager(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        registerGlobalShortcuts();
        log.info("Keyboard shortcuts enabled");
    }
    
    private void registerGlobalShortcuts() {
        JRootPane rootPane = mainFrame.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();
        
        // Space - Toggle recording/submit answer
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "toggleRecording");
        actionMap.put("toggleRecording", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.toggleRecording();
            }
        });
        
        // Ctrl+N - Next question
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "nextQuestion");
        actionMap.put("nextQuestion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.nextQuestion();
            }
        });
        
        // Ctrl+M - Mode selector
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), "modeSelector");
        actionMap.put("modeSelector", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showModeSelector();
            }
        });
        
        // Ctrl+A - Analytics dashboard
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "analytics");
        actionMap.put("analytics", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.switchToTab(5);
            }
        });
        
        // Ctrl+Q - Quick practice
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), "quickPractice");
        actionMap.put("quickPractice", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.startQuickPractice();
            }
        });
        
        // F1 - Help
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help");
        actionMap.put("help", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showShortcutsHelp();
            }
        });
    }
    
    private void showShortcutsHelp() {
        String helpText = """
            ⌨️ Keyboard Shortcuts
            ═══════════════════════════════════
            
            Space       - Toggle Recording/Submit Answer
            Ctrl+N      - Next Question
            Ctrl+M      - Select Interview Mode
            Ctrl+A      - View Analytics
            Ctrl+Q      - Quick Practice Mode
            F1          - Show This Help
            
            Navigation:
            - Use Tab/Shift+Tab to move between elements
            - Arrow keys to navigate lists
            - Enter to activate buttons
            """;
        
        JOptionPane.showMessageDialog(
            mainFrame,
            helpText,
            "Keyboard Shortcuts",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}

