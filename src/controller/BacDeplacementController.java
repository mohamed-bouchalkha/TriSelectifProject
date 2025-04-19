package controller;

import dao.AdresseDAO;
import dao.BacDAO;
import dao.CentreTriDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

public class BacDeplacementController {

    @FXML private TableView<Bac> bacsTable;
    @FXML private TableColumn<Bac, String> idColumn;
    @FXML private TableColumn<Bac, String> couleurColumn;
    @FXML private TableColumn<Bac, String> adresseColumn;

    @FXML private ComboBox<Bac> bacCombo;
    @FXML private RadioButton existingAddressRadio;
    @FXML private RadioButton newAddressRadio;
    @FXML private ToggleGroup addressToggleGroup;
    @FXML private ComboBox<Adresse> adresseCombo;
    @FXML private VBox newAddressBox;
    @FXML private HBox existingAddressBox;
    @FXML private TextField numeroField;
    @FXML private TextField rueField;
    @FXML private TextField codePostalField;
    @FXML private TextField villeField;

    @FXML private Button deplacerButton;
    @FXML private Button clearButton;
    @FXML private Button retourButton;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    @FXML private Label messageLabel;
    @FXML private Label centreTitleLabel;

    private Connection connection;
    private BacDAO bacDAO;
    private AdresseDAO adresseDAO;
    private CentreTriDAO centreTriDAO;
    private ObservableList<Bac> bacsList;
    private ObservableList<Adresse> adressesList;
    private int centreId;
    private String centreName;

    public void setCentreId(int centreId) {
        this.centreId = centreId;
        if (bacsList != null && bacDAO != null) {
            loadCentreInfo();
            loadBacs();
            loadAdresses();
        }
    }

    public void setCentreName(String centreName) {
        this.centreName = centreName;
        if (centreTitleLabel != null) {
            centreTitleLabel.setText(centreName);
        }
    }

    @FXML
    public void initialize() {
        connection = Main.conn;
        if (connection == null) {
            showErrorMessage("Erreur: Connexion à la base de données non disponible");
            deplacerButton.setDisable(true);
            return;
        }

        bacDAO = new BacDAO(connection);
        adresseDAO = new AdresseDAO(connection);
        centreTriDAO = new CentreTriDAO(connection);

        // Initialisation des colonnes de la table
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdBac().toString()));
        couleurColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCouleurBac().name()));
        adresseColumn.setCellValueFactory(cellData -> {
            Adresse adresse = cellData.getValue().getAdresseBac();
            return new SimpleStringProperty(adresse != null ? adresse.toString() : "Non définie");
        });

        bacsList = FXCollections.observableArrayList();
        bacsTable.setItems(bacsList);

        // Configuration du ComboBox pour les bacs
        bacCombo.setItems(bacsList);
        bacCombo.setConverter(new javafx.util.StringConverter<Bac>() {
            @Override
            public String toString(Bac bac) {
                return bac != null ? bac.getIdBac() + " (" + bac.getCouleurBac().name() + ")" : "";
            }

            @Override
            public Bac fromString(String string) {
                return null;
            }
        });

        // Configuration du ComboBox pour les adresses
        adressesList = FXCollections.observableArrayList();
        adresseCombo.setItems(adressesList);
        adresseCombo.setConverter(new javafx.util.StringConverter<Adresse>() {
            @Override
            public String toString(Adresse adresse) {
                return adresse != null ? adresse.toString() : "";
            }

            @Override
            public Adresse fromString(String string) {
                return null;
            }
        });

        // Gestion de la bascule entre adresse existante et nouvelle adresse
        existingAddressRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                existingAddressBox.setVisible(true);
                existingAddressBox.setManaged(true);
                newAddressBox.setVisible(false);
                newAddressBox.setManaged(false);
            }
        });
        newAddressRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                existingAddressBox.setVisible(false);
                existingAddressBox.setManaged(false);
                newAddressBox.setVisible(true);
                newAddressBox.setManaged(true);
            }
        });

        // Validation des champs
        setupTextFieldValidation(numeroField, "[0-9]*", "Numéro invalide");
        setupTextFieldValidation(codePostalField, "[0-9]{0,5}", "Code postal invalide (5 chiffres)");

        // Charger les données si centreId est défini
        if (centreId > 0) {
            loadCentreInfo();
            loadBacs();
            loadAdresses();
        }
    }

    private void loadBacs() {
        if (centreId <= 0) {
            showErrorMessage("Erreur: ID du centre non défini");
            deplacerButton.setDisable(true);
            return;
        }

        bacsList.clear();
        try {
            CentreTri centre = centreTriDAO.find(centreId);
            if (centre == null) {
                showErrorMessage("Erreur: Centre de tri non trouvé (ID: " + centreId + ")");
                deplacerButton.setDisable(true);
                return;
            }

            List<Bac> bacs = bacDAO.findAllByCentre(centreId);
            if (bacs.isEmpty()) {
                showInfoMessage("Aucun bac disponible pour ce centre");
            } else {
                bacsList.addAll(bacs);
                showSuccessMessage("Bacs chargés avec succès");
            }
            deplacerButton.setDisable(false);
        } catch (Exception e) {
            showErrorMessage("Erreur lors du chargement des bacs: " + e.getMessage());
            deplacerButton.setDisable(true);
        }
    }

    private void loadAdresses() {
        adressesList.clear();
        try {
            List<Adresse> adresses = adresseDAO.findAll();
            if (adresses.isEmpty()) {
                showInfoMessage("Aucune adresse disponible dans la base de données");
                existingAddressRadio.setDisable(true);
                newAddressRadio.setSelected(true);
            } else {
                adressesList.addAll(adresses);
                existingAddressRadio.setDisable(false);
            }
        } catch (Exception e) {
            showErrorMessage("Erreur lors du chargement des adresses: " + e.getMessage());
            existingAddressRadio.setDisable(true);
            newAddressRadio.setSelected(true);
        }
    }

    private void loadCentreInfo() {
        try {
            CentreTri centre = centreTriDAO.find(centreId);
            if (centre != null) {
                centreName = centre.getNomCentre();
                centreTitleLabel.setText(centreName);
            } else {
                centreTitleLabel.setText("Centre de tri inconnu");
            }
        } catch (Exception e) {
            showErrorMessage("Erreur lors du chargement des informations du centre: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeplacerBac() {
        if (!validerChamps()) {
            return;
        }

        Bac selectedBac = bacCombo.getValue();
        if (selectedBac == null) {
            showWarningMessage("Veuillez sélectionner un bac à déplacer");
            bacCombo.requestFocus();
            return;
        }

        // Run the database operation in a background thread
        new Thread(() -> {
            try {
                int adresseId;
                if (existingAddressRadio.isSelected()) {
                    Adresse selectedAdresse = adresseCombo.getValue();
                    if (selectedAdresse == null) {
                        Platform.runLater(() -> {
                            showWarningMessage("Veuillez sélectionner une adresse existante");
                            adresseCombo.requestFocus();
                        });
                        return;
                    }
                    adresseId = selectedAdresse.getId();
                } else {
                    Adresse nouvelleAdresse = new Adresse(
                            -1,
                            Integer.parseInt(numeroField.getText()),
                            rueField.getText(),
                            Integer.parseInt(codePostalField.getText()),
                            villeField.getText()
                    );

                    adresseId = adresseDAO.findByAdresse(nouvelleAdresse);
                    if (adresseId == -1) {
                        adresseId = adresseDAO.create(nouvelleAdresse);
                        if (adresseId == -1) {
                            Platform.runLater(() -> showErrorMessage("Erreur lors de la création de la nouvelle adresse"));
                            return;
                        }
                    }
                }

                if (bacDAO.updateAdresse(selectedBac.getIdBac(), adresseId)) {
                    Platform.runLater(() -> {
                        loadBacs();
                        loadAdresses();
                        clearFormulaire();
                        showSuccessMessage("Bac déplacé avec succès");
                    });
                } else {
                    Platform.runLater(() -> showErrorMessage("Erreur lors du déplacement du bac"));
                }
            } catch (NumberFormatException e) {
                Platform.runLater(() -> showErrorMessage("Veuillez entrer des valeurs numériques valides pour numéro et code postal"));
            } catch (Exception e) {
                Platform.runLater(() -> showErrorMessage("Erreur lors du déplacement du bac: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void handleClearFormulaire() {
        clearFormulaire();
        showInfoMessage("Formulaire réinitialisé");
    }

    @FXML
    public void handleRefresh() {
        loadBacs();
        loadAdresses();
    }

    @FXML
    public void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/gestionBacs.fxml"));
            Parent gestionBacsPage = loader.load();
            GestionBacsController controller = loader.getController();
            controller.setCentreId(centreId);
            controller.setCentreName(centreName);
            Scene scene = new Scene(gestionBacsPage);
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Bacs");
            stage.show();
        } catch (IOException e) {
            showErrorMessage("Erreur lors du retour à la gestion des bacs");
        }
    }

    @FXML
    public void handleLogout() {
        try {
            CentreTri.clearMapCentre();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/MainConnection.fxml"));
            Parent loginPage = loader.load();
            Scene scene = new Scene(loginPage);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            showErrorMessage("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    private boolean validerChamps() {
        if (bacCombo.getValue() == null) {
            showWarningMessage("Veuillez sélectionner un bac");
            bacCombo.requestFocus();
            return false;
        }

        if (existingAddressRadio.isSelected()) {
            if (adresseCombo.getValue() == null) {
                showWarningMessage("Veuillez sélectionner une adresse existante");
                adresseCombo.requestFocus();
                return false;
            }
        } else {
            if (numeroField.getText().isEmpty() || !numeroField.getText().matches("\\d+")) {
                showWarningMessage("Veuillez entrer un numéro valide (nombre entier)");
                numeroField.requestFocus();
                return false;
            }
            if (rueField.getText().isEmpty()) {
                showWarningMessage("Veuillez entrer une rue");
                rueField.requestFocus();
                return false;
            }
            if (codePostalField.getText().isEmpty() || !codePostalField.getText().matches("\\d{5}")) {
                showWarningMessage("Veuillez entrer un code postal valide (5 chiffres)");
                codePostalField.requestFocus();
                return false;
            }
            if (villeField.getText().isEmpty()) {
                showWarningMessage("Veuillez entrer une ville");
                villeField.requestFocus();
                return false;
            }
        }
        return true;
    }

    private void clearFormulaire() {
        bacCombo.setValue(null);
        adresseCombo.setValue(null);
        numeroField.clear();
        rueField.clear();
        codePostalField.clear();
        villeField.clear();
        existingAddressRadio.setSelected(true);
        messageLabel.setText("");
        messageLabel.getStyleClass().removeAll("error-message", "success-message", "warning-message", "info-message");
    }

    private void setupTextFieldValidation(TextField field, String pattern, String errorMessage) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches(pattern)) {
                field.setStyle("-fx-border-color: #D32F2F;");
                field.setTooltip(new Tooltip(errorMessage));
            } else {
                field.setStyle("");
                field.setTooltip(null);
            }
        });
    }

    private void showSuccessMessage(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("error-message", "warning-message", "info-message");
        messageLabel.getStyleClass().add("success-message");
        fadeMessage();
    }

    private void showErrorMessage(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("success-message", "warning-message", "info-message");
        messageLabel.getStyleClass().add("error-message");
        fadeMessage();
    }

    private void showWarningMessage(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("error-message", "success-message", "info-message");
        messageLabel.getStyleClass().add("warning-message");
        fadeMessage();
    }

    private void showInfoMessage(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("error-message", "success-message", "warning-message");
        messageLabel.getStyleClass().add("info-message");
        fadeMessage();
    }

    private void fadeMessage() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                Platform.runLater(() -> {
                    if (!messageLabel.getText().isEmpty()) {
                        messageLabel.setText("");
                        messageLabel.getStyleClass().removeAll("error-message", "success-message", "warning-message", "info-message");
                    }
                });
            } catch (InterruptedException e) {
                // Ignorer
            }
        }).start();
    }
}