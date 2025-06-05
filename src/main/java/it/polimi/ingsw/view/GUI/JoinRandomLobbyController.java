package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.messages.MessageType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;

public class JoinRandomLobbyController implements MessageHandler {

    @FXML private ToggleButton btnLearnerMode;
    @FXML private ToggleButton btnAdvancedMode;
    @FXML private Label errorLabel;

    @FXML private ToggleGroup modeGroup;

    @FXML
    private void handleJoinRandom() {
        Toggle selectedMode = modeGroup.getSelectedToggle();

        if (selectedMode == null) {
            showError("Please select preferred game mode.");
        } else {
            errorLabel.setVisible(false);
            boolean isLearner = ((ToggleButton) selectedMode).getId().equals(btnLearnerMode.getId());

            System.out.println("Joining random lobby with learner mode: " + isLearner);
            JavaFxInterface.getClientInstance().send(MessageType.JOIN_RANDOM_LOBBY, isLearner);
        }
    }

    @FXML
    private void handleBack() {
        try {
            // Torna alla schermata principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/preGame.fxml"));
            Parent mainView = loader.load();

            MainController mainController = loader.getController();
            MessageDispatcher.getInstance().unregisterHandler(this); // Rimuovi questo handler
            MessageDispatcher.getInstance().registerHandler(mainController); // Registra il main controller

            Scene scene = errorLabel.getScene();
            scene.setRoot(mainView);

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error returning to main menu");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @Override
    public void handleMessage(MessageType eventType, String username, Object... args) {
        switch (eventType) {
            case JOIN_LOBBY_OK -> {
                // Lobby joinata con successo
                javafx.application.Platform.runLater(() -> {
                    System.out.println("Successfully joined random lobby!");
                    // Qui potresti navigare alla lobby/waiting room
                });
            }
            case ERROR -> {
                if (args.length > 0) {
                    javafx.application.Platform.runLater(() -> showError(args[0].toString()));
                }
            }
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return messageType == MessageType.JOIN_LOBBY_OK || messageType == MessageType.ERROR;
    }
}