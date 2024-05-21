package org.example.crackgui;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.crackgui.gebruikers.Gebruiker;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField gebruikersnaamField;

    @FXML
    private PasswordField wachtwoordField;

    @FXML
    private PasswordField herhaalWachtwoordField;

    @FXML
    private TextField loginGebruikersnaamField;

    @FXML
    private PasswordField loginWachtwoordField;

    @FXML
    private Button registreerButton;

    @FXML
    private Button loginButton;

    @FXML
    private VBox vbox;

    private Parent fxml;

    private static final String GEBRUIKERS_FILE = "src/main/java/org/example/crackgui/gebruikers/gebruikers.txt";
    private static boolean isInitialLoad = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (registreerButton != null) {
            registreerButton.setOnAction(event -> registreer());
        }
        if (loginButton != null) {
            loginButton.setOnAction(event -> login());
        }

        if (isInitialLoad && vbox != null) {
            isInitialLoad = false;
            loadInitialView();
        }
    }

    private void loadInitialView() {
        vbox.setTranslateX(vbox.getLayoutX() * 20);
        TranslateTransition t = new TranslateTransition(Duration.seconds(1), vbox);
        t.setToX(vbox.getLayoutX() * 20);
        t.play();
        t.setOnFinished((e) -> {
            try {
                fxml = FXMLLoader.load(getClass().getResource("SignIn.fxml"));
                vbox.getChildren().removeAll();
                vbox.getChildren().setAll(fxml);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void open_signin(ActionEvent event) {
        loadFXML("SignIn.fxml", vbox.getLayoutX() * 20);
    }

    @FXML
    private void open_signup(ActionEvent event) {
        loadFXML("SignUp.fxml", 0);
    }

    private void loadFXML(String fxmlFile, double toX) {
        TranslateTransition t = new TranslateTransition(Duration.seconds(1), vbox);
        t.setToX(toX);
        t.play();
        t.setOnFinished((e) -> {
            try {
                fxml = FXMLLoader.load(getClass().getResource(fxmlFile));
                vbox.getChildren().clear();
                vbox.getChildren().add(fxml);
            } catch (IOException ex) {
                showAlert("Error", "Could not load the FXML file: " + fxmlFile);
                ex.printStackTrace();
            }
        });
    }

    private void registreer() {
        String gebruikersnaam = gebruikersnaamField.getText();
        String wachtwoord = wachtwoordField.getText();
        String herhaalWachtwoord = herhaalWachtwoordField.getText();

        if (!wachtwoord.equals(herhaalWachtwoord)) {
            showAlert("Error", "Wachtwoorden zijn niet gelijk aan elkaar.");
            return;
        }

        try {
            List<Gebruiker> gebruikers = loadGebruikers();
            if (gebruikers.stream().anyMatch(g -> g.getGebruikersnaam().equals(gebruikersnaam))) {
                showAlert("Error", "Gebruikersnaam bestaat al.");
                return;
            }

            int id = generateId();
            Gebruiker gebruiker = new Gebruiker(id, gebruikersnaam, wachtwoord);
            saveGebruiker(gebruiker);
            showAlert("Success", "Gebruiker succesvol geregistreerd.");
        } catch (IOException e) {
            showAlert("Error", "Er is een fout opgetreden bij het registreren van de gebruiker.");
            e.printStackTrace();
        }
    }

    private void login() {
        String gebruikersnaam = loginGebruikersnaamField.getText();
        String wachtwoord = loginWachtwoordField.getText();

        try {
            List<Gebruiker> gebruikers = loadGebruikers();
            Optional<Gebruiker> matchedGebruiker = gebruikers.stream()
                    .filter(g -> g.getGebruikersnaam().equals(gebruikersnaam) && g.getWachtwoord().equals(wachtwoord))
                    .findFirst();

            if (matchedGebruiker.isPresent()) {
                runCrackGPT();
            } else {
                showAlert("Error", "Gebruikersnaam of wachtwoord is incorrect.");
            }
        } catch (IOException e) {
            showAlert("Error", "Er is een fout opgetreden bij het inloggen.");
            e.printStackTrace();
        }
    }

    private int generateId() throws IOException {
        List<Gebruiker> gebruikers = loadGebruikers();
        int highestId = gebruikers.stream().mapToInt(Gebruiker::getId).max().orElse(0);
        return highestId + 1;
    }

    private List<Gebruiker> loadGebruikers() throws IOException {
        List<Gebruiker> gebruikers = new ArrayList<>();
        File file = new File(GEBRUIKERS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(", ");
                    if (parts.length == 3) {
                        int id = Integer.parseInt(parts[0].trim());
                        String gebruikersnaam = parts[1].trim();
                        String wachtwoord = parts[2].trim();
                        Gebruiker gebruiker = new Gebruiker(id, gebruikersnaam, wachtwoord);
                        gebruikers.add(gebruiker);
                    }
                }
            }
        }
        return gebruikers;
    }

    private void saveGebruiker(Gebruiker gebruiker) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GEBRUIKERS_FILE, true))) {
            writer.write(gebruiker.getId() + ", " + gebruiker.getGebruikersnaam() + ", " + gebruiker.getWachtwoord());
            writer.newLine();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void runCrackGPT() {
        try {
            Stage newStage = new Stage();
            CrackGPT crackGPT = new CrackGPT();
            crackGPT.start(newStage);
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            showAlert("Error", "Er is een fout opgetreden bij het starten van CrackGPT.");
            e.printStackTrace();
        }
    }

}
