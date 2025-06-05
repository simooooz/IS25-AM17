package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.messages.MessageType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;

public class CreateLobbyController implements MessageHandler {

    @FXML private TextField lobbyNameField;
    @FXML private ToggleButton btn2Players;
    @FXML private ToggleButton btn3Players;
    @FXML private ToggleButton btn4Players;
    @FXML private ToggleButton btnLearnerMode;
    @FXML private ToggleButton btnAdvancedMode;
    @FXML private Label errorLabel;

    @FXML private ToggleGroup playersGroup;
    @FXML private ToggleGroup modeGroup;

    @FXML
    private void handleCreateLobby() {
        String name = lobbyNameField.getText().trim();
        boolean nameValid = name.length() >= 3 && name.length() <= 18;

        Toggle selectedPlayer = playersGroup.getSelectedToggle();
        Toggle selectedMode = modeGroup.getSelectedToggle();

        if (!nameValid)
            showError("Lobby name must be between 3 and 18 characters.");
        else if (selectedPlayer == null)
            showError("Please select number of players.");
        else if (selectedMode == null)
            showError("Please select game mode.");
        else {
            errorLabel.setVisible(false);
            int players = ((ToggleButton) selectedPlayer).getId().equals(btn2Players.getId()) ? 2 :
                    (((ToggleButton) selectedPlayer).getId().equals(btn3Players.getId()) ? 3 : 4);
            boolean isLearner = ((ToggleButton) selectedMode).getId().equals(btnLearnerMode.getId());

            System.out.println("Creating lobby: " + name + " with " + players + " players, learner: " + isLearner);
            JavaFxInterface.getClientInstance().send(MessageType.CREATE_LOBBY, name, players, isLearner);
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

            Scene scene = lobbyNameField.getScene();
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
            case CREATE_LOBBY_OK -> {
                // Lobby creata con successo, potresti navigare a una nuova vista
                javafx.application.Platform.runLater(() -> {
                    // Opzionalmente potresti caricare una vista "waiting room" o tornare al menu
                    System.out.println("Lobby created successfully!");
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
        return messageType == MessageType.CREATE_LOBBY_OK || messageType == MessageType.ERROR;
    }
}