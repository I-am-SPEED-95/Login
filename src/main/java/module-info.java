module org.example.crackgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires json.simple;


    opens org.example.crackgui to javafx.fxml;
    exports org.example.crackgui;
}