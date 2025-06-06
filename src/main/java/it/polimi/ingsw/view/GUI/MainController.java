package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.messages.MessageType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class MainController implements MessageHandler {

    @FXML private VBox vbox;
    @FXML private Label command_text;
    @FXML private Label result_text;

    // Cache della Scene per evitare problemi di null
    private Scene cachedScene;

    @FXML
    public void initialize() {
        TextField input = new TextField();
        input.setPromptText("Insert username and press ENTER");
        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String username = input.getText().trim();
                if (username.length() < 3 || username.length() > 18) {
                    if (result_text != null) {
                        result_text.setText("Username must be between 3 and 18 characters");
                    }
                    event.consume();
                    return;
                }
                JavaFxInterface.getClientInstance().send(MessageType.SET_USERNAME, username);
            }
        });

        if (vbox != null) {
            vbox.getChildren().add(input);
        }
        if (command_text != null) {
            command_text.setText("Welcome to Galaxy Trucker\nFirst of all type an username");
        }

        // Cache della Scene
        Platform.runLater(() -> {
            cacheScene();
            if (cachedScene == null) {
                Platform.runLater(this::cacheScene);
            }
        });
    }

    private void cacheScene() {
        if (vbox != null && vbox.getScene() != null) {
            cachedScene = vbox.getScene();
        } else if (command_text != null && command_text.getScene() != null) {
            cachedScene = command_text.getScene();
        } else if (result_text != null && result_text.getScene() != null) {
            cachedScene = result_text.getScene();
        }
    }

    public void setLobbySelection() {
        if (vbox == null) return;

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);

        Button createLobbyButton = new Button();
        createLobbyButton.setText("Create lobby");
        createLobbyButton.setOnMouseClicked(event -> navigateToScene("/fxml/createLobby.fxml", CreateLobbyController.class));

        Button joinLobbyButton = new Button();
        joinLobbyButton.setText("Join lobby");
        joinLobbyButton.setOnMouseClicked(event -> navigateToScene("/fxml/joinLobby.fxml", JoinLobbyController.class));

        Button joinRandomLobbyButton = new Button();
        joinRandomLobbyButton.setText("Join random lobby");
        joinRandomLobbyButton.setOnMouseClicked(event -> navigateToScene("/fxml/joinRandomLobby.fxml", JoinRandomLobbyController.class));

        hbox.getChildren().addAll(createLobbyButton, joinLobbyButton, joinRandomLobbyButton);
        vbox.getChildren().add(hbox);
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
        if (vbox != null && vbox.getScene() != null) {
            cachedScene = vbox.getScene();
            return cachedScene;
        } else if (command_text != null && command_text.getScene() != null) {
            cachedScene = command_text.getScene();
            return cachedScene;
        } else if (result_text != null && result_text.getScene() != null) {
            cachedScene = result_text.getScene();
            return cachedScene;
        }

        return null;
    }

    private void showError(String message) {
        if (result_text != null) {
            result_text.setText(message);
        }
    }

    @Override
    public void handleMessage(MessageType eventType, String username, Object... args) {
        switch (eventType) {
            case USERNAME_OK -> {
                Platform.runLater(() -> setLobbySelection());
            }
            case GAME_STARTED_OK -> {
                Platform.runLater(() -> navigateToScene("/fxml/game.fxml", BuildController.class));
            }
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return List.of(
                MessageType.SET_USERNAME,
                MessageType.USERNAME_OK,
                MessageType.CREATE_LOBBY_OK,
                MessageType.JOIN_LOBBY_OK,
                MessageType.JOIN_RANDOM_LOBBY_OK,
                MessageType.LEAVE_GAME_OK,
                MessageType.GAME_STARTED_OK
        ).contains(messageType);
    }
}