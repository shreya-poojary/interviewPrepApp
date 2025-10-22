package com.interviewprep.ui;

import com.interviewprep.model.InterviewMode;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

@Slf4j
public class InterviewModeSelectorPanel extends JPanel {
    private final MainFrame mainFrame;
    private ButtonGroup modeGroup;
    private JTextArea descriptionArea;
    
    public InterviewModeSelectorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("🎯 Select Interview Mode");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel modesPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        modeGroup = new ButtonGroup();
        
        for (InterviewMode mode : InterviewMode.values()) {
            JPanel modeCard = createModeCard(mode);
            modesPanel.add(modeCard);
        }
        
        add(modesPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        descriptionArea = new JTextArea(3, 40);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Mode Description"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        bottomPanel.add(descriptionArea, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> mainFrame.switchToTab(2));
        actionPanel.add(backButton);
        
        JButton startButton = new JButton("Start Interview →");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setPreferredSize(new Dimension(200, 40));
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> mainFrame.switchToTab(4));
        actionPanel.add(startButton);
        
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
        
        setMode(InterviewMode.PRACTICE);
    }
    
    private JPanel createModeCard(InterviewMode mode) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);
        
        JRadioButton radioButton = new JRadioButton(mode.getDisplayName());
        radioButton.setFont(new Font("Arial", Font.BOLD, 14));
        radioButton.setOpaque(false);
        radioButton.addActionListener(e -> setMode(mode));
        modeGroup.add(radioButton);
        
        JLabel iconLabel = new JLabel(mode.getIcon(), JLabel.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        
        JPanel content = new JPanel(new BorderLayout(5, 5));
        content.setOpaque(false);
        content.add(radioButton, BorderLayout.NORTH);
        content.add(iconLabel, BorderLayout.CENTER);
        
        JLabel timeLabel = new JLabel(mode.getFormattedTimeLimit(), JLabel.CENTER);
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        content.add(timeLabel, BorderLayout.SOUTH);
        
        card.add(content, BorderLayout.CENTER);
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                radioButton.setSelected(true);
                setMode(mode);
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(240, 248, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!radioButton.isSelected()) {
                    card.setBackground(Color.WHITE);
                }
            }
        });
        
        return card;
    }
    
    private void setMode(InterviewMode mode) {
        descriptionArea.setText(mode.getDescription());
        mainFrame.setSelectedMode(mode);
    }
}

