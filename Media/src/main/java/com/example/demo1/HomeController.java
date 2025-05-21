package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class HomeController {
    @FXML private Label movieCountLabel;
    @FXML private Label tvShowCountLabel;
    @FXML private Label episodeCountLabel;
    @FXML private Label songCountLabel;
    @FXML private TextField searchField;
    @FXML private HBox continueWatchingContainer;
    @FXML private HBox recentlyAddedContainer;
    
    // Add these to match the fx:ids we added
    @FXML private VBox moviesStatCard;
    @FXML private VBox tvShowsStatCard;
    @FXML private VBox episodesStatCard;
    @FXML private VBox songsStatCard;
    
    private final UserPreferences prefs = UserPreferences.getInstance();
    private final ThemeManager themeManager = ThemeManager.getInstance();
    
    @FXML
    public void initialize() {
        // Apply current theme to all stat cards
        String currentTheme = prefs.getTheme();
        themeManager.applyThemeToNode(moviesStatCard, currentTheme);
        themeManager.applyThemeToNode(tvShowsStatCard, currentTheme);
        themeManager.applyThemeToNode(episodesStatCard, currentTheme);
        themeManager.applyThemeToNode(songsStatCard, currentTheme);
        
        // Load media statistics
        updateMediaCounts();
        
        // Load continue watching section
        loadContinueWatching();
        
        // Load recently added media
        loadRecentlyAdded();
    }
    
    private void updateMediaCounts() {
        // Using placeholder values since MediaLibrary doesn't have these methods
        // In a real application, these would come from a media database
        int movieCount = 42;
        int tvShowCount = 15;
        int episodeCount = 237;
        int songCount = 128;
        
        // Update labels
        movieCountLabel.setText(String.valueOf(movieCount));
        tvShowCountLabel.setText(String.valueOf(tvShowCount));
        episodeCountLabel.setText(String.valueOf(episodeCount));
        songCountLabel.setText(String.valueOf(songCount));
    }
    
    private void loadContinueWatching() {
        // This would be implemented to load media the user was last watching
        // For now, we'll leave it empty or add placeholder content
    }
    
    private void loadRecentlyAdded() {
        // This would be implemented to load recently added media
        // For now, we'll leave it empty or add placeholder content
    }
    
    @FXML
    public void openNotifications() {
        // Show notifications panel or dialog
        showAlert("Notifications", "No new notifications", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    public void addNewMedia() {
        try {
            AddMediaDialog dialog = new AddMediaDialog();
            dialog.showAndWait().ifPresent(mediaItem -> {
                // Refresh data after adding media
                updateMediaCounts();
                loadRecentlyAdded();
            });
        } catch (Exception e) {
            showAlert("Error", "Could not open add media dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}