package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnectionController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink createAccountLink;

    @FXML
    private Label messageLabel;

    @FXML
    private Rectangle userIndicator;

    @FXML
    private Rectangle adminIndicator;

    @FXML
    private VBox loginForm;

    private String userType = "user"; // Valeur par défaut

    @FXML
    public void initialize() {
        // Configuration initiale - sélectionner l'option utilisateur par défaut
        selectUserOption();

        // Effacer le message d'erreur quand l'utilisateur commence à saisir
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            messageLabel.setText("");
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            messageLabel.setText("");
        });
    }

    @FXML
    public void selectUserOption() {
        userType = "user";
        userIndicator.setFill(javafx.scene.paint.Color.valueOf("#2196F3"));
        adminIndicator.setFill(javafx.scene.paint.Color.TRANSPARENT);
    }

    @FXML
    public void selectAdminOption() {
        userType = "admin";
        adminIndicator.setFill(javafx.scene.paint.Color.valueOf("#2196F3"));
        userIndicator.setFill(javafx.scene.paint.Color.TRANSPARENT);
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        // Logique de connexion à implémenter ici
        // Pour l'instant, on affiche juste un message
        messageLabel.setText("Tentative de connexion en tant que " +
                (userType.equals("admin") ? "administrateur" : "utilisateur"));

        // TODO: Implémenter la vérification d'authentification avec la base de données
    }

    @FXML
    public void handleCreateAccount(ActionEvent event) {
        try {
            // Charger la page d'inscription
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/inscription.fxml"));
            Parent inscriptionView = loader.load();

            // Obtenir la scène actuelle
            Scene currentScene = createAccountLink.getScene();

            // Remplacer le contenu de la scène
            currentScene.setRoot(inscriptionView);

            // Alternative: Ouvrir dans une nouvelle fenêtre
            // Stage stage = new Stage();
            // stage.setScene(new Scene(inscriptionView));
            // stage.setTitle("Création de compte");
            // stage.show();
            //
            // // Fermer la fenêtre actuelle si nécessaire
            // Stage currentStage = (Stage) createAccountLink.getScene().getWindow();
            // currentStage.close();

        } catch (IOException e) {
            messageLabel.setText("Erreur lors du chargement de la page d'inscription.");
            e.printStackTrace();
        }
    }
}