package controller;

import dao.AdresseDAO;
import dao.BacDAO;
import dao.CentreTriDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.Main;
import model.Adresse;
import model.Bac;
import model.CentreTri;
import model.Couleur;

import java.io.IOException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GestionBacsController {

    @FXML private TableView<Bac> bacsTable;
    @FXML private TableColumn<Bac, String> idColumn;
    @FXML private TableColumn<Bac, String> couleurColumn;
    @FXML private TableColumn<Bac, Integer> capaciteColumn;
    @FXML private TableColumn<Bac, Integer> contenuColumn;
    @FXML private TableColumn<Bac, Double> tauxRemplissageColumn;
    @FXML private TableColumn<Bac, String> adresseColumn;

    @FXML private ComboBox<Couleur> couleurCombo;
    @FXML private TextField capaciteField;
    @FXML private TextField contenuField;
    @FXML private TextField numeroField;
    @FXML private TextField rueField;
    @FXML private TextField codePostalField;
    @FXML private TextField villeField;

    @FXML private Button ajouterButton;
    @FXML private Button modifierButton;
    @FXML private Button supprimerButton;
    @FXML private Button retourButton;
    @FXML private Button clearButton;
    @FXML private Button refreshButton;

    @FXML private Label messageLabel;
    @FXML private Label centreTitleLabel;
    @FXML private Label totalBacsLabel;
    @FXML private Label totalCapaciteLabel;
    @FXML private Label totalContenuLabel;
    @FXML private Label formTitleLabel;

    // Format pour les nombres décimaux
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.0");

    private Connection connection;
    private BacDAO bacDAO;
    private AdresseDAO adresseDAO;
    private CentreTriDAO centreTriDAO;
    private ObservableList<Bac> bacsList;
    private Bac selectedBac;
    private int centreId;
    private String centreName;

    public void setCentreId(int centreId) {
        this.centreId = centreId;
        System.out.println("setCentreId: centreId défini à " + centreId);
        if (bacsList != null && bacDAO != null) {
            loadCentreInfo();
            loadBacs();
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
            return;
        }

        bacDAO = new BacDAO(connection);
        adresseDAO = new AdresseDAO(connection);
        centreTriDAO = new CentreTriDAO(connection);

        // Initialisation des colonnes de la table
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdBac().toString()));

        // Personnalisation de la colonne couleur avec des indicateurs colorés
        couleurColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCouleurBac().name()));
        couleurColumn.setCellFactory(column -> new TableCell<Bac, String>() {
            @Override
            protected void updateItem(String couleur, boolean empty) {
                super.updateItem(couleur, empty);

                if (empty || couleur == null) {
                    setText(null);
                    setStyle("");
                    getStyleClass().removeAll("couleur-cell", "couleur-vert", "couleur-jaune", "couleur-bleu", "couleur-gris");
                } else {
                    setText(couleur);
                    getStyleClass().add("couleur-cell");

                    switch (couleur.toLowerCase()) {
                        case "vert":
                            getStyleClass().add("couleur-vert");
                            break;
                        case "jaune":
                            getStyleClass().add("couleur-jaune");
                            break;
                        case "bleu":
                            getStyleClass().add("couleur-bleu");
                            break;
                        case "gris":
                            getStyleClass().add("couleur-gris");
                            break;
                        default:
                            setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
                    }
                }
            }
        });

        // Formatage des colonnes numériques
        capaciteColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        contenuColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        tauxRemplissageColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Ajustement des largeurs des colonnes pour un meilleur affichage
        idColumn.setPrefWidth(150.0);
        couleurColumn.setPrefWidth(100.0);
        capaciteColumn.setPrefWidth(110.0);
        contenuColumn.setPrefWidth(110.0);
        tauxRemplissageColumn.setPrefWidth(90.0);
        adresseColumn.setPrefWidth(250.0);

        capaciteColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCapacite()).asObject());
        contenuColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getContenu()).asObject());

        // Ajout de la colonne pour le taux de remplissage
        tauxRemplissageColumn.setCellValueFactory(cellData -> {
            int capacite = cellData.getValue().getCapacite();
            int contenu = cellData.getValue().getContenu();
            double taux = capacite > 0 ? ((double) contenu / capacite) * 100 : 0;
            return new SimpleDoubleProperty(taux).asObject();
        });
        tauxRemplissageColumn.setCellFactory(column -> new TableCell<Bac, Double>() {
            @Override
            protected void updateItem(Double taux, boolean empty) {
                super.updateItem(taux, empty);
                if (empty || taux == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(decimalFormat.format(taux) + "%");

                    // Changement de couleur selon le taux de remplissage
                    if (taux >= 90) {
                        setStyle("-fx-text-fill: #D32F2F;"); // Rouge pour taux critique
                    } else if (taux >= 75) {
                        setStyle("-fx-text-fill: #FF8F00;"); // Orange pour taux élevé
                    } else if (taux >= 50) {
                        setStyle("-fx-text-fill: #2E7D32;"); // Vert pour taux normal
                    } else {
                        setStyle("-fx-text-fill: #1565C0;"); // Bleu pour taux faible
                    }
                }
            }
        });

        adresseColumn.setCellValueFactory(cellData -> {
            Adresse adresse = cellData.getValue().getAdresseBac();
            return new SimpleStringProperty(adresse != null ? adresse.toString() : "Non définie");
        });

        bacsList = FXCollections.observableArrayList();
        bacsTable.setItems(bacsList);

        // Configuration des valeurs du ComboBox de couleur
        couleurCombo.setItems(FXCollections.observableArrayList(Couleur.values()));
        couleurCombo.setConverter(new javafx.util.StringConverter<Couleur>() {
            @Override
            public String toString(Couleur couleur) {
                return couleur != null ? couleur.name() : "";
            }

            @Override
            public Couleur fromString(String string) {
                try {
                    return string != null ? Couleur.valueOf(string) : null;
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        });

        // Écouteur de sélection pour la table des bacs
        bacsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedBac = newSelection;
                remplirFormulaire(selectedBac);
                modifierButton.setDisable(false);
                supprimerButton.setDisable(false);
                formTitleLabel.setText("Modification du bac");
            } else {
                selectedBac = null;
                clearFormulaire();
                modifierButton.setDisable(true);
                supprimerButton.setDisable(true);
                formTitleLabel.setText("Nouveau bac");
            }
        });

        // Écouteurs pour validation en temps réel
        setupTextFieldValidation(capaciteField, "[0-9]*", "Capacité invalide");
        setupTextFieldValidation(contenuField, "[0-9]*", "Contenu invalide");
        setupTextFieldValidation(numeroField, "[0-9]*", "Numéro invalide");
        setupTextFieldValidation(codePostalField, "[0-9]{0,5}", "Code postal invalide (5 chiffres)");

        // Initialiser les statistiques
        if (centreId > 0) {
            loadCentreInfo();
            loadBacs();
        }
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
            System.err.println("Erreur lors du chargement des informations du centre: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadBacs() {
        if (centreId <= 0) {
            System.out.println("loadBacs: centreId non défini (" + centreId + ")");
            showErrorMessage("Erreur: ID du centre non défini");
            return;
        }

        bacsList.clear();
        try {
            List<Bac> bacs = bacDAO.findAllByCentre(centreId);
            if (bacs.isEmpty()) {
                System.out.println("Aucun bac trouvé pour centreId: " + centreId);
                showInfoMessage("Aucun bac disponible pour ce centre");
            } else {
                System.out.println("Bacs trouvés pour centreId " + centreId + ": " + bacs.size());
                bacsList.addAll(bacs);
                showSuccessMessage("Bacs chargés avec succès");
                updateStatistics();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des bacs: " + e.getMessage());
            e.printStackTrace();
            showErrorMessage("Erreur lors du chargement des bacs: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        int totalBacs = bacsList.size();
        int totalCapacite = 0;
        int totalContenu = 0;

        for (Bac bac : bacsList) {
            totalCapacite += bac.getCapacite();
            totalContenu += bac.getContenu();
        }

        totalBacsLabel.setText(String.valueOf(totalBacs));
        totalCapaciteLabel.setText(totalCapacite + " g");
        totalContenuLabel.setText(totalContenu + " g");
    }

    @FXML
    public void handleRefresh() {
        if (centreId <= 0) {
            showErrorMessage("Erreur: ID du centre non défini. Impossible d'actualiser.");
            return;
        }

        try {
            // Désélectionner le bac actuel et réinitialiser le formulaire
            bacsTable.getSelectionModel().clearSelection();
            clearFormulaire();

            // Recharger les bacs
            loadBacs();

            // S'assurer que les statistiques sont mises à jour
            updateStatistics();

            // Message de confirmation (déjà géré dans loadBacs si succès)
        } catch (Exception e) {
            showErrorMessage("Erreur lors de l'actualisation des bacs: " + e.getMessage());
            System.err.println("Erreur dans handleRefresh: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAjouterBac() {
        try {
            if (!validerChamps()) {
                return;
            }

            CentreTri centre = centreTriDAO.find(centreId);
            if (centre == null) {
                showErrorMessage("Erreur: Centre de tri non trouvé pour ID " + centreId);
                System.out.println("Centre non trouvé pour centreId: " + centreId);
                return; // Stop execution if centre is not found
            }

            Adresse adresse = new Adresse(
                    -1,
                    Integer.parseInt(numeroField.getText()),
                    rueField.getText(),
                    Integer.parseInt(codePostalField.getText()),
                    villeField.getText()
            );

            int adresseId = adresseDAO.findByAdresse(adresse);
            if (adresseId == -1) {
                adresseId = adresseDAO.create(adresse);
                if (adresseId == -1) {
                    showErrorMessage("Erreur lors de la création de l'adresse");
                    return;
                }
            }

            UUID idBac = UUID.randomUUID();
            Couleur couleur = couleurCombo.getValue();
            if (couleur == null) {
                showErrorMessage("Veuillez sélectionner une couleur valide");
                return;
            }
            int capacite = Integer.parseInt(capaciteField.getText());
            int contenu = Integer.parseInt(contenuField.getText());
            Bac bac = new Bac(idBac, centre, couleur, capacite);
            bac.setContenu(contenu);
            bac.setAdresseBac(adresse);

            bacDAO.create(bac, centre.getIdCentre(), adresseId);

            loadBacs();
            clearFormulaire();
            showSuccessMessage("Bac ajouté avec succès");
        } catch (Exception e) {
            showErrorMessage("Erreur lors de l'ajout du bac: " + e.getMessage());
            System.err.println("Erreur dans handleAjouterBac: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    public void handleModifierBac() {
        if (selectedBac == null) {
            showWarningMessage("Veuillez sélectionner un bac à modifier");
            return;
        }

        try {
            if (!validerChamps()) {
                return;
            }

            Adresse adresse = selectedBac.getAdresseBac();
            if (adresse == null) {
                adresse = new Adresse(
                        -1,
                        Integer.parseInt(numeroField.getText()),
                        rueField.getText(),
                        Integer.parseInt(codePostalField.getText()),
                        villeField.getText()
                );
                int adresseId = adresseDAO.create(adresse);
                if (adresseId == -1) {
                    showErrorMessage("Erreur lors de la création de l'adresse");
                    return;
                }
                adresse.setId(adresseId);
            } else {
                adresse.setNum(Integer.parseInt(numeroField.getText()));
                adresse.setNomRue(rueField.getText());
                adresse.setCodeP(Integer.parseInt(codePostalField.getText()));
                adresse.setVille(villeField.getText());
                adresseDAO.update(adresse, adresse.getId());
            }

            selectedBac.setCouleurBac(couleurCombo.getValue());
            selectedBac.setCapacite(Integer.parseInt(capaciteField.getText()));
            selectedBac.setContenu(Integer.parseInt(contenuField.getText()));
            selectedBac.setAdresseBac(adresse);
            bacDAO.update(selectedBac, adresse.getId());

            loadBacs();
            clearFormulaire();
            showSuccessMessage("Bac modifié avec succès");
        } catch (NumberFormatException e) {
            showErrorMessage("Veuillez entrer des valeurs numériques valides pour numéro, capacité, contenu et code postal");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            showErrorMessage("Veuillez sélectionner une couleur valide");
            e.printStackTrace();
        } catch (Exception e) {
            showErrorMessage("Erreur lors de la modification du bac: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSupprimerBac() {
        if (selectedBac == null) {
            showWarningMessage("Veuillez sélectionner un bac à supprimer");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le bac");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le bac sélectionné ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bacDAO.delete(selectedBac.getIdBac());
                loadBacs();
                clearFormulaire();
                showSuccessMessage("Bac supprimé avec succès");
            } catch (Exception e) {
                showErrorMessage("Erreur lors de la suppression du bac: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pages/welcome.fxml"));
            Parent welcomePage = loader.load();
            WelcomeController welcomeController = loader.getController();
            welcomeController.setUserInfo("Centre de Tri", centreName, centreId);
            Scene scene = new Scene(welcomePage);
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tableau de Bord - Centre de Tri");
            stage.show();
        } catch (IOException e) {
            showErrorMessage("Erreur lors du retour au tableau de bord");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleClearFormulaire() {
        clearFormulaire();
        showInfoMessage("Formulaire réinitialisé");
    }

    private boolean validerChamps() {
        if (couleurCombo.getValue() == null) {
            showWarningMessage("Veuillez sélectionner une couleur");
            couleurCombo.requestFocus();
            return false;
        }
        if (capaciteField.getText().isEmpty() || !capaciteField.getText().matches("\\d+")) {
            showWarningMessage("Veuillez entrer une capacité valide (nombre entier positif)");
            capaciteField.requestFocus();
            return false;
        }
        if (contenuField.getText().isEmpty() || !contenuField.getText().matches("\\d+")) {
            showWarningMessage("Veuillez entrer un contenu valide (nombre entier positif)");
            contenuField.requestFocus();
            return false;
        }
        try {
            int capacite = Integer.parseInt(capaciteField.getText());
            int contenu = Integer.parseInt(contenuField.getText());
            if (contenu > capacite) {
                showWarningMessage("Le contenu ne peut pas dépasser la capacité");
                contenuField.requestFocus();
                return false;
            }
            if (contenu < 0) {
                showWarningMessage("Le contenu ne peut pas être négatif");
                contenuField.requestFocus();
                return false;
            }
            if (capacite <= 0) {
                showWarningMessage("La capacité doit être supérieure à zéro");
                capaciteField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Capacité ou contenu invalide (trop grand ou format incorrect)");
            return false;
        }
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
        return true;
    }

    private void remplirFormulaire(Bac bac) {
        if (bac == null) {
            clearFormulaire();
            return;
        }
        Couleur couleur = bac.getCouleurBac();
        couleurCombo.setValue(couleur);
        capaciteField.setText(String.valueOf(bac.getCapacite()));
        contenuField.setText(String.valueOf(bac.getContenu()));
        Adresse adresse = bac.getAdresseBac();
        if (adresse != null) {
            numeroField.setText(adresse.getNum() > 0 ? String.valueOf(adresse.getNum()) : "");
            rueField.setText(adresse.getNomRue() != null ? adresse.getNomRue() : "");
            codePostalField.setText(adresse.getCodeP() > 0 ? String.valueOf(adresse.getCodeP()) : "");
            villeField.setText(adresse.getVille() != null ? adresse.getVille() : "");
        } else {
            numeroField.clear();
            rueField.clear();
            codePostalField.clear();
            villeField.clear();
        }
    }

    private void clearFormulaire() {
        couleurCombo.setValue(null);
        capaciteField.clear();
        contenuField.clear();
        numeroField.clear();
        rueField.clear();
        codePostalField.clear();
        villeField.clear();
        selectedBac = null;
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);
        formTitleLabel.setText("Nouveau bac");
        messageLabel.setText("");
        messageLabel.getStyleClass().removeAll("error-message", "success-message", "warning-message", "info-message");
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
                Thread.sleep(5000); // 5 secondes
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