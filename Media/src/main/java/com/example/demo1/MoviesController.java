package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.text.Text;

public class MoviesController {
    @FXML private FlowPane moviesGrid;
    @FXML private VBox emptyState;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ToggleButton viewToggle;

    private final MediaManager mediaManager = MediaManager.getInstance();

    @FXML
    public void initialize() {
        // Initialize sort options
        sortComboBox.setItems(FXCollections.observableArrayList(
                "Recently Added",
                "Title A-Z",
                "Title Z-A"
        ));
        sortComboBox.getSelectionModel().selectFirst();

        // Add search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshMovies();
        });

        // Add sort listener
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            refreshMovies();
        });

        // Initial load
        refreshMovies();
    }

    private void refreshMovies() {
        moviesGrid.getChildren().clear();
        List<MediaManager.MediaItem> movies = mediaManager.getMediaByType(MediaManager.MediaType.MOVIE);

        // Apply search filter
        String searchText = searchField.getText().toLowerCase();
        movies = movies.stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(searchText))
                .toList();

        // Apply sorting
        String sortOption = sortComboBox.getValue();
        if (sortOption != null) {
            movies = switch (sortOption) {
                case "Title A-Z" -> movies.stream()
                        .sorted((a, b) -> a.getTitle().compareTo(b.getTitle()))
                        .toList();
                case "Title Z-A" -> movies.stream()
                        .sorted((a, b) -> b.getTitle().compareTo(a.getTitle()))
                        .toList();
                default -> movies; // "Recently Added" - already sorted by date
            };
        }

        // Show/hide empty state
        emptyState.setVisible(movies.isEmpty());
        moviesGrid.setVisible(!movies.isEmpty());

        // Create movie cards
        for (MediaManager.MediaItem movie : movies) {
            moviesGrid.getChildren().add(createMovieCard(movie));
        }
    }

    private VBox createMovieCard(MediaManager.MediaItem movie) {
        VBox card = new VBox(10);
        card.getStyleClass().add("movie-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setPrefHeight(300);

        // Thumbnail/Poster
        if (movie.getThumbnailUrl() != null && !movie.getThumbnailUrl().isEmpty()) {
            try {
                ImageView poster = new ImageView(new Image(movie.getThumbnailUrl()));
                poster.setFitWidth(180);
                poster.setFitHeight(270);
                poster.setPreserveRatio(true);
                card.getChildren().add(poster);
            } catch (Exception e) {
                // Fallback to icon if image fails to load
                FontIcon movieIcon = new FontIcon("fas-film");
                movieIcon.setIconSize(48);
                card.getChildren().add(movieIcon);
            }
        } else {
            FontIcon movieIcon = new FontIcon("fas-film");
            movieIcon.setIconSize(48);
            card.getChildren().add(movieIcon);
        }

        // Title
        Text title = new Text(movie.getTitle());
        title.getStyleClass().add("movie-title");
        title.setWrappingWidth(180);
        card.getChildren().add(title);

        // Add click handler
        card.setOnMouseClicked(event -> openMovieDetails(movie));

        return card;
    }

    @FXML
    private void addNewMovie() {
        AddMediaDialog dialog = new AddMediaDialog();
        dialog.showAndWait().ifPresent(mediaItem -> {
            refreshMovies();
        });
    }

    private void openMovieDetails(MediaManager.MediaItem movie) {
        // TODO: Implement movie details view
        System.out.println("Opening details for: " + movie.getTitle());
    }
}
