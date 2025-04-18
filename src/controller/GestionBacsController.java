package controller;

import dao.AdresseDAO;
import dao.BacDAO;
import dao.CentreTriDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.Main;
import model.Adresse;
import model.Bac;
import model.CentreTri;
import model.Couleur;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class GestionBacsController {

    @FXML private TableView<Bac> bacsTable;
    @FXML private TableColumn<Bac, String> idColumn;
    @FXML private TableColumn<Bac, String> couleurColumn;
    @FXML private TableColumn<Bac, Integer> capaciteColumn;
    @FXML private TableColumn<Bac, Integer> contenuColumn;
    @FXML private TableColumn<Bac, String> adresseColumn;

    @FXML private ComboBox<String> couleurCombo;
    @FXML private TextField capaciteField;
    @FXML private TextField numeroField;
    @FXML private TextField rueField;
    @FXML private TextField codePostalField;
    @FXML private TextField villeField;

    @FXML private Button ajouterButton;
    @FXML private Button modifierButton;
    @FXML private Button supprimerButton;
    @FXML private Button retourButton;

    @FXML private Label messageLabel;

    private Connection connection;
    private BacDAO bacDAO;
    private AdresseDAO adresseDAO;
    private CentreTriDAO centreTriDAO;
    private ObservableList<Bac> bacsList;
    private Bac selectedBac;
    private int centreId; // ID du centre connecté

    public void setCentreId(int centreId) {
        this.centreId = centreId;
        // Recharger les bacs après avoir défini centreId
        if (bacsList != null && bacDAO != null) {
            loadBacs();
        }
    }

    @FXML
    public void initialize() {
        // Initialiser la connexion et les DAO
        connection = Main.conn; // Remplacer Main.getConnection() par Main.conn
        if (connection == null) {
            messageLabel.setText("Erreur: Connexion à la base de données non disponible");
            return;
        }

        bacDAO = new BacDAO(connection);
        adresseDAO = new AdresseDAO(connection);
        centreTriDAO = new CentreTriDAO(connection);

        // Configurer les colonnes de la table
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdBac().toString()));
        couleurColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCouleurBac().name()));
        capaciteColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCapacite()).asObject());
        contenuColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getContenu()).asObject());
        adresseColumn.setCellValueFactory(cellData -> {
            Adresse adresse = cellData.getValue().getAdresseBac();
            return new SimpleStringProperty(adresse != null ? adresse.toString() : "Non définie");
        });

        // Initialiser la liste des bacs
        bacsList = FXCollections.observableArrayList();
        bacsTable.setItems(bacsList);

        // Configurer le ComboBox des couleurs
        couleurCombo.setItems(FXCollections.observableArrayList("vert", "jaune", "bleu", "gris"));

        // Gérer la sélection dans la table
        bacsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedBac = newSelection;
                remplirFormulaire(selectedBac);
                modifierButton.setDisable(false);
                supprimerButton.setDisable(false);
            } else {
                selectedBac = null;
                clearFormulaire();
                modifierButton.setDisable(true);
                supprimerButton.setDisable(true);
            }
        });

        // Charger les bacs si centreId est déjà défini
        if (centreId > 0) {
            loadBacs();
        }
    }

    // Charger la liste des bacs depuis la base de données
    private void loadBacs() {
        if (centreId <= 0) {
            System.out.println("loadBacs: centreId non défini (" + centreId + ")");
            messageLabel.setText("Erreur: ID du centre non défini");
            return;
        }

        bacsList.clear();
        try {
            List<Bac> bacs = bacDAO.findAllByCentre(centreId);
            if (bacs.isEmpty()) {
                System.out.println("Aucun bac trouvé pour centreId: " + centreId);
                messageLabel.setText("Aucun bac disponible pour ce centre");
            } else {
                System.out.println("Bacs trouvés pour centreId " + centreId + ": " + bacs.size());
                bacsList.addAll(bacs);
                messageLabel.setText("Bacs chargés avec succès");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des bacs: " + e.getMessage());
            e.printStackTrace();
            messageLabel.setText("Erreur lors du chargement des bacs");
        }
    }

    @FXML
    public void handleAjouterBac() {
        try {
            // Valider les champs
            if (!validerChamps()) {
                return;
            }

            // Créer une nouvelle adresse
            Adresse adresse = new Adresse(
                    -1,
                    Integer.parseInt(numeroField.getText()),
                    rueField.getText(),
                    Integer.parseInt(codePostalField.getText()),
                    villeField.getText()
            );

            // Vérifier si l'adresse existe déjà
            int adresseId = adresseDAO.findByAdresse(adresse);
            if (adresseId == -1) {
                // L'adresse n'existe pas, la créer
                adresseId = adresseDAO.create(adresse);
                if (adresseId == -1) {
                    messageLabel.setText("Erreur lors de la création de l'adresse");
                    return;
                }
            }

            // Créer un nouveau bac
            UUID idBac = UUID.randomUUID();
            Couleur couleur = Couleur.valueOf(couleurCombo.getValue());
            int capacite = Integer.parseInt(capaciteField.getText());
            CentreTri centre = centreTriDAO.find(centreId);
            if (centre == null) {
                messageLabel.setText("Erreur: Centre de tri non trouvé");
                return;
            }
            Bac bac = new Bac(idBac, centre, couleur, capacite);
            bac.setAdresseBac(adresse); // Définir l'adresse dans l'objet Bac

            // Insérer le bac dans la base de données
            bacDAO.create(bac, centre.getIdCentre(), adresseId);

            // Rafraîchir la liste
            loadBacs();
            clearFormulaire();
            messageLabel.setText("Bac ajouté avec succès");
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de l'ajout du bac: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleModifierBac() {
        if (selectedBac == null) {
            messageLabel.setText("Veuillez sélectionner un bac");
            return;
        }

        try {
            // Valider les champs
            if (!validerChamps()) {
                return;
            }

            // Mettre à jour l'adresse
            Adresse adresse = selectedBac.getAdresseBac();
            adresse.setNum(Integer.parseInt(numeroField.getText()));
            adresse.setNomRue(rueField.getText());
            adresse.setCodeP(Integer.parseInt(codePostalField.getText()));
            adresse.setVille(villeField.getText());
            adresseDAO.update(adresse, adresse.getId());

            // Mettre à jour le bac
            selectedBac.setCapacite(Integer.parseInt(capaciteField.getText()));
            selectedBac.setCouleurBac(Couleur.valueOf(couleurCombo.getValue()));
            bacDAO.update(selectedBac, adresse.getId());

            // Rafraîchir la liste
            loadBacs();
            clearFormulaire();
            messageLabel.setText("Bac modifié avec succès");
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de la modification du bac");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSupprimerBac() {
        if (selectedBac == null) {
            messageLabel.setText("Veuillez sélectionner un bac");
            return;
        }

        try {
            // Supprimer le bac
            bacDAO.delete(selectedBac.getIdBac());

            // Rafraîchir la liste
            loadBacs();
            clearFormulaire();
            messageLabel.setText("Bac supprimé avec succès");
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de la suppression du bac");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRetour() {
        try {
            // Charger la page du tableau de bord
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/welcome.fxml"));
            Parent welcomePage = loader.load();

            // Configurer le contrôleur du tableau de bord
            WelcomeController welcomeController = loader.getController();
            welcomeController.setUserInfo("Centre de Tri", "Nom du centre", centreId);

            // Changer la scène
            Scene scene = new Scene(welcomePage);
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tableau de Bord - Centre de Tri");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Erreur lors du retour au tableau de bord");
            e.printStackTrace();
        }
    }

    // Valider les champs du formulaire
    private boolean validerChamps() {
        if (couleurCombo.getValue() == null) {
            messageLabel.setText("Veuillez sélectionner une couleur");
            return false;
        }
        if (capaciteField.getText().isEmpty() || !capaciteField.getText().matches("\\d+")) {
            messageLabel.setText("Veuillez entrer une capacité valide");
            return false;
        }
        if (numeroField.getText().isEmpty() || !numeroField.getText().matches("\\d+")) {
            messageLabel.setText("Veuillez entrer un numéro valide");
            return false;
        }
        if (rueField.getText().isEmpty()) {
            messageLabel.setText("Veuillez entrer une rue");
            return false;
        }
        if (codePostalField.getText().isEmpty() || !codePostalField.getText().matches("\\d{5}")) {
            messageLabel.setText("Veuillez entrer un code postal valide (5 chiffres)");
            return false;
        }
        if (villeField.getText().isEmpty()) {
            messageLabel.setText("Veuillez entrer une ville");
            return false;
        }
        return true;
    }

    // Remplir le formulaire avec les données du bac sélectionné
    private void remplirFormulaire(Bac bac) {
        couleurCombo.setValue(bac.getCouleurBac().name());
        capaciteField.setText(String.valueOf(bac.getCapacite()));
        Adresse adresse = bac.getAdresseBac();
        if (adresse != null) {
            numeroField.setText(String.valueOf(adresse.getNum()));
            rueField.setText(adresse.getNomRue());
            codePostalField.setText(String.valueOf(adresse.getCodeP()));
            villeField.setText(adresse.getVille());
        } else {
            numeroField.clear();
            rueField.clear();
            codePostalField.clear();
            villeField.clear();
        }
    }

    // Vider le formulaire
    private void clearFormulaire() {
        couleurCombo.setValue(null);
        capaciteField.clear();
        numeroField.clear();
        rueField.clear();
        codePostalField.clear();
        villeField.clear();
        selectedBac = null;
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);
    }
}