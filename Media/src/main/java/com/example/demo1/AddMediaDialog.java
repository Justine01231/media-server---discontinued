package com.example.demo1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.Optional;

public class AddMediaDialog {
    private final Dialog<MediaManager.MediaItem> dialog;
    private final TextField titleField = new TextField();
    private final ComboBox<MediaManager.MediaType> typeCombo = new ComboBox<>();
    private final TextArea descriptionArea = new TextArea();
    private final TextField thumbnailField = new TextField();
    private final Spinner<Integer> durationSpinner = new Spinner<>(0, 999, 0);
    private final Button browseImageButton = new Button();
    private final TextField filePathField = new TextField();
    private final Button browseFileButton = new Button();
    private File selectedMediaFile = null;
    
    public AddMediaDialog() {
        dialog = new Dialog<>();
        dialog.setTitle("Add New Media");
        dialog.setHeaderText("Enter media details");
        dialog.getDialogPane().setPrefWidth(550);
        dialog.getDialogPane().setPrefHeight(480);
        
        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25));
        
        // Setup fields with better styling
        titleField.setPromptText("Enter title");
        titleField.setPrefWidth(350);
        
        typeCombo.getItems().addAll(MediaManager.MediaType.values());
        typeCombo.setValue(MediaManager.MediaType.MOVIE);
        typeCombo.setPrefWidth(350);
        
        descriptionArea.setPromptText("Enter description");
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setWrapText(true);
        
        filePathField.setPromptText("No file selected");
        filePathField.setEditable(false);
        filePathField.setPrefWidth(300);
        HBox.setHgrow(filePathField, Priority.ALWAYS);
        
        thumbnailField.setPromptText("Thumbnail URL (optional)");
        thumbnailField.setPrefWidth(300);
        HBox.setHgrow(thumbnailField, Priority.ALWAYS);
        
        durationSpinner.setEditable(true);
        durationSpinner.getValueFactory().setWrapAround(true);
        durationSpinner.setPrefWidth(100);
        
        // Setup buttons with icons
        browseFileButton.setGraphic(new FontIcon("fas-folder-open"));
        browseFileButton.setText("Browse");
        
        browseImageButton.setGraphic(new FontIcon("fas-image"));
        browseImageButton.setText("Browse");
        
        // Create file selection container
        HBox fileSelectionBox = new HBox(10);
        fileSelectionBox.setAlignment(Pos.CENTER_LEFT);
        fileSelectionBox.getChildren().addAll(filePathField, browseFileButton);
        
        // Create thumbnail selection container
        HBox thumbnailSelectionBox = new HBox(10);
        thumbnailSelectionBox.setAlignment(Pos.CENTER_LEFT);
        thumbnailSelectionBox.getChildren().addAll(thumbnailField, browseImageButton);
        
        // Add fields to grid with better labeling
        int row = 0;
        grid.add(new Label("Media File:*"), 0, row);
        grid.add(fileSelectionBox, 1, row++);
        
        grid.add(new Label("Title:*"), 0, row);
        grid.add(titleField, 1, row++);
        
        grid.add(new Label("Media Type:*"), 0, row);
        grid.add(typeCombo, 1, row++);
        
        grid.add(new Label("Description:*"), 0, row);
        grid.add(descriptionArea, 1, row++);
        
        grid.add(new Label("Thumbnail:"), 0, row);
        grid.add(thumbnailSelectionBox, 1, row++);
        
        grid.add(new Label("Duration (min):"), 0, row);
        grid.add(durationSpinner, 1, row++);
        
        // Add a note about required fields
        Label requiredNote = new Label("* Required fields");
        requiredNote.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
        grid.add(requiredNote, 1, row);
        
        // Add event handler for media file browse button
        browseFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Media File");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mkv", "*.avi", "*.mov"),
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"),
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                selectedMediaFile = selectedFile;
                filePathField.setText(selectedFile.getAbsolutePath());
                
                // Auto-fill title if it's empty
                if (titleField.getText().trim().isEmpty()) {
                    String fileName = selectedFile.getName();
                    // Remove extension
                    int lastDotIndex = fileName.lastIndexOf('.');
                    if (lastDotIndex > 0) {
                        fileName = fileName.substring(0, lastDotIndex);
                    }
                    // Replace underscores with spaces and capitalize words
                    fileName = fileName.replace('_', ' ');
                    titleField.setText(fileName);
                }
                
                // Auto-select media type based on file extension
                String extension = selectedFile.getName().toLowerCase();
                if (extension.endsWith(".mp4") || extension.endsWith(".mkv") || extension.endsWith(".avi") || extension.endsWith(".mov")) {
                    typeCombo.setValue(MediaManager.MediaType.MOVIE);
                } else if (extension.endsWith(".mp3") || extension.endsWith(".wav")) {
                    typeCombo.setValue(MediaManager.MediaType.MUSIC);
                } else if (extension.endsWith(".jpg") || extension.endsWith(".jpeg") || extension.endsWith(".png")) {
                    typeCombo.setValue(MediaManager.MediaType.PHOTO);
                }
            }
        });
        
        // Add event handler for thumbnail browse button
        browseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Thumbnail Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                thumbnailField.setText(selectedFile.toURI().toString());
            }
        });
        
        // Disable/Enable add button based on validation
        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        
        // Add styling to the dialog buttons
        addButton.getStyleClass().add("primary-button");
        
        // Validation listeners
        titleField.textProperty().addListener((observable, oldValue, newValue) -> validateForm(addButton));
        filePathField.textProperty().addListener((observable, oldValue, newValue) -> validateForm(addButton));
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> validateForm(addButton));
        
        // Set the resulting item converter
        dialog.setResultConverter(createResultConverter());
        
        dialog.getDialogPane().setContent(grid);
        
        // Apply CSS styling
        dialog.getDialogPane().getStyleClass().add("custom-dialog");
        grid.getStyleClass().add("dialog-grid");
        
        // Request focus on the file browser button by default
        browseFileButton.requestFocus();
    }
    
    private void validateForm(Button addButton) {
        boolean isValid = 
            selectedMediaFile != null && 
            !titleField.getText().trim().isEmpty() && 
            !descriptionArea.getText().trim().isEmpty();
        
        addButton.setDisable(!isValid);
    }
    
    public Optional<MediaManager.MediaItem> showAndWait() {
        return dialog.showAndWait();
    }
    
    private Callback<ButtonType, MediaManager.MediaItem> createResultConverter() {
        return dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                // Validate required fields
                if (selectedMediaFile == null || 
                    titleField.getText().trim().isEmpty() || 
                    descriptionArea.getText().trim().isEmpty()) {
                    
                    // Show warning for required fields
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Missing Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Please fill in all required fields.");
                    alert.showAndWait();
                    return null;
                }
                
                // Create the media item with the collected information
                // We can only use the parameters supported by the constructor
                MediaManager.MediaItem newItem = new MediaManager.MediaItem(
                    selectedMediaFile,
                    typeCombo.getValue(),
                    titleField.getText().trim(),
                    thumbnailField.getText().trim() // This is optional, can be empty
                );
                
                // Add to media manager
                MediaManager.getInstance().addMedia(newItem);
                
                return newItem;
            }
            return null;
        };
    }
}