module com.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.core;
    requires javafx.swing;
    requires java.desktop;
    requires javafx.media;
    requires java.prefs;

    opens com.example.demo1 to javafx.fxml;
    exports com.example.demo1;
}