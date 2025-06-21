package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class JoinLobbyController implements MessageHandler {

    @FXML private TextField lobbyNameField;
    @FXML private Label errorLabel;
    @FXML private VBox vbox;
    @FXML private Label result_text;

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
        }
    }

    @FXML
    private void handleJoinLobby() {
        String name = lobbyNameField.getText().trim();

        if (name.isEmpty()) {
            showError("Please enter lobby name.");
        } else {
            if (errorLabel != null) {
                errorLabel.setVisible(false);
            }
            System.out.println("Joining lobby: " + name);
            App.getClientInstance().send(MessageType.JOIN_LOBBY, name);
        }
    }

    @FXML
    private void handleBack() {
        navigateToScene("/fxml/login.fxml", LoginController.class);
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }

    private void navigateToGame() {
        navigateToScene("/fxml/build.fxml", BuildController.class);
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
        }

        return null;
    }

    @Override
    public void handleMessage(GameEvent event) {
        switch (event.eventType()) {
            case JOINED_LOBBY_EVENT -> {
                Platform.runLater(() -> {
                    System.out.println("Successfully joined lobby!");
                    navigateToScene("/fxml/waitingRoom.fxml", WaitingRoomController.class);
                });
            }
            // FIX: Aggiunto supporto per GAME_STARTED_OK
            case MATCH_STARTED_EVENT -> {
                Platform.runLater(() -> {
                    System.out.println("Game started automatically after joining!");
                    navigateToGame();
                });
            }
            case ERROR -> {
                if (event.getArgs().length > 0) {
                    Platform.runLater(() -> showError(event.getArgs()[0].toString()));
                }
            }
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        // FIX: Aggiunto GAME_STARTED_OK alla lista dei messaggi gestibili
        return messageType == MessageType.JOINED_LOBBY_EVENT ||
                messageType == MessageType.MATCH_STARTED_EVENT ||
                messageType == MessageType.ERROR;
    }
}
