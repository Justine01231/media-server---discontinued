package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckbox;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerLink;

    // This should be replaced with a proper database in a real application
    private static final Set<String> registeredUsers = new HashSet<>();
    private static String currentUser;

    // Get user preferences for theme
    private final UserPreferences prefs = UserPreferences.getInstance();

    // Method to register a user (call this from RegisterController)
    public static void registerUser(String username) {
        registeredUsers.add(username);
        currentUser = username;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void clearCurrentUser() {
        currentUser = null;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // Simple validation - in real app, you'd check against a database
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter username and password", Alert.AlertType.ERROR);
            return;
        }
        
        // Check if user exists (simplified authentication)
        if (!registeredUsers.contains(username)) {
            showAlert("Error", "User not found. Please register first.", Alert.AlertType.ERROR);
            return;
        }
        
        // Set the current user on successful login
        currentUser = username;
        
        // Navigate to dashboard via the loginSuccessful method
        loginSuccessful();
    }

    @FXML
    private void switchToRegister() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) loginButton.getScene().getWindow();
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            
            // Apply theme to register page
            String currentTheme = prefs.getTheme();
            ThemeManager.getInstance().applyThemeToScene(scene, currentTheme);
            
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load register page.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loginSuccessful() {
        try {
            // Load the dashboard view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Apply base styles and theme
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            
            // Apply current theme
            String currentTheme = prefs.getTheme();
            ThemeManager.getInstance().applyThemeToScene(scene, currentTheme);
            
            stage.setScene(scene);
            stage.setTitle("Media Server - Dashboard");
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}