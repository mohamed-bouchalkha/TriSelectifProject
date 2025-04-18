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
    private String centreName; // Stocker le nom du centre

    public void setUserInfo(String accountType, String name, int centreId) {
        this.centreId = centreId;
        this.centreName = name;
        System.out.println("WelcomeController: setUserInfo - accountType = " + accountType +
                ", name = " + name + ", centreId = " + centreId);
        welcomeLabel.setText("Bienvenue, " + accountType + " - " + name);
        // Pour les ménages, centreId est -1
        if (accountType.equals("Centre de Tri") && centreId <= 0) {
            System.out.println("WelcomeController: ID de centre invalide pour Centre de Tri - centreId = " + centreId);
            welcomeLabel.setText(welcomeLabel.getText() + " (Erreur: ID de centre invalide)");
        }
    }

    @FXML
    public void handleOpenGestionBacs() {
        if (centreId <= 0) {
            System.out.println("WelcomeController: Navigation vers Gestion des Bacs bloquée - centreId invalide = " + centreId);
            welcomeLabel.setText("Erreur: ID de centre invalide. Impossible d'accéder à la gestion des bacs.");
            return;
        }
        try {
            System.out.println("WelcomeController: Navigation vers Gestion des Bacs - centreId = " + centreId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/gestionBacs.fxml"));
            Parent gestionBacsPage = loader.load();
            GestionBacsController gestionBacsController = loader.getController();
            gestionBacsController.setCentreId(centreId);
            gestionBacsController.setCentreName(centreName);
            Scene scene = new Scene(gestionBacsPage);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Bacs - " + centreName);
            stage.show();
        } catch (IOException e) {
            System.err.println("WelcomeController: Erreur lors de la navigation vers Gestion des Bacs - " + e.getMessage());
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