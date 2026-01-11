package ry.ms.view.user.login;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LoginFrame {

    private VBox view;
    private LoginController controller;

    public LoginFrame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/user/fxml/Login.fxml"));
            view = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            view = new VBox(new Label("Erreur: Impossible de charger l'écran de connexion."));
        }
    }

    public Pane getView() {
        return view;
    }

    /**
     * Définit une action à exécuter lorsque la connexion est réussie.
     * 
     * @param onSuccess Le Runnable à exécuter.
     */
    public void setOnSuccess(Runnable onSuccess) {
        if (controller != null) {
            controller.setOnSuccess(onSuccess);
        }
    }

    public void setOnRegisterRequest(Runnable onRegisterRequest) {
        if (controller != null) {
            controller.setOnRegisterRequest(onRegisterRequest);
        }
    }

    public void setOnForgotPasswordRequest(Runnable onForgotPasswordRequest) {
        if (controller != null) {
            controller.setOnForgotPasswordRequest(onForgotPasswordRequest);
        }
    }
}