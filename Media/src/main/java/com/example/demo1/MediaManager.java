package com.example.demo1;

import java.io.File;
import java.util.*;

public class MediaManager {
    private static MediaManager instance;
    private final List<MediaItem> allMedia = new ArrayList<>();
    private final List<MediaItem> recentlyAdded = new ArrayList<>();
    private final List<MediaItem> continueWatching = new ArrayList<>();

    public static class MediaItem {
        private final File file;
        private final MediaType type;
        private final String title;
        private final String thumbnailUrl;
        private double watchProgress = 0.0;
        private final Date dateAdded;

        public MediaItem(File file, MediaType type, String title, String thumbnailUrl) {
            this.file = file;
            this.type = type;
            this.title = title;
            this.thumbnailUrl = thumbnailUrl;
            this.dateAdded = new Date();
        }

        // Add getters for new fields
        public String getTitle() { return title; }
        public String getThumbnailUrl() { return thumbnailUrl; }

        public File getFile() { return file; }
        public MediaType getType() { return type; }
        public double getWatchProgress() { return watchProgress; }
        public void setWatchProgress(double progress) {
            this.watchProgress = progress;
            MediaManager.getInstance().updateContinueWatching(this);
        }
        public Date getDateAdded() { return dateAdded; }
    }

    public enum MediaType {
        MOVIE, TV_SHOW, MUSIC, PHOTO
    }

    private MediaManager() {}

    public static MediaManager getInstance() {
        if (instance == null) {
            instance = new MediaManager();
        }
        return instance;
    }

    public MediaItem createMediaItem(File file, String title, String thumbnailUrl) {
        MediaType type = detectMediaType(file);
        MediaItem item = new MediaItem(file, type, title, thumbnailUrl);
        addMedia(item);
        return item;
    }

    public void addMedia(MediaItem item) {
        allMedia.add(item);
        recentlyAdded.add(0, item);
        if (recentlyAdded.size() > 10) {
            recentlyAdded.remove(recentlyAdded.size() - 1);
        }
    }

    private MediaType detectMediaType(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi")) {
            return MediaType.MOVIE;
        } else if (name.endsWith(".mp3") || name.endsWith(".wav")) {
            return MediaType.MUSIC;
        } else if (name.endsWith(".jpg") || name.endsWith(".png")) {
            return MediaType.PHOTO;
        }
        return MediaType.MOVIE; // Default to movie
    }

    public void updateContinueWatching(MediaItem item) {
        if (item.getWatchProgress() > 0 && item.getWatchProgress() < 1.0) {
            if (!continueWatching.contains(item)) {
                continueWatching.add(0, item);
                if (continueWatching.size() > 10) {
                    continueWatching.remove(continueWatching.size() - 1);
                }
            }
        } else {
            continueWatching.remove(item);
        }
    }

    public List<MediaItem> getRecentlyAdded() { return new ArrayList<>(recentlyAdded); }
    public List<MediaItem> getContinueWatching() { return new ArrayList<>(continueWatching); }
    public List<MediaItem> getAllMedia() { return new ArrayList<>(allMedia); }

    public int getCount(MediaType type) {
        return (int) allMedia.stream().filter(item -> item.getType() == type).count();
    }

    public List<MediaItem> getMediaByType(MediaType type) {
        return allMedia.stream()
                .filter(item -> item.getType() == type)
                .sorted((a, b) -> b.getDateAdded().compareTo(a.getDateAdded()))
                .toList();
    }
}