package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DashboardController {
    @FXML private Label usernameLabel;
    @FXML private Button homeButton;
    @FXML private Button moviesButton;
    @FXML private Button tvShowsButton;
    @FXML private Button musicButton;
    @FXML private Button photosButton;
    @FXML private Button settingsButton;
    @FXML private Button logoutButton;
    @FXML private VBox mainContentArea;
    @FXML private ImageView profileImageView;
    @FXML private FontIcon profileDefaultIcon;
    @FXML private Circle profileCircle;
    @FXML private StackPane profileImageContainer;
    
    private BorderPane borderPane;
    private final UserPreferences prefs = UserPreferences.getInstance();
    private final String userDataFolder = System.getProperty("user.home") + File.separator + ".mediaserver";
    
    @FXML
    public void initialize() {
        // Store a reference to the BorderPane
        borderPane = (BorderPane) mainContentArea.getParent();
        
        // Set username from login
        String currentUser = LoginController.getCurrentUser();
        if (currentUser != null && !currentUser.isEmpty()) {
            usernameLabel.setText(currentUser);
        } else {
            // Use the one from preferences if available
            String username = prefs.getUsername();
            if (username != null && !username.isEmpty()) {
                usernameLabel.setText(username);
            }
        }
        
        // Ensure user data folder exists
        try {
            Files.createDirectories(Paths.get(userDataFolder));
        } catch (IOException e) {
            System.err.println("Could not create user data folder: " + e.getMessage());
        }
        
        // Load profile image if available
        loadProfileImage();
        
        // Set home as the initial view
        navigateToHome();
        
        // Add cursor styling to profile image container
        profileImageContainer.getStyleClass().add("clickable");
    }
    
    @FXML
    public void viewProfileImage() {
        // Show image viewer if there's an image
        if (profileImageView.isVisible() && profileImageView.getImage() != null) {
            showEnhancedImageViewer(profileImageView.getImage());
        }
    }
    
    private void showEnhancedImageViewer(Image image) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        
        // Create a larger ImageView
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(400); // Larger size for viewing
        
        // Create a title
        Label nameLabel = new Label(usernameLabel.getText());
        nameLabel.getStyleClass().add("image-viewer-name");
        
        // Create a close button
        Button closeButton = new Button("×");
        closeButton.getStyleClass().add("image-viewer-close-button");
        closeButton.setOnAction(e -> stage.close());
        
        // Edit profile button that redirects to settings
        Button editProfileButton = new Button("Edit Profile");
        editProfileButton.getStyleClass().add("edit-profile-button");
        editProfileButton.setOnAction(e -> {
            stage.close();
            navigateToSettings();
        });
        
        // Layout
        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(imageView, nameLabel, editProfileButton);
        
        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("image-viewer-dialog");
        layout.setCenter(contentBox);
        layout.setTop(closeButton);
        BorderPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        BorderPane.setMargin(closeButton, new Insets(10));
        
        Scene scene = new Scene(layout, 500, 550);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        
        stage.setScene(scene);
        stage.show();
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
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading profile image: " + e.getMessage());
            // If there's an error, we'll continue showing the default icon
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
    }
    
    @FXML
    public void handleBrandClick() {
        navigateToHome();
    }
    
    @FXML
    public void navigateToHome() {
        loadContentView("Home.fxml");
        setActiveButton(homeButton);
    }
    
    @FXML
    public void navigateToMovies() {
        loadContentView("Movies.fxml");
        setActiveButton(moviesButton);
    }
    
    @FXML
    public void navigateToTVShows() {
        // TODO: Implement TV Shows view
        showAlert("Coming Soon", "TV Shows section is coming soon!", Alert.AlertType.INFORMATION);
        setActiveButton(tvShowsButton);
    }
    
    @FXML
    public void navigateToMusic() {
        // TODO: Implement Music view
        showAlert("Coming Soon", "Music section is coming soon!", Alert.AlertType.INFORMATION);
        setActiveButton(musicButton);
    }
    
    @FXML
    public void navigateToPhotos() {
        // TODO: Implement Photos view
        showAlert("Coming Soon", "Photos section is coming soon!", Alert.AlertType.INFORMATION);
        setActiveButton(photosButton);
    }
    
    @FXML
    public void openSyncDialog() {
        // TODO: Implement sync dialog
        showAlert("Coming Soon", "Sync functionality is coming soon!", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    public void openMetadataManager() {
        // TODO: Implement metadata manager
        showAlert("Coming Soon", "Metadata manager is coming soon!", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    public void openSettings() {
        navigateToSettings();
    }
    
    private void navigateToSettings() {
        loadContentView("Settings.fxml");
        setActiveButton(settingsButton);
    }
    
    @FXML
    public void openNotifications() {
        // TODO: Implement notifications panel
        showAlert("Notifications", "No new notifications", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    public void addNewMedia() {
        AddMediaDialog dialog = new AddMediaDialog();
        dialog.showAndWait().ifPresent(mediaItem -> {
            // Refresh home view after adding media
            if (homeButton.getStyleClass().contains("active-nav-button")) {
                navigateToHome();
            }
        });
    }
    
    @FXML
    public void handleLogout() {
        try {
            LoginController.clearCurrentUser();
            
            // Navigate back to login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not return to login screen: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void loadContentView(String fxmlFile) {
        try {
            // Load the specified FXML into the content area
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent contentView = loader.load();
            
            // Apply current theme to the new content
            ThemeManager.getInstance().applyThemeToNode(contentView, prefs.getTheme());
            
            // Store a reference to the BorderPane if not already done
            if (borderPane == null) {
                borderPane = (BorderPane) mainContentArea.getParent();
            }
            
            // Replace the current content with the new view
            borderPane.setCenter(contentView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load view: " + fxmlFile + "\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void setActiveButton(Button activeButton) {
        // Remove active class from all buttons
        homeButton.getStyleClass().remove("active-nav-button");
        moviesButton.getStyleClass().remove("active-nav-button");
        tvShowsButton.getStyleClass().remove("active-nav-button");
        musicButton.getStyleClass().remove("active-nav-button");
        photosButton.getStyleClass().remove("active-nav-button");
        settingsButton.getStyleClass().remove("active-nav-button");
        
        // Add active class to selected button
        activeButton.getStyleClass().add("active-nav-button");
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Add this method to the DashboardController
    public void refreshProfileImage() {
        // Reload the profile image from preferences
        loadProfileImage();
    }
    
    public void syncWithPreferences() {
        // Update username from preferences
        String username = prefs.getUsername();
        if (username != null && !username.isEmpty()) {
            usernameLabel.setText(username);
        }
        
        // Reload profile image
        loadProfileImage();
    }
}