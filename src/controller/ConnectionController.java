package controller;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.CentreTriDAO;
import main.Main;

public class ConnectionController {

    @FXML private Rectangle menageIndicator;
    @FXML private Rectangle centreTriIndicator;

    @FXML private VBox menageLoginForm;
    @FXML private VBox centreTriLoginForm;

    @FXML private TextField menageNomCompte;
    @FXML private PasswordField menageMotDePasse;

    @FXML private TextField centreNomCentre;
    @FXML private TextField centreAdresse;

    @FXML private Button menageLoginButton;
    @FXML private Button centreTriLoginButton;

    @FXML private Label messageLabel;

    // Variable pour suivre le type de compte sélectionné
    private String selectedAccountType = "menage";

    // Référence à la connexion de la base de données depuis Main
    private Connection connection;

    @FXML
    public void initialize() {
        // Par défaut, l'option Ménage est sélectionnée
        selectMenageOption();

        // Utiliser la connexion établie dans la classe Main
        connection = Main.conn;

        // Vérifier si la connexion est valide
        if (connection == null) {
            messageLabel.setText("Erreur: Connexion à la base de données non disponible");
        }
    }

    @FXML
    public void selectMenageOption() {
        // Activer l'indicateur pour Ménage
        menageIndicator.setFill(Color.valueOf("#4CAF50"));
        centreTriIndicator.setFill(Color.TRANSPARENT);

        // Afficher le formulaire pour Ménage et cacher celui pour Centre de Tri
        menageLoginForm.setVisible(true);
        menageLoginForm.setManaged(true);
        centreTriLoginForm.setVisible(false);
        centreTriLoginForm.setManaged(false);

        // Mettre à jour le type de compte sélectionné
        selectedAccountType = "menage";

        // Effacer le message d'erreur précédent
        messageLabel.setText("");
    }

    @FXML
    public void selectCentreTriOption() {
        // Activer l'indicateur pour Centre de Tri
        centreTriIndicator.setFill(Color.valueOf("#4CAF50"));
        menageIndicator.setFill(Color.TRANSPARENT);

        // Afficher le formulaire pour Centre de Tri et cacher celui pour Ménage
        centreTriLoginForm.setVisible(true);
        centreTriLoginForm.setManaged(true);
        menageLoginForm.setVisible(false);
        menageLoginForm.setManaged(false);

        // Mettre à jour le type de compte sélectionné
        selectedAccountType = "centreTri";

        // Effacer le message d'erreur précédent
        messageLabel.setText("");
    }

    @FXML
    public void handleMenageLogin(ActionEvent event) {
        String nomCompte = menageNomCompte.getText().trim();
        String motDePasse = menageMotDePasse.getText();

        if (nomCompte.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            // Vérifier si la connexion est valide
            if (connection == null) {
                messageLabel.setText("Erreur: Connexion à la base de données non disponible");
                return;
            }

            // Vérifier les identifiants dans la base de données
            String sql = "SELECT * FROM Menage WHERE nomCompte = ? AND motDePasse = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, nomCompte);
            stmt.setString(2, motDePasse);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Authentification réussie
                redirectToWelcomePage("Ménage", nomCompte, -1); // Pas d'ID pour ménage
            } else {
                // Authentification échouée
                messageLabel.setText("Nom de compte ou mot de passe incorrect");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de la connexion");
            e.printStackTrace();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du chargement de la page d'accueil");
            e.printStackTrace();
        }
    }


    @FXML
    public void handleCentreTriLogin(ActionEvent event) {
        String nomCentre = centreNomCentre.getText().trim();
        String adresse = centreAdresse.getText().trim();

        if (nomCentre.isEmpty() || adresse.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            if (connection == null) {
                messageLabel.setText("Erreur: Connexion à la base de données non disponible");
                return;
            }

            String sql = "SELECT ct.idCentre, ct.nomCentre, a.id AS adresse_id " +
                    "FROM CentreTri ct " +
                    "JOIN Adresse a ON ct.adresse_id = a.id " +
                    "WHERE ct.nomCentre = ? AND " +
                    "(CONCAT(a.numero, ' ', a.nomRue, ', ', a.codePostal, ' ', a.ville) = ? OR " +
                    "a.nomRue = ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, nomCentre);
            stmt.setString(2, adresse);
            stmt.setString(3, adresse);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idCentre = rs.getInt("idCentre");
                redirectToWelcomePage("Centre de Tri", nomCentre, idCentre);
            } else {
                messageLabel.setText("Centre de tri non trouvé ou adresse incorrecte");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de la connexion");
            e.printStackTrace();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du chargement de la page d'accueil");
            e.printStackTrace();
        }
    }

    private void redirectToWelcomePage(String accountType, String name, int centreId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/welcome.fxml"));
        Parent welcomePage = loader.load();
        WelcomeController welcomeController = loader.getController();
        welcomeController.setUserInfo(accountType, name, centreId);
        Scene scene = new Scene(welcomePage);
        Stage stage = (Stage) centreNomCentre.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Bienvenue - " + accountType);
        stage.show();
    }



    @FXML
    public void handleCreateAccount(ActionEvent event) {
        try {
            // Charger la page d'inscription
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/inscription.fxml"));
            Parent inscriptionPage = loader.load();

            // Si vous avez un contrôleur pour la page d'inscription
            InscriptionController inscriptionController = loader.getController();
            inscriptionController.setAccountType(selectedAccountType);

            // Créer une nouvelle scène avec la page d'inscription
            Scene scene = new Scene(inscriptionPage);

            // Obtenir la fenêtre actuelle et définir la nouvelle scène
            Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du chargement de la page d'inscription");
            e.printStackTrace();
        }
    }
}