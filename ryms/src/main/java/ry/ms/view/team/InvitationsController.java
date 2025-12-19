package ry.ms.view.team;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ry.ms.businessLogic.team.models.Invitation;
import ry.ms.persistLogic.team.postgres.InvitationDAOPostgres;

import java.util.List;

public class InvitationsController {

    @FXML private ListView<Invitation> invitationListView;
    @FXML private Label statusLabel;

    private final TeamController controller = new TeamController();
    private final String myEmail = UserSession.getInstance().getUserEmail();

    public void initialize() {
        refreshList();
    }

    private void refreshList() {
        try {
            // Note: Normalement on ne devrait pas appeler le DAO directement ici, 
            // mais exposer une méthode "getMyInvitations" dans le controller.
            // Pour simplifier l'exercice :
            List<Invitation> list = new InvitationDAOPostgres().findPendingByReceiver(myEmail);
            invitationListView.getItems().setAll(list);
            
            // Custom Cell Factory pour afficher un texte joli
            invitationListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Invitation item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("Team ID: " + item.getTeamId() + " (de " + item.getSender() + ")");
                    }
                }
            });
            
        } catch (Exception e) {
            statusLabel.setText("Erreur chargement: " + e.getMessage());
        }
    }

    @FXML
    private void handleAccept() {
        Invitation selected = invitationListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            controller.acceptInvitation(selected.getId());
            statusLabel.setText("Invitation acceptée !");
            refreshList();
            // La fenêtre se fermera et mettra à jour le dashboard
        } catch (Exception e) {
            statusLabel.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleReject() {
        Invitation selected = invitationListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        try {
            controller.rejectInvitation(selected.getId());
            statusLabel.setText("Invitation refusée.");
            refreshList();
        } catch (Exception e) {
            statusLabel.setText("Erreur : " + e.getMessage());
        }
    }
}