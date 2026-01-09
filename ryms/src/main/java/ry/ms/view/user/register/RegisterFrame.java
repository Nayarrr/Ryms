package ry.ms.view.user.register;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RegisterFrame {

    private VBox view;
    private RegisterController controller;

    public RegisterFrame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/user/fxml/Register.fxml"));
            view = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            view = new VBox(new Label("Erreur: Impossible de charger l'écran d'inscription."));
        }
    }

    public Pane getView() {
        return view;
    }

    public void setOnSuccess(Runnable onSuccess) {
        if (controller != null) {
            controller.setOnSuccess(onSuccess);
        }
    }

    public void setOnLoginRequest(Runnable onLoginRequest) {
        if (controller != null) {
            controller.setOnLoginRequest(onLoginRequest);
        }
    }
}