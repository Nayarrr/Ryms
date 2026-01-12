package ry.ms.view.user.profile;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class ProfileFrame {
    private Parent view;
    private ProfileController controller;

    public ProfileFrame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/user/fxml/Profile.fxml"));
            this.view = loader.load();
            this.controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Parent getView() {
        return view;
    }

    public void setOnBackRequest(Runnable onBackRequest) {
        if (controller != null) {
            controller.setOnBackRequest(onBackRequest);
        }
    }

    public void setOnLogoutRequest(Runnable onLogoutRequest) {
        if (controller != null) {
            controller.setOnLogoutRequest(onLogoutRequest);
        }
    }
}
