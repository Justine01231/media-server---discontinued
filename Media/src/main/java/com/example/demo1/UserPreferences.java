package com.example.demo1;

import java.util.prefs.Preferences;

public class UserPreferences {
    private static UserPreferences instance;
    private final Preferences prefs;
    
    // Preference keys
    private static final String USERNAME_KEY = "username";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String THEME_KEY = "theme";
    private static final String PROFILE_IMAGE_PATH_KEY = "profile_image_path";
    private static final String REMEMBER_LOGIN_KEY = "remember_login";
    private static final String ENABLE_STATS_KEY = "enable_stats";
    
    private UserPreferences() {
        prefs = Preferences.userNodeForPackage(UserPreferences.class);
    }
    
    public static synchronized UserPreferences getInstance() {
        if (instance == null) {
            instance = new UserPreferences();
        }
        return instance;
    }
    
    public String getUsername() {
        return prefs.get(USERNAME_KEY, "");
    }
    
    public void setUsername(String username) {
        prefs.put(USERNAME_KEY, username);
    }
    
    public String getEmail() {
        return prefs.get(EMAIL_KEY, "");
    }
    
    public void setEmail(String email) {
        prefs.put(EMAIL_KEY, email);
    }
    
    public String getPassword() {
        return prefs.get(PASSWORD_KEY, "");
    }
    
    public void setPassword(String password) {
        prefs.put(PASSWORD_KEY, password);
    }
    
    public String getTheme() {
        return prefs.get(THEME_KEY, "dark");
    }
    
    public void setTheme(String theme) {
        prefs.put(THEME_KEY, theme);
    }
    
    public String getProfileImagePath() {
        return prefs.get(PROFILE_IMAGE_PATH_KEY, "");
    }
    
    public void setProfileImagePath(String path) {
        prefs.put(PROFILE_IMAGE_PATH_KEY, path);
    }
    
    public boolean getRememberLogin() {
        return prefs.getBoolean(REMEMBER_LOGIN_KEY, true);
    }
    
    public void setRememberLogin(boolean remember) {
        prefs.putBoolean(REMEMBER_LOGIN_KEY, remember);
    }
    
    public boolean getEnableStats() {
        return prefs.getBoolean(ENABLE_STATS_KEY, false);
    }
    
    public void setEnableStats(boolean enable) {
        prefs.putBoolean(ENABLE_STATS_KEY, enable);
    }
    
    public void clear() {
        try {
            prefs.clear();
        } catch (Exception e) {
            System.err.println("Could not clear preferences: " + e.getMessage());
        }
    }
}