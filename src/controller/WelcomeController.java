package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {

    @FXML private Label welcomeLabel;
    private int centreId; // Stocker l'ID du centre

    public void setUserInfo(String accountType, String name, int centreId) {
        this.centreId = centreId;
        welcomeLabel.setText("Bienvenue, " + accountType + " - " + name);
    }

    @FXML
    public void handleOpenGestionBacs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/gestionBacs.fxml"));
            Parent gestionBacsPage = loader.load();
            GestionBacsController gestionBacsController = loader.getController();
            gestionBacsController.setCentreId(centreId); // Passer l'ID
            Scene scene = new Scene(gestionBacsPage);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Bacs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleOpenCollecte() {
        // À implémenter
    }

    @FXML
    public void handleOpenStatistiques() {
        // À implémenter
    }

    @FXML
    public void handleOpenPartenaires() {
        // À implémenter
    }
}