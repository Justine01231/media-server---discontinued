package com.example.demo1;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class ThemeManager {
    private static ThemeManager instance;
    private static final String DARK_THEME_CSS = "dark-theme.css";
    private static final String LIGHT_THEME_CSS = "light-theme.css";
    
    private ThemeManager() {
        // Private constructor for singleton
    }
    
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public void applyTheme(String theme) {
        UserPreferences.getInstance().setTheme(theme);
    }
    
    public void applyThemeToScene(Scene scene, String theme) {
        if (scene == null) return;
        
        // Remove any existing theme
        scene.getStylesheets().remove(getClass().getResource(DARK_THEME_CSS).toExternalForm());
        scene.getStylesheets().remove(getClass().getResource(LIGHT_THEME_CSS).toExternalForm());
        
        // Apply selected theme
        if ("light".equals(theme)) {
            scene.getRoot().getStyleClass().remove("dark-theme");
            scene.getRoot().getStyleClass().add("light-theme");
            scene.getStylesheets().add(getClass().getResource(LIGHT_THEME_CSS).toExternalForm());
        } else {
            scene.getRoot().getStyleClass().remove("light-theme");
            scene.getRoot().getStyleClass().add("dark-theme");
            scene.getStylesheets().add(getClass().getResource(DARK_THEME_CSS).toExternalForm());
        }
    }
    
    public void applyThemeToNode(Parent node, String theme) {
        if (node == null) return;
        
        // Apply theme class
        if ("light".equals(theme)) {
            node.getStyleClass().remove("dark-theme");
            node.getStyleClass().add("light-theme");
        } else {
            node.getStyleClass().remove("light-theme");
            node.getStyleClass().add("dark-theme");
        }
    }
}