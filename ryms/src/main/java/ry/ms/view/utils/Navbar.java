package ry.ms.view.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Navbar {
    private BorderPane view;
    private NavbarController controller;

    public Navbar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/utils/fxml/Navbar.fxml"));
            view = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            view = new BorderPane(new Label("Erreur: Impossible de charger la barre de navigation."));
        }
    }

    public Pane getView() {
        return view;
    }

    public void setOnLoginClick(Runnable onLoginClick) {
        if (controller != null) {
            controller.setOnLoginClick(onLoginClick);
        }
    }

    public void setOnRegisterClick(Runnable onRegisterClick) {
        if (controller != null) {
            controller.setOnRegisterClick(onRegisterClick);
        }
    }
}
