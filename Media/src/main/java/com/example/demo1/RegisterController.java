package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private Button registerButton;
    @FXML private Hyperlink loginLink;
    @FXML private FontIcon passwordVisibilityIcon;
    @FXML private FontIcon confirmPasswordVisibilityIcon;
    @FXML private Text lengthRequirement;
    @FXML private Text numberRequirement;
    @FXML private Text uppercaseRequirement;
    
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;
    
    private final UserPreferences prefs = UserPreferences.getInstance();

    @FXML
    public void initialize() {
        // Set initial style for requirements (not met)
        lengthRequirement.getStyleClass().add("requirement-not-met");
        numberRequirement.getStyleClass().add("requirement-not-met");
        uppercaseRequirement.getStyleClass().add("requirement-not-met");
        
        // Add listeners to password fields for live validation
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordRequirements(newVal);
            passwordTextField.setText(newVal);
        });
        
        passwordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordRequirements(newVal);
            passwordField.setText(newVal);
        });
        
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            confirmPasswordTextField.setText(newVal);
        });
        
        confirmPasswordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            confirmPasswordField.setText(newVal);
        });
    }
    
    private void updatePasswordRequirements(String password) {
        // Check length requirement (8+ characters)
        if (password.length() >= 8) {
            lengthRequirement.getStyleClass().remove("requirement-not-met");
            lengthRequirement.getStyleClass().add("requirement-met");
        } else {
            lengthRequirement.getStyleClass().remove("requirement-met");
            lengthRequirement.getStyleClass().add("requirement-not-met");
        }
        
        // Update the numberRequirement validation in the updatePasswordRequirements method
        // Check for at least one special character
        if (password.matches(".*[^a-zA-Z0-9].*")) {
            numberRequirement.getStyleClass().remove("requirement-not-met");
            numberRequirement.getStyleClass().add("requirement-met");
        } else {
            numberRequirement.getStyleClass().remove("requirement-met");
            numberRequirement.getStyleClass().add("requirement-not-met");
        }
        
        // Check for at least one uppercase letter
        if (password.matches(".*[A-Z].*")) {
            uppercaseRequirement.getStyleClass().remove("requirement-not-met");
            uppercaseRequirement.getStyleClass().add("requirement-met");
        } else {
            uppercaseRequirement.getStyleClass().remove("requirement-met");
            uppercaseRequirement.getStyleClass().add("requirement-not-met");
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = getPasswordText();
        String confirmPassword = getConfirmPasswordText();
        
        // Simple validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showAlert("Error", "Please enter a valid email address", Alert.AlertType.ERROR);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match", Alert.AlertType.ERROR);
            return;
        }
        
        // Check password requirements
        if (password.length() < 8) {
            showAlert("Error", "Password must be at least 8 characters long", Alert.AlertType.ERROR);
            return;
        }
        
        // Also update the validation in handleRegister method
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            showAlert("Error", "Password must contain at least one special character", Alert.AlertType.ERROR);
            return;
        }
        
        if (!password.matches(".*[A-Z].*")) {
            showAlert("Error", "Password must contain at least one uppercase letter", Alert.AlertType.ERROR);
            return;
        }
        
        // Register the user
        LoginController.registerUser(username);
        prefs.setUsername(username);
        prefs.setEmail(email);
        prefs.setPassword(password);
        
        showAlert("Success", "Registration successful!", Alert.AlertType.INFORMATION);
        
        // Navigate back to login
        switchToLogin();
    }

    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        
        if (passwordVisible) {
            // Show password (show TextField, hide PasswordField)
            passwordField.setVisible(false);
            passwordTextField.setVisible(true);
            
            // Change icon to eye-slash
            passwordVisibilityIcon.setIconLiteral("fas-eye-slash");
        } else {
            // Hide password (show PasswordField, hide TextField)
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
            
            // Change icon to eye
            passwordVisibilityIcon.setIconLiteral("fas-eye");
        }
    }
    
    @FXML
    private void toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible;
        
        if (confirmPasswordVisible) {
            // Show password
            confirmPasswordField.setVisible(false);
            confirmPasswordTextField.setVisible(true);
            
            // Change icon to eye-slash
            confirmPasswordVisibilityIcon.setIconLiteral("fas-eye-slash");
        } else {
            // Hide password
            confirmPasswordField.setVisible(true);
            confirmPasswordTextField.setVisible(false);
            
            // Change icon to eye
            confirmPasswordVisibilityIcon.setIconLiteral("fas-eye");
        }
    }
    
    // Helper methods to get the current password text regardless of visibility state
    private String getPasswordText() {
        return passwordVisible ? passwordTextField.getText() : passwordField.getText();
    }
    
    private String getConfirmPasswordText() {
        return confirmPasswordVisible ? confirmPasswordTextField.getText() : confirmPasswordField.getText();
    }

    @FXML
    private void switchToLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) registerButton.getScene().getWindow();
            
            // Apply base stylesheet
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            
            // Apply theme
            String currentTheme = prefs.getTheme();
            ThemeManager.getInstance().applyThemeToScene(scene, currentTheme);
            
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load login page.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}