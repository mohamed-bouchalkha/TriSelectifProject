package controller;

import dao.MenageDAO;
import dao.AdresseDAO;
import model.Menage;
import model.Adresse;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.Main; // Importez la classe Main pour accéder à la connexion

public class MenageController {

    @FXML private TextField nomField;
    @FXML private PasswordField mdpField;
    @FXML private TextField adresseIdField;
    @FXML private Label messageLabel;

    private MenageDAO menageDAO;
    private AdresseDAO adresseDAO;

    public MenageController() {
        try {
            // Utiliser la connexion existante de la classe Main
            this.menageDAO = new MenageDAO(Main.conn);
            this.adresseDAO = new AdresseDAO(Main.conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreerCompte() {
        String nom = nomField.getText();
        String mdp = mdpField.getText();

        try {
            int adresseId = Integer.parseInt(adresseIdField.getText());

            // Récupérer l'adresse à partir de l'ID
            Adresse adresse = adresseDAO.find(adresseId);

            if (adresse != null) {
                // Créer le ménage avec l'adresse récupérée
                Menage m = new Menage(nom, mdp, adresse);
                menageDAO.create(m, adresseId);
                messageLabel.setText("Compte créé avec succès !");

                // Effacer les champs après création réussie
                nomField.clear();
                mdpField.clear();
                adresseIdField.clear();
            } else {
                messageLabel.setText("Adresse non trouvée avec l'ID fourni.");
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Adresse ID invalide. Veuillez saisir un nombre.");
        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}