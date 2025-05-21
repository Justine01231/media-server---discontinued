package com.example.demo1;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class ProfileImageCropper {
    // Mouse position tracking variables
    private static double mouseAnchorX;
    private static double mouseAnchorY;
    private static double translateAnchorX;
    private static double translateAnchorY;

    public static Optional<Image> showImagePicker(Stage ownerStage) {
        // Create file chooser for selecting image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Show file chooser
        File selectedFile = fileChooser.showOpenDialog(ownerStage);
        if (selectedFile == null) {
            return Optional.empty();
        }

        try {
            // Load the selected image
            Image originalImage = new Image(selectedFile.toURI().toString());
            return showCropperDialog(ownerStage, originalImage);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static Optional<Image> showCropperDialog(Stage ownerStage, Image originalImage) {
        // Create a new dialog stage
        Stage cropperStage = new Stage();
        cropperStage.initOwner(ownerStage);
        cropperStage.initModality(Modality.APPLICATION_MODAL);
        cropperStage.initStyle(StageStyle.DECORATED);
        cropperStage.setTitle("Crop Profile Picture");
        cropperStage.setWidth(450);  // Fixed width
        cropperStage.setHeight(550); // Fixed height
        cropperStage.setResizable(false);

        // Main container with dark background
        VBox mainContainer = new VBox(25);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #2a2a2a;");
        mainContainer.setPadding(new Insets(30));

        // Title at top
        Label titleLabel = new Label("Position Your Profile Picture");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Create circular crop area with a fixed size
        double cropDiameter = 250;

        // Container for the crop area with shadow effect
        StackPane cropAreaWithBorder = new StackPane();
        cropAreaWithBorder.setMinSize(cropDiameter + 20, cropDiameter + 20);
        cropAreaWithBorder.setMaxSize(cropDiameter + 20, cropDiameter + 20);
        cropAreaWithBorder.setAlignment(Pos.CENTER);

        // The crop area itself (black background)
        StackPane cropArea = new StackPane();
        cropArea.setMinSize(cropDiameter, cropDiameter);
        cropArea.setMaxSize(cropDiameter, cropDiameter);
        cropArea.setStyle("-fx-background-color: black;");
        cropArea.setAlignment(Pos.CENTER);

        // Create a visible circular border
        Circle visibleBorder = new Circle(cropDiameter / 2);
        visibleBorder.setStroke(Color.WHITE);
        visibleBorder.setStrokeWidth(2);
        visibleBorder.setFill(Color.TRANSPARENT);

        // Apply circular clip to crop area
        Circle clipCircle = new Circle(cropDiameter / 2);
        cropArea.setClip(clipCircle);

        // Calculate optimal scale to ensure image fills the circle completely
        double scaleWidth = cropDiameter / originalImage.getWidth();
        double scaleHeight = cropDiameter / originalImage.getHeight();
        double initialScale = Math.max(scaleWidth, scaleHeight);

        // Create the image view with proper scaling
        ImageView imageView = new ImageView(originalImage);
        imageView.setPreserveRatio(true);

        // Set initial image size to fill circle
        imageView.setFitWidth(originalImage.getWidth() * initialScale);
        imageView.setFitHeight(originalImage.getHeight() * initialScale);

        // Center the image in the crop area
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(cropDiameter, cropDiameter);
        imageContainer.getChildren().add(imageView);
        imageContainer.setAlignment(Pos.CENTER);

        // Initial centering of the image
        double imageWidth = imageView.getFitWidth();
        double imageHeight = imageView.getFitHeight();
        double cropWidth = cropDiameter;
        double cropHeight = cropDiameter;

        imageView.setTranslateX((cropWidth - imageWidth) / 2);
        imageView.setTranslateY((cropHeight - imageHeight) / 2);

        // Add drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(15);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));
        cropAreaWithBorder.setEffect(dropShadow);

        // Add everything to the crop area
        cropArea.getChildren().add(imageContainer);
        cropAreaWithBorder.getChildren().addAll(cropArea, visibleBorder);

        // Enable dragging for the image
        cropArea.setCursor(Cursor.MOVE);

        cropArea.setOnMousePressed(event -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            translateAnchorX = imageView.getTranslateX();
            translateAnchorY = imageView.getTranslateY();
            event.consume();
        });

        cropArea.setOnMouseDragged(event -> {
            double newX = translateAnchorX + event.getSceneX() - mouseAnchorX;
            double newY = translateAnchorY + event.getSceneY() - mouseAnchorY;

            // Constrain the translation to keep the image within the cropArea
            double maxTranslateX = (imageView.getFitWidth() - cropWidth) / 2;
            double maxTranslateY = (imageView.getFitHeight() - cropHeight) / 2;

            newX = Math.max(-maxTranslateX, Math.min(maxTranslateX, newX));
            newY = Math.max(-maxTranslateY, Math.min(maxTranslateY, newY));

            imageView.setTranslateX(newX);
            imageView.setTranslateY(newY);
            event.consume();
        });

        // Add zoom controls
        HBox zoomControlsBox = new HBox(10);
        zoomControlsBox.setAlignment(Pos.CENTER);

        Label zoomMinLabel = new Label("-");
        zoomMinLabel.setStyle("-fx-text-fill: white;");

        Label zoomMaxLabel = new Label("+");
        zoomMaxLabel.setStyle("-fx-text-fill: white;");

        Slider zoomSlider = new Slider(initialScale, initialScale * 2.5, initialScale);
        zoomSlider.setPrefWidth(300);

        zoomControlsBox.getChildren().addAll(zoomMinLabel, zoomSlider, zoomMaxLabel);

        // Add instructions
        Label instructionsLabel = new Label("Drag to position • Use slider to zoom");
        instructionsLabel.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px;");

        // Update image size when zoom slider changes
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double scale = newVal.doubleValue();

            // Update image size
            imageView.setFitWidth(originalImage.getWidth() * scale);
            imageView.setFitHeight(originalImage.getHeight() * scale);

            // Recalculate constraints for dragging
            double maxTranslateX = (imageView.getFitWidth() - cropWidth) / 2;
            double maxTranslateY = (imageView.getFitHeight() - cropHeight) / 2;

            // If the current position would be out of bounds after resizing, adjust it
            double currentX = imageView.getTranslateX();
            double currentY = imageView.getTranslateY();

            currentX = Math.max(-maxTranslateX, Math.min(maxTranslateX, currentX));
            currentY = Math.max(-maxTranslateY, Math.min(maxTranslateY, currentY));

            imageView.setTranslateX(currentX);
            imageView.setTranslateY(currentY);
        });

        // Add action buttons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(100);
        cancelButton.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-padding: 10 15;");

        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(100);
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10 15;");

        buttonsBox.getChildren().addAll(cancelButton, saveButton);

        // Add all components to main container
        mainContainer.getChildren().addAll(
                titleLabel,
                cropAreaWithBorder,
                instructionsLabel,
                zoomControlsBox,
                buttonsBox
        );

        // Result container
        final Image[] result = new Image[1];

        // Set button actions
        cancelButton.setOnAction(e -> cropperStage.close());

        saveButton.setOnAction(e -> {
            try {
                // Take snapshot of just the crop area
                javafx.scene.image.WritableImage snapshot = cropArea.snapshot(null, null);

                // Save the cropped image
                String userDataFolder = System.getProperty("user.home") + File.separator + ".mediaserver";
                Path userDataPath = Paths.get(userDataFolder);
                if (!Files.exists(userDataPath)) {
                    Files.createDirectories(userDataPath);
                }

                Path profileImagePath = userDataPath.resolve("profile.png");
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
                ImageIO.write(bufferedImage, "png", profileImagePath.toFile());

                // Update preferences
                UserPreferences.getInstance().setProfileImagePath(profileImagePath.toString());

                // Set result and close
                result[0] = snapshot;
                cropperStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Create and show scene
        Scene scene = new Scene(mainContainer);
        cropperStage.setScene(scene);
        cropperStage.showAndWait();

        return result[0] != null ? Optional.of(result[0]) : Optional.empty();
    }
}