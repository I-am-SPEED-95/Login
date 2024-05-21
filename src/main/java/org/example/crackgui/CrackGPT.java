package org.example.crackgui;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class CrackGPT extends Application {
    private static final int APP_WIDTH = 800;
    private static final int APP_HEIGHT = 600;

    private TextArea textArea;
    private TextArea textAreaOutput;
    private ComboBox<String> language;
    private Button talkButton;
    private ResourceBundle resourceBundle;
    private Label languageLabel;



    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = createScene();
        scene.getStylesheets().add(getClass().getResource("org/example/crackgui/style2.css").toExternalForm());
        stage.setTitle("CrackGPT-4");
        stage.setScene(scene);
        stage.show();
    }

    private Scene createScene() {
        VBox box = new VBox();
        box.setStyle("-fx-background-color: #2067DC;");
        box.getStyleClass().add("body");


        Label crackGpt = new Label("TimoOnCrack snif snif");
        crackGpt.getStyleClass().add("crackgpt-label");
        crackGpt.setMaxWidth(Double.MAX_VALUE);
        crackGpt.setAlignment(Pos.CENTER);
        box.getChildren().add(crackGpt);

        textArea = new TextArea();
        textArea.setWrapText(true);
        box.getChildren().add(textArea);

        StackPane textAreaPane = new StackPane();
        textAreaPane.setPadding(new Insets(0, 15, 0, 15));
        textAreaPane.getChildren().add(textArea);
        box.getChildren().add(textAreaPane);

        GridPane settingsPane = createSettingsComponent();
        box.getChildren().add(settingsPane);

        talkButton = createButton();
        talkButton.setOnAction(event -> {
            String userInput = textArea.getText();
            String selectedLanguage = language.getValue();

            CrackGPTgui crackGPTgui = new CrackGPTgui();
            String response = crackGPTgui.crackGPT(userInput, selectedLanguage);

            textAreaOutput.setText(response);
        });
        StackPane talkButtonPane = new StackPane();
        talkButtonPane.setPadding(new Insets(40, 20, 0, 20));
        talkButtonPane.getChildren().add(talkButton);
        box.getChildren().add(talkButtonPane);

        textAreaOutput = new TextArea();
        textAreaOutput.setWrapText(true);
        box.getChildren().add(textAreaOutput);

        StackPane textAreaOutputPane = new StackPane();
        textAreaOutputPane.setPadding(new Insets(20, 15, 20, 15));
        textAreaOutputPane.getChildren().add(textAreaOutput);
        textAreaOutput.setEditable(false);
        box.getChildren().add(textAreaOutputPane);

        BorderPane root = new BorderPane();

        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER_LEFT);
        Button hamburgerButton = new Button("\u2630");
        hamburgerButton.getStyleClass().add("hamburger-button");
        hamburgerButton.setOnAction(event -> {
            if (root.getLeft() == null) {
                root.setLeft(createMenu());
            } else {
                root.setLeft(null);
            }
        });
        topBox.getChildren().add(hamburgerButton);
        root.setTop(topBox);

        root.setCenter(box);

        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        return scene;
    }

    private HBox createMenu() {
        HBox menu = new HBox();
        menu.getStyleClass().add("menu");
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(10));

        Label newItem = new Label("New Chat");
        Label historyItem = new Label("History");
        menu.getChildren().addAll(newItem, historyItem);

        return menu;
    }


    private Button createButton() {
        Button button = new Button();
        button.getStyleClass().add("talk-btn");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER);


        String buttonText = resourceBundle.getString("ask") + " CrackedGPT";
        button.setText(buttonText);


        button.setOnAction(event -> {
            String userInput = textArea.getText();
            String selectedLanguage = language.getValue();

            CrackGPTgui crackGPTgui = new CrackGPTgui();
            String response = crackGPTgui.crackGPT(userInput, selectedLanguage);

            textAreaOutput.setText(response);
        });

        return button;
    }



    private GridPane createSettingsComponent() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 0, 0, 0));
        gridPane.setAlignment(Pos.CENTER);

        this.languageLabel = new Label("Language");
        this.languageLabel.getStyleClass().add("setting-label");
        GridPane.setHalignment(this.languageLabel, HPos.CENTER);
        gridPane.add(this.languageLabel, 0, 0);

        language = new ComboBox<>();
        language.getItems().addAll("English", "Spanish", "Dutch", "Norwegian", "Portuguese",
                "German", "French", "Italian");
        language.getStyleClass().add("setting-combo-box");
        gridPane.add(language, 0, 1);
        language.setValue("Dutch");

        language.setOnAction(event -> languageChange());

        loadBundle();

        return gridPane;
    }

    private void languageChange() {
        String selectedLanguage = language.getValue();
        Locale newLocale;
        switch (selectedLanguage) {
            case "English":
                newLocale = new Locale("en");
                break;
            case "Spanish":
                newLocale = new Locale("es");
                break;
            case "Dutch":
                newLocale = new Locale("nl");
                break;
            case "Norwegian":
                newLocale = new Locale("no");
                break;
            case "Portuguese":
                newLocale = new Locale("pt");
                break;
            case "German":
                newLocale = new Locale("de");
                break;
            case "French":
                newLocale = new Locale("fr");
                break;
            case "Italian":
                newLocale = new Locale("it");
                break;
            default:
                newLocale = Locale.getDefault();
                break;
        }
        resourceBundle = ResourceBundle.getBundle("org.example.crackgui.messages", newLocale);
        updateTexts();
        updateButton();
    }


    private void updateButton() {
        String buttonText = resourceBundle.getString("ask") + " CrackedGPT";
        talkButton.setText(buttonText);
    }


    private void loadBundle() {
        Locale defaultLocale = Locale.getDefault();
        resourceBundle = ResourceBundle.getBundle("org.example.crackgui.messages", defaultLocale);
        updateTexts();
    }


    private void updateTexts() {
        this.languageLabel.setText(resourceBundle.getString("language"));
    }
}
