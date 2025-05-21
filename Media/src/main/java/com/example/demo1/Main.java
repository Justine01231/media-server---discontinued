package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load the initial FXML (login screen)
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        
        // Apply base stylesheet
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        
        // Get user's preferred theme and apply it
        UserPreferences prefs = UserPreferences.getInstance();
        String currentTheme = prefs.getTheme();
        ThemeManager.getInstance().applyThemeToScene(scene, currentTheme);
        
        // Configure and show the stage
        stage.setTitle("Media Server - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Initialize any app services here
        
        // Register some test users (for demo purposes only)
        LoginController.registerUser("admin");
        
        // Launch the JavaFX application
        launch();
    }
}