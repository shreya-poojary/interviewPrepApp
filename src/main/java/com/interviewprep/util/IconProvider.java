package com.interviewprep.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;

/**
 * Cross-platform icon provider with intelligent fallback system
 */
public class IconProvider {
    
    private static final Map<String, String> ICONS = new HashMap<>();
    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");
    private static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
    private static final boolean HAS_EMOJI_SUPPORT = checkEmojiSupport();
    
    static {
        initializeIcons();
    }
    
    private static void initializeIcons() {
        if (IS_WINDOWS) {
            // Windows - use simple Unicode symbols that work better
            ICONS.put("TARGET", "★");           // Star
            ICONS.put("DOCUMENT", "📄");         // Document emoji (usually works)
            ICONS.put("CLIPBOARD", "📋");        // Clipboard emoji (usually works)
            ICONS.put("CHECK", "✓");             // Check mark
            ICONS.put("MICROPHONE", "🎤");       // Microphone emoji (usually works)
            ICONS.put("CHART", "📊");            // Chart emoji (usually works)
            ICONS.put("ROBOT", "🤖");            // Robot emoji (usually works)
            ICONS.put("INFO", "ℹ");              // Info symbol
            ICONS.put("WARNING", "⚠");           // Warning symbol
            ICONS.put("CLOCK", "⏱");            // Clock symbol
            ICONS.put("KEYBOARD", "⌨");          // Keyboard symbol
            ICONS.put("PLAY", "▶");              // Play symbol
            ICONS.put("STOP", "■");              // Stop symbol
            ICONS.put("VIDEO", "📹");            // Video emoji (usually works)
            ICONS.put("RECORDING", "●");         // Recording dot
        } else {
            // macOS/Linux - use full emojis
            ICONS.put("TARGET", "🎯");
            ICONS.put("DOCUMENT", "📄");
            ICONS.put("CLIPBOARD", "📋");
            ICONS.put("CHECK", "✅");
            ICONS.put("MICROPHONE", "🎤");
            ICONS.put("CHART", "📊");
            ICONS.put("ROBOT", "🤖");
            ICONS.put("INFO", "ℹ️");
            ICONS.put("WARNING", "⚠️");
            ICONS.put("CLOCK", "⏱️");
            ICONS.put("KEYBOARD", "⌨️");
            ICONS.put("PLAY", "▶️");
            ICONS.put("STOP", "■");
            ICONS.put("VIDEO", "📹");
            ICONS.put("RECORDING", "●");
        }
    }
    
    /**
     * Check if the system supports emoji rendering
     */
    private static boolean checkEmojiSupport() {
        if (IS_MAC) {
            return true; // macOS has excellent emoji support
        }
        
        if (IS_WINDOWS) {
            // Check for emoji font support on Windows
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontNames = ge.getAvailableFontFamilyNames();
            
            for (String fontName : fontNames) {
                String lowerFontName = fontName.toLowerCase();
                if (lowerFontName.contains("segoe ui emoji") || 
                    lowerFontName.contains("noto color emoji") ||
                    lowerFontName.contains("apple color emoji") ||
                    lowerFontName.contains("twemoji")) {
                    return true;
                }
            }
            
            // Test with a simple emoji to see if it renders
            try {
                Font testFont = new Font("Segoe UI", Font.PLAIN, 12);
                // Test with a simple Unicode symbol that should work on Windows
                return testFont.canDisplay('★'); // Test with star symbol
            } catch (Exception e) {
                return false;
            }
        }
        
        return true; // Linux generally has good emoji support
    }
    
    /**
     * Get the recommended font for the current platform
     */
    public static Font getRecommendedFont() {
        if (IS_WINDOWS) {
            if (HAS_EMOJI_SUPPORT) {
                return new Font("Segoe UI", Font.PLAIN, 12);
            } else {
                return new Font("Segoe UI", Font.PLAIN, 12);
            }
        } else if (IS_MAC) {
            return new Font("SF Pro Display", Font.PLAIN, 12);
        } else {
            return new Font("DejaVu Sans", Font.PLAIN, 12);
        }
    }
    
    /**
     * Get an icon by name
     */
    public static String getIcon(String name) {
        return ICONS.getOrDefault(name, "");
    }
    
    /**
     * Get a title with icon
     */
    public static String getTitle(String iconName, String title) {
        String icon = getIcon(iconName);
        return icon.isEmpty() ? title : icon + " " + title;
    }
    
    /**
     * Get a button text with icon
     */
    public static String getButtonText(String iconName, String text) {
        String icon = getIcon(iconName);
        return icon.isEmpty() ? text : icon + " " + text;
    }
    
    /**
     * Get a status message with icon
     */
    public static String getStatusMessage(String iconName, String message) {
        String icon = getIcon(iconName);
        return icon.isEmpty() ? message : icon + " " + message;
    }
    
    /**
     * Check if emoji support is available
     */
    public static boolean supportsEmoji() {
        return HAS_EMOJI_SUPPORT;
    }
    
    /**
     * Get platform information for debugging
     */
    public static String getPlatformInfo() {
        return String.format("OS: %s, Emoji Support: %s, Font: %s", 
            System.getProperty("os.name"), 
            HAS_EMOJI_SUPPORT, 
            getRecommendedFont().getFontName());
    }
}
