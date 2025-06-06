package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.messages.MessageType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CreateLobbyController implements MessageHandler {

    @FXML private TextField lobbyNameField;
    @FXML private ToggleButton btn2Players;
    @FXML private ToggleButton btn3Players;
    @FXML private ToggleButton btn4Players;
    @FXML private ToggleButton btnLearnerMode;
    @FXML private ToggleButton btnAdvancedMode;
    @FXML private Label errorLabel;
    @FXML private VBox vbox;
    @FXML private Label result_text;

    @FXML private ToggleGroup playersGroup;
    @FXML private ToggleGroup modeGroup;

    // Cache della Scene per evitare problemi di null
    private Scene cachedScene;

    @FXML
    public void initialize() {
        // Salva un riferimento alla Scene quando diventa disponibile
        Platform.runLater(() -> {
            cacheScene();
            // Retry se non è ancora disponibile
            if (cachedScene == null) {
                Platform.runLater(this::cacheScene);
            }
        });
    }

    private void cacheScene() {
        if (lobbyNameField != null && lobbyNameField.getScene() != null) {
            cachedScene = lobbyNameField.getScene();
        } else if (errorLabel != null && errorLabel.getScene() != null) {
            cachedScene = errorLabel.getScene();
        } else if (vbox != null && vbox.getScene() != null) {
            cachedScene = vbox.getScene();
        } else if (btn2Players != null && btn2Players.getScene() != null) {
            cachedScene = btn2Players.getScene();
        }
    }

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
        navigateToScene("/fxml/preGame.fxml", MainController.class);
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }

    // Metodo unificato per la navigazione
    private <T extends MessageHandler> void navigateToScene(String fxmlPath, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            T controller = loader.getController();
            MessageDispatcher.getInstance().unregisterHandler(this);
            MessageDispatcher.getInstance().registerHandler(controller);

            Scene scene = getCurrentScene();
            if (scene != null) {
                scene.setRoot(view);
            } else {
                System.err.println("Could not get current scene for navigation to " + fxmlPath);
                showError("Navigation error");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading view: " + fxmlPath);
        }
    }

    // Metodo helper per ottenere la Scene in modo sicuro
    private Scene getCurrentScene() {
        // Prima prova la cache
        if (cachedScene != null) {
            return cachedScene;
        }

        // Poi prova i componenti FXML e aggiorna la cache
        if (lobbyNameField != null && lobbyNameField.getScene() != null) {
            cachedScene = lobbyNameField.getScene();
            return cachedScene;
        } else if (errorLabel != null && errorLabel.getScene() != null) {
            cachedScene = errorLabel.getScene();
            return cachedScene;
        } else if (vbox != null && vbox.getScene() != null) {
            cachedScene = vbox.getScene();
            return cachedScene;
        } else if (btn2Players != null && btn2Players.getScene() != null) {
            cachedScene = btn2Players.getScene();
            return cachedScene;
        } else if (btnLearnerMode != null && btnLearnerMode.getScene() != null) {
            cachedScene = btnLearnerMode.getScene();
            return cachedScene;
        }

        return null;
    }

    // Modifica nel metodo handleMessage di CreateLobbyController

    @Override
    public void handleMessage(MessageType eventType, String username, Object... args) {
        switch (eventType) {
            case CREATE_LOBBY_OK -> {
                Platform.runLater(() -> {
                    System.out.println("Lobby created successfully!");
                    // NON reinoltrare il messaggio - lascia che il WaitingRoomController
                    // gestisca il proprio stato tramite i dati del client
                    navigateToScene("/fxml/waitingRoom.fxml", WaitingRoomController.class);
                });
            }
            case ERROR -> {
                if (args.length > 0) {
                    Platform.runLater(() -> showError(args[0].toString()));
                }
            }
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return messageType == MessageType.CREATE_LOBBY_OK || messageType == MessageType.ERROR;
    }
}