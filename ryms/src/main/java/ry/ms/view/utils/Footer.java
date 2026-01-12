package ry.ms.view.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Footer {
    private HBox view;

    public Footer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/utils/fxml/Footer.fxml"));
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            view = new HBox(new Label("Erreur: Impossible de charger le pied de page."));
        }
    }

    public Pane getView() { return view; }
}
