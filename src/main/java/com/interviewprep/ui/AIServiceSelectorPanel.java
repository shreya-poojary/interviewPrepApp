package com.interviewprep.ui;

import com.interviewprep.service.AIServiceManager;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel for selecting between different AI services (Ollama, Bedrock)
 */
@Slf4j
public class AIServiceSelectorPanel extends JPanel {
    
    private final MainFrame mainFrame;
    private final AIServiceManager aiServiceManager;
    
    private JComboBox<String> serviceComboBox;
    private JLabel statusLabel;
    private JButton testButton;
    private JTextArea statusArea;
    
    public AIServiceSelectorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.aiServiceManager = mainFrame.getAIServiceManager();
        
        initializeUI();
        updateServiceStatus();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("🤖 AI Service Selection"));
        
        // Top panel with controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        JLabel serviceLabel = new JLabel("Select AI Service:");
        serviceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.add(serviceLabel);
        
        // Service selection combo box
        serviceComboBox = new JComboBox<>();
        updateServiceList();
        serviceComboBox.setPreferredSize(new Dimension(200, 30));
        serviceComboBox.addActionListener(this::onServiceChanged);
        controlPanel.add(serviceComboBox);
        
        // Test button
        testButton = new JButton("🧪 Test Connection");
        testButton.setPreferredSize(new Dimension(150, 30));
        testButton.addActionListener(this::testCurrentService);
        controlPanel.add(testButton);
        
        // Status label
        statusLabel = new JLabel("Status: Checking...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        controlPanel.add(statusLabel);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Status area
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusArea.setBackground(new Color(240, 240, 240));
        statusArea.setBorder(BorderFactory.createLoweredBevelBorder());
        
        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with info
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("ℹ️ Information"));
        
        JTextArea infoArea = new JTextArea(
            "• Ollama: Free, local LLM (requires Ollama installation)\n" +
            "• Bedrock: AWS cloud LLM (requires AWS credentials)\n" +
            "• Switch between services anytime during your session\n" +
            "• Each service may have different response styles and capabilities"
        );
        infoArea.setEditable(false);
        infoArea.setBackground(getBackground());
        infoArea.setFont(new Font("Arial", Font.PLAIN, 11));
        infoPanel.add(infoArea);
        
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void updateServiceList() {
        serviceComboBox.removeAllItems();
        
        String[] availableServices = aiServiceManager.getAvailableServices();
        for (String service : availableServices) {
            serviceComboBox.addItem(service);
        }
        
        // Set current service as selected
        String currentService = aiServiceManager.getCurrentServiceName();
        if (currentService.contains("Ollama")) {
            serviceComboBox.setSelectedItem("Ollama");
        } else if (currentService.contains("Bedrock")) {
            serviceComboBox.setSelectedItem("Bedrock");
        }
    }
    
    private void onServiceChanged(ActionEvent e) {
        String selectedService = (String) serviceComboBox.getSelectedItem();
        if (selectedService != null) {
            boolean success = aiServiceManager.setPreferredService(selectedService.toLowerCase());
            if (success) {
                statusLabel.setText("Status: ✅ Switched to " + selectedService);
                log.info("Switched to AI service: {}", selectedService);
            } else {
                statusLabel.setText("Status: ❌ Failed to switch to " + selectedService);
                log.warn("Failed to switch to AI service: {}", selectedService);
            }
            updateServiceStatus();
        }
    }
    
    private void testCurrentService(ActionEvent e) {
        testButton.setEnabled(false);
        testButton.setText("Testing...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                boolean isAvailable = aiServiceManager.isCurrentServiceAvailable();
                String serviceName = aiServiceManager.getCurrentServiceName();
                
                if (isAvailable) {
                    statusLabel.setText("Status: ✅ " + serviceName + " is working");
                    statusArea.append("✅ " + serviceName + " connection test: PASSED\n");
                } else {
                    statusLabel.setText("Status: ❌ " + serviceName + " is not available");
                    statusArea.append("❌ " + serviceName + " connection test: FAILED\n");
                }
                
                updateServiceStatus();
                
            } catch (Exception ex) {
                statusLabel.setText("Status: ❌ Test failed");
                statusArea.append("❌ Connection test failed: " + ex.getMessage() + "\n");
                log.error("Service test failed", ex);
            } finally {
                testButton.setEnabled(true);
                testButton.setText("🧪 Test Connection");
            }
        });
    }
    
    private void updateServiceStatus() {
        SwingUtilities.invokeLater(() -> {
            StringBuilder status = new StringBuilder();
            status.append("=== AI Service Status ===\n\n");
            
            // Test all services
            aiServiceManager.testAllServices();
            
            String currentService = aiServiceManager.getCurrentServiceName();
            status.append("Current Service: ").append(currentService).append("\n\n");
            
            String[] availableServices = aiServiceManager.getAvailableServices();
            status.append("Available Services:\n");
            for (String service : availableServices) {
                status.append("• ").append(service).append("\n");
            }
            
            status.append("\n=== Configuration ===\n");
            status.append("• Ollama URL: http://localhost:11434\n");
            status.append("• Bedrock Region: us-east-1\n");
            status.append("• Switch services anytime during your session\n");
            
            statusArea.setText(status.toString());
        });
    }
    
    public void refreshStatus() {
        updateServiceList();
        updateServiceStatus();
    }
}
