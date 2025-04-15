package controller;

import dao.MenageDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

public class InscriptionController {

    @FXML
    private TextField nomCompteField;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    private TextField motDePasseVisible;

    @FXML
    private ComboBox<String> adresseComboBox;

    @FXML
    private CheckBox proCheckBox;

    @FXML
    private Button inscriptionButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Hyperlink retourLien;

    @FXML
    private ImageView eyeIcon;

    @FXML
    private Button togglePasswordButton;

    private boolean passwordVisible = false;
    private MenageDAO menageDAO;

    @FXML
    public void initialize() {
        Connection conn = Main.conn;
        menageDAO = new MenageDAO(conn);

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nomRue FROM Adresse");
            while (rs.next()) {
                adresseComboBox.getItems().add(rs.getString("nomRue"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors du chargement des adresses.");
        }

        // Synchroniser les champs de mot de passe
        motDePasseField.textProperty().addListener((observable, oldValue, newValue) -> {
            motDePasseVisible.setText(newValue);
        });

        motDePasseVisible.textProperty().addListener((observable, oldValue, newValue) -> {
            motDePasseField.setText(newValue);
        });

        // Réinitialiser le message quand l'utilisateur commence à saisir
        nomCompteField.textProperty().addListener((observable, oldValue, newValue) -> {
            messageLabel.setText("");
        });

        motDePasseField.textProperty().addListener((observable, oldValue, newValue) -> {
            messageLabel.setText("");
        });

        adresseComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            messageLabel.setText("");
        });
    }

    @FXML
    public void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            // Show password as plain text
            motDePasseVisible.setText(motDePasseField.getText());
            motDePasseField.setVisible(false);
            motDePasseField.setManaged(false);
            motDePasseVisible.setVisible(true);
            motDePasseVisible.setManaged(true);
            togglePasswordButton.setText("🙈"); // Change to hide icon
        } else {
            // Hide password (show as dots)
            motDePasseField.setText(motDePasseVisible.getText());
            motDePasseField.setVisible(true);
            motDePasseField.setManaged(true);
            motDePasseVisible.setVisible(false);
            motDePasseVisible.setManaged(false);
            togglePasswordButton.setText("👁"); // Change to show icon
        }
    }
    private String accountType;

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @FXML
    public void handleInscription() {
        String nomCompte = nomCompteField.getText();
        String motDePasse = motDePasseField.getText();
        String adresse = adresseComboBox.getValue();
        boolean estProfessionnel = proCheckBox.isSelected();

        // Validation des champs
        if (nomCompte.isEmpty() || motDePasse.isEmpty() || adresse == null) {
            messageLabel.setText("Veuillez remplir tous les champs");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        // Simuler la création de compte (à remplacer par l'enregistrement réel dans la base de données)
        boolean success = true; // À remplacer par le résultat de l'insertion en base de données

        if (success) {
            messageLabel.setText("Compte créé avec succès !");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("success-message");

            // Désactiver les contrôles après l'inscription réussie
            nomCompteField.setDisable(true);
            motDePasseField.setDisable(true);
            motDePasseVisible.setDisable(true);
            adresseComboBox.setDisable(true);
            proCheckBox.setDisable(true);
            inscriptionButton.setDisable(true);
            togglePasswordButton.setDisable(true);
        } else {
            messageLabel.setText("Erreur lors de la création du compte");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
        }
    }

    @FXML
    public void retourConnexion(ActionEvent event) {
        try {
            // Charger la vue de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/MainConnection.fxml"));
            Parent connexionView = loader.load();

            // Créer une nouvelle scène avec la vue de connexion
            Scene connexionScene = new Scene(connexionView);

            // Obtenir la fenêtre actuelle et définir la nouvelle scène
            Stage stage = (Stage) retourLien.getScene().getWindow();
            stage.setScene(connexionScene);
            stage.setTitle("Connexion");

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors du chargement de la page de connexion");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
        }
    }
}