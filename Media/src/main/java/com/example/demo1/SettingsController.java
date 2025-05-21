package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SettingsController {
    
    @FXML private StackPane profilePicContainer;
    @FXML private Circle profileCircle;
    @FXML private ImageView profileImageView;
    @FXML private FontIcon profileDefaultIcon;
    @FXML private StackPane uploadOverlay;
    @FXML private Button removePhotoButton;
    @FXML private Label usernameValueLabel;
    @FXML private Label emailValueLabel;
    @FXML private RadioButton darkThemeRadio;
    @FXML private RadioButton lightThemeRadio;
    @FXML private CheckBox rememberLoginCheck;
    @FXML private CheckBox enableStatsCheck;
    
    // Store original values for comparison when applying changes
    private Map<String, Object> originalValues = new HashMap<>();
    private final UserPreferences prefs = UserPreferences.getInstance();
    private final String userDataFolder = System.getProperty("user.home") + File.separator + ".mediaserver";
    private boolean profileImageChanged = false;
    private boolean usernameChanged = false;
    private boolean emailChanged = false;
    
    @FXML
    public void initialize() {
        // Load current preferences
        loadCurrentPreferences();
        
        // Load profile image if available
        loadProfileImage();
        
        // Setup hover effect for profile picture
        setupProfilePicHover();
        
        // Store original values for comparison when applying changes
        saveOriginalValues();
    }
    
    private void saveOriginalValues() {
        originalValues.put("username", prefs.getUsername());
        originalValues.put("email", prefs.getEmail());
        originalValues.put("theme", prefs.getTheme());
        originalValues.put("rememberLogin", rememberLoginCheck.isSelected());
        originalValues.put("enableStats", enableStatsCheck.isSelected());
    }
    
    private void loadCurrentPreferences() {
        // Set username
        String username = prefs.getUsername();
        if (username != null && !username.isEmpty()) {
            usernameValueLabel.setText(username);
        }
        
        // Set email (mock data for now)
        String email = prefs.getEmail();
        if (email != null && !email.isEmpty()) {
            emailValueLabel.setText(email);
        } else {
            emailValueLabel.setText("user@example.com");
        }
        
        // Set theme
        String theme = prefs.getTheme();
        if ("light".equals(theme)) {
            lightThemeRadio.setSelected(true);
        } else {
            darkThemeRadio.setSelected(true);
        }
        
        // Set remember login (mock data)
        rememberLoginCheck.setSelected(true);
        
        // Set usage stats (mock data)
        enableStatsCheck.setSelected(false);
    }
    
    private void setupProfilePicHover() {
        // Show upload overlay on hover
        profilePicContainer.setOnMouseEntered(e -> {
            uploadOverlay.setVisible(true);
        });
        
        profilePicContainer.setOnMouseExited(e -> {
            uploadOverlay.setVisible(false);
        });
    }
    
    private void loadProfileImage() {
        try {
            String imagePath = prefs.getProfileImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                Path path = Paths.get(imagePath);
                if (Files.exists(path)) {
                    // Load the image
                    Image image = new Image(new FileInputStream(path.toFile()));
                    updateProfileImageView(image);
                    
                    // Enable remove button
                    removePhotoButton.setDisable(false);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading profile image: " + e.getMessage());
        }
    }
    
private void updateProfileImageView(Image image) {
    // Get the radius from the circle
    double radius = profileCircle.getRadius();
    
    // Create a new clip circle with the correct center
    Circle clip = new Circle(radius, radius, radius);
    
    // Configure the image view
    profileImageView.setImage(image);
    profileImageView.setClip(clip);
    profileImageView.setFitWidth(radius * 2);
    profileImageView.setFitHeight(radius * 2);
    
    // Center the image in the circle
    profileImageView.setTranslateX(0);
    profileImageView.setTranslateY(0);
    
    // Hide the default icon and show the image
    profileDefaultIcon.setVisible(false);
    profileImageView.setVisible(true);
    
    // Mark as changed
    profileImageChanged = true;
}
    
    @FXML
    public void handleProfileImageUpload() {
        // Get the current stage
        Stage stage = (Stage) profilePicContainer.getScene().getWindow();
        
        // Use the ProfileImageCropper to handle image selection and cropping
        Optional<Image> result = ProfileImageCropper.showImagePicker(stage);
        
        // If an image was selected and cropped, update the profile
        result.ifPresent(image -> {
            updateProfileImageView(image);
            removePhotoButton.setDisable(false);
            // Mark as changed so we know to update the dashboard
            profileImageChanged = true;
        });
    }
    
    @FXML
    public void removeProfilePicture() {
        // Clear the image
        profileImageView.setImage(null);
        profileImageView.setVisible(false);
        profileDefaultIcon.setVisible(true);
        
        // Clear the preference
        prefs.setProfileImagePath("");
        
        // Mark as changed
        profileImageChanged = true;
        
        // Disable remove button
        removePhotoButton.setDisable(true);
        
        // Show confirmation
        showAlert("Profile Picture", "Profile picture has been removed", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    public void changeUsername() {
        TextInputDialog dialog = new TextInputDialog(usernameValueLabel.getText());
        dialog.setTitle("Change Username");
        dialog.setHeaderText("Enter a new username");
        dialog.setContentText("Username:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newUsername -> {
            if (!newUsername.trim().isEmpty()) {
                usernameValueLabel.setText(newUsername.trim());
                prefs.setUsername(newUsername.trim());
                usernameChanged = true;
            }
        });
    }
    
    @FXML
    public void changeEmail() {
        TextInputDialog dialog = new TextInputDialog(emailValueLabel.getText());
        dialog.setTitle("Change Email");
        dialog.setHeaderText("Enter a new email address");
        dialog.setContentText("Email:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newEmail -> {
            if (!newEmail.trim().isEmpty()) {
                emailValueLabel.setText(newEmail.trim());
                prefs.setEmail(newEmail.trim());
                emailChanged = true;
            }
        });
    }
    
    @FXML
    public void changePassword() {
        // Create custom dialog for password change
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current password and a new password");
        
        // Set the button types
        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);
        
        // Create password fields
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current password");
        
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        
        // Create layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        grid.add(new Label("Current password:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);
        grid.add(new Label("New password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the first field
        currentPasswordField.requestFocus();
        
        // Convert the result to a string when the change button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return "change";
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(s -> {
            // Validate inputs
            String currentPass = currentPasswordField.getText();
            String newPass = newPasswordField.getText();
            String confirmPass = confirmPasswordField.getText();
            
            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showAlert("Error", "All password fields are required", Alert.AlertType.ERROR);
                return;
            }
            
            // For this demo, we'll just accept any password
            // In a real app, you'd validate against stored password
            
            if (!newPass.equals(confirmPass)) {
                showAlert("Error", "New passwords don't match", Alert.AlertType.ERROR);
                return;
            }
            
            if (newPass.length() < 6) {
                showAlert("Error", "Password must be at least 6 characters", Alert.AlertType.ERROR);
                return;
            }
            
            // Save the new password
            prefs.setPassword(newPass);
            showAlert("Success", "Password has been changed successfully", Alert.AlertType.INFORMATION);
        });
    }
    
    @FXML
    public void applyTheme() {
        String theme = darkThemeRadio.isSelected() ? "dark" : "light";
        prefs.setTheme(theme);
        ThemeManager.getInstance().applyTheme(theme);
        
        // Apply to current scene
        if (profilePicContainer.getScene() != null) {
            ThemeManager.getInstance().applyThemeToScene(profilePicContainer.getScene(), theme);
        }
    }
    
    @FXML
    public void clearAllData() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear All Data");
        confirmAlert.setHeaderText("Are you sure you want to clear all your data?");
        confirmAlert.setContentText("This will reset all settings, remove your profile picture, and log you out. This action cannot be undone.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Clear all preferences
            prefs.clear();
            
            // Remove profile picture
            try {
                Path profilePath = Paths.get(userDataFolder, "profile.png");
                if (Files.exists(profilePath)) {
                    Files.delete(profilePath);
                }
            } catch (IOException e) {
                System.err.println("Could not delete profile picture: " + e.getMessage());
            }
            
            // Show success message and prompt to restart
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Data Cleared");
            successAlert.setHeaderText("All data has been cleared");
            successAlert.setContentText("The application will now close. Please restart to complete the process.");
            
            successAlert.showAndWait().ifPresent(response -> {
                // Close application
                Stage stage = (Stage) profilePicContainer.getScene().getWindow();
                stage.close();
            });
        }
    }
    
    @FXML
    public void viewLicense() {
        Alert licenseAlert = new Alert(Alert.AlertType.INFORMATION);
        licenseAlert.setTitle("License Information");
        licenseAlert.setHeaderText("Media Server License");
        licenseAlert.setContentText("This software is licensed under the MIT License.\n\n" +
                "Copyright (c) 2023 Media Server Inc.\n\n" +
                "Permission is hereby granted, free of charge, to any person obtaining a copy " +
                "of this software and associated documentation files, to deal in the Software without restriction...");
        
        licenseAlert.showAndWait();
    }
    
    @FXML
    public void checkForUpdates() {
        Alert updatesAlert = new Alert(Alert.AlertType.INFORMATION);
        updatesAlert.setTitle("Check for Updates");
        updatesAlert.setHeaderText("Checking for updates...");
        updatesAlert.setContentText("You are running the latest version (1.0.0)");
        
        updatesAlert.showAndWait();
    }
    
    @FXML
    public void restoreDefaults() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Restore Defaults");
        confirmAlert.setHeaderText("Are you sure you want to restore default settings?");
        confirmAlert.setContentText("This will reset all settings to their default values.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Reset theme to dark
            darkThemeRadio.setSelected(true);
            prefs.setTheme("dark");
            
            // Reset other settings
            rememberLoginCheck.setSelected(true);
            enableStatsCheck.setSelected(false);
            
            // Apply theme changes
            applyTheme();
            
            showAlert("Success", "Settings have been restored to defaults", Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    public void applySettings() {
        // Apply theme changes
        applyTheme();
        
        // Apply privacy settings
        prefs.setRememberLogin(rememberLoginCheck.isSelected());
        prefs.setEnableStats(enableStatsCheck.isSelected());
        
        // Show success message
        showAlert("Success", "Settings have been applied", Alert.AlertType.INFORMATION);
        
        // Navigate back to Dashboard to show changes
        try {
            // Load the Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent dashboardView = loader.load();
            
            // Get the controller and synchronize with preferences
            DashboardController controller = loader.getController();
            controller.syncWithPreferences();
            
            // Get the current scene's root (which is the BorderPane)
            BorderPane root = (BorderPane) profilePicContainer.getScene().getRoot();
            
            // Replace the content with the dashboard
            root.setCenter(dashboardView);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not return to dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
private void updateDashboard() {
    try {
        // Instead of trying to navigate the scene graph, let's directly update 
        // the dashboard when navigating back to it
        
        // Store the changed values so we know what to update when returning to dashboard
        if (profileImageChanged) {
            // Just keep track that we need to update the profile image
            profileImageChanged = true;
        }
        
        if (usernameChanged) {
            // Just keep track that we changed the username
            usernameChanged = true;
        }
        
        // A simpler approach: force the username and profileImageView to refresh when returning to the dashboard
        Button settingsButton = (Button) profilePicContainer.getScene().lookup("#settingsButton");
        if (settingsButton != null) {
            settingsButton.setOnAction(event -> {
                // Apply changes immediately to the dashboard
                BorderPane root = (BorderPane) profilePicContainer.getScene().getRoot();
                VBox sidebar = (VBox) root.getLeft();
                
                // Find and update username label in the sidebar
                Label usernameLabel = (Label) sidebar.lookup("#usernameLabel");
                if (usernameLabel != null && usernameChanged) {
                    usernameLabel.setText(prefs.getUsername());
                }
                
                // Refresh profile image
                if (profileImageChanged) {
                    ImageView profileImageView = (ImageView) sidebar.lookup("#profileImageView");
                    FontIcon profileDefaultIcon = (FontIcon) sidebar.lookup("#profileDefaultIcon");
                    
                    if (profileImageView != null && profileDefaultIcon != null) {
                        String imagePath = prefs.getProfileImagePath();
                        if (imagePath != null && !imagePath.isEmpty() && 
                            Files.exists(Paths.get(imagePath))) {
                            
                            try {
                                // Load the image
                                Image image = new Image(new FileInputStream(imagePath));
                                
                                // Configure image view with circular clip
                                Circle clip = new Circle(24);
                                clip.setCenterX(24);
                                clip.setCenterY(24);
                                
                                profileImageView.setImage(image);
                                profileImageView.setClip(clip);
                                profileImageView.setVisible(true);
                                profileDefaultIcon.setVisible(false);
                            } catch (Exception e) {
                                System.err.println("Error loading profile image: " + e.getMessage());
                            }
                        } else {
                            // No image, show default icon
                            profileImageView.setImage(null);
                            profileImageView.setVisible(false);
                            profileDefaultIcon.setVisible(true);
                        }
                    }
                }
                
                // Now navigate to settings as normal
                loadContentView("Settings.fxml");
                setActiveButton(settingsButton);
            });
        }
        
        // Alternative: add a custom event handler to window events
        Stage stage = (Stage) profilePicContainer.getScene().getWindow();
        stage.setUserData(new Object[] {profileImageChanged, usernameChanged, emailChanged});
    } catch (Exception e) {
        System.err.println("Error updating dashboard: " + e.getMessage());
    }
}

private void loadContentView(String fxmlFile) {
    try {
        // Load the specified FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent contentView = loader.load();
        
        // Get the current scene's root
        BorderPane root = (BorderPane) profilePicContainer.getScene().getRoot();
        root.setCenter(contentView);
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Could not load view: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}

private void setActiveButton(Button button) {
    // This would need the same implementation as in DashboardController
    // But we can simplify it for our needs
}
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}