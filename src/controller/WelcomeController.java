package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeController {

    @FXML
    private Label welcomeLabel;

    public void setUserInfo(String accountType, String name) {
        welcomeLabel.setText("Hello " + name + " !");
    }
}