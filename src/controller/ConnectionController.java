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

    private String selectedAccountType = "menage";
    private Connection connection;

    @FXML
    public void initialize() {
        selectMenageOption();
        connection = Main.conn;
        if (connection == null) {
            messageLabel.setText("Erreur: Connexion à la base de données non disponible");
        }
    }

    @FXML
    public void selectMenageOption() {
        menageIndicator.setFill(Color.valueOf("#4CAF50"));
        centreTriIndicator.setFill(Color.TRANSPARENT);
        menageLoginForm.setVisible(true);
        menageLoginForm.setManaged(true);
        centreTriLoginForm.setVisible(false);
        centreTriLoginForm.setManaged(false);
        selectedAccountType = "menage";
        messageLabel.setText("");
    }

    @FXML
    public void selectCentreTriOption() {
        centreTriIndicator.setFill(Color.valueOf("#4CAF50"));
        menageIndicator.setFill(Color.TRANSPARENT);
        centreTriLoginForm.setVisible(true);
        centreTriLoginForm.setManaged(true);
        menageLoginForm.setVisible(false);
        menageLoginForm.setManaged(false);
        selectedAccountType = "centreTri";
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
            if (connection == null) {
                messageLabel.setText("Erreur: Connexion à la base de données non disponible");
                return;
            }

            String sql = "SELECT * FROM Menage WHERE nomCompte = ? AND motDePasse = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, nomCompte);
            stmt.setString(2, motDePasse);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                redirectToWelcomePage("Ménage", nomCompte, -1);
            } else {
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

            // Requête pour vérifier le centre de tri
            String sql = "SELECT ct.idCentre, ct.nomCentre, a.id AS adresse_id, a.numero, a.nomRue, a.codePostal, a.ville " +
                    "FROM CentreTri ct " +
                    "JOIN Adresse a ON ct.adresse_id = a.id " +
                    "WHERE ct.nomCentre = ? AND " +
                    "(TRIM(CONCAT(a.numero, ' ', a.nomRue, ', ', a.codePostal, ' ', a.ville)) = ? OR " +
                    "TRIM(a.nomRue) = ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, nomCentre);
            stmt.setString(2, adresse);
            stmt.setString(3, adresse);
            System.out.println("ConnectionController: Tentative de connexion CentreTri - nomCentre = " + nomCentre + ", adresse = " + adresse);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idCentre = rs.getInt("idCentre");
                String retrievedNomCentre = rs.getString("nomCentre");
                String fullAdresse = rs.getInt("numero") + " " + rs.getString("nomRue") + ", " +
                        rs.getInt("codePostal") + " " + rs.getString("ville");
                System.out.println("ConnectionController: Centre trouvé - idCentre = " + idCentre +
                        ", nomCentre = " + retrievedNomCentre + ", adresse complète = " + fullAdresse);
                redirectToWelcomePage("Centre de Tri", retrievedNomCentre, idCentre);
            } else {
                System.out.println("ConnectionController: Aucun centre de tri trouvé pour nomCentre = " + nomCentre + ", adresse = " + adresse);
                messageLabel.setText("Centre de tri non trouvé ou adresse incorrecte");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            messageLabel.setText("Erreur lors de la connexion");
            System.err.println("ConnectionController: Erreur SQL lors de la connexion CentreTri - " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du chargement de la page d'accueil");
            System.err.println("ConnectionController: Erreur IO lors de la redirection - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectToWelcomePage(String accountType, String name, int centreId) throws IOException {
        System.out.println("ConnectionController: Redirection vers WelcomeController - accountType = " + accountType +
                ", name = " + name + ", centreId = " + centreId);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/inscription.fxml"));
            Parent inscriptionPage = loader.load();
            InscriptionController inscriptionController = loader.getController();
            inscriptionController.setAccountType(selectedAccountType);
            Scene scene = new Scene(inscriptionPage);
            Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du chargement de la page d'inscription");
            e.printStackTrace();
        }
    }
}