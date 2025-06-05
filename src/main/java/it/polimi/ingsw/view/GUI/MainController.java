package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.messages.MessageType;
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

    @FXML
    public void initialize() {
        TextField input = new TextField();
        input.setPromptText("Insert username and press ENTER");
        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String username = input.getText().trim();
                if (username.length() < 3 || username.length() > 18) {
                    result_text.setText("Username must be between 3 and 18 characters");
                    event.consume();
                    return;
                }
                JavaFxInterface.getClientInstance().send(MessageType.SET_USERNAME, username);
            }
        });

        vbox.getChildren().add(input);
        command_text.setText("Welcome to Galaxy Trucker\nFirst of all type an username");
    }

    public void setLobbySelection() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);

        Button createLobbyButton = new Button();
        createLobbyButton.setText("Create lobby");
        createLobbyButton.setOnMouseClicked(event -> loadCreateLobbyView());

        Button joinLobbyButton = new Button();
        joinLobbyButton.setText("Join lobby");
        joinLobbyButton.setOnMouseClicked(event -> loadJoinLobbyView());

        Button joinRandomLobbyButton = new Button();
        joinRandomLobbyButton.setText("Join random lobby");
        joinRandomLobbyButton.setOnMouseClicked(event -> loadJoinRandomLobbyView());

        hbox.getChildren().addAll(createLobbyButton, joinLobbyButton, joinRandomLobbyButton);
        vbox.getChildren().add(hbox);
    }

    private void loadCreateLobbyView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/createLobby.fxml"));
            Parent createLobbyView = loader.load();

            CreateLobbyController createLobbyController = loader.getController();
            MessageDispatcher.getInstance().unregisterHandler(this); // Rimuovi questo handler
            MessageDispatcher.getInstance().registerHandler(createLobbyController);

            // Sostituisci la vista corrente
            Scene scene = vbox.getScene();
            scene.setRoot(createLobbyView);

        } catch (IOException e) {
            e.printStackTrace();
            result_text.setText("Error loading create lobby view");
        }
    }

    private void loadJoinLobbyView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/joinLobby.fxml"));
            Parent joinLobbyView = loader.load();

            JoinLobbyController joinLobbyController = loader.getController();
            MessageDispatcher.getInstance().unregisterHandler(this); // Rimuovi questo handler
            MessageDispatcher.getInstance().registerHandler(joinLobbyController);

            // Sostituisci la vista corrente
            Scene scene = vbox.getScene();
            scene.setRoot(joinLobbyView);

        } catch (IOException e) {
            e.printStackTrace();
            result_text.setText("Error loading join lobby view");
        }
    }

    private void loadJoinRandomLobbyView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/joinRandomLobby.fxml"));
            Parent joinRandomLobbyView = loader.load();

            JoinRandomLobbyController joinRandomLobbyController = loader.getController();
            MessageDispatcher.getInstance().unregisterHandler(this); // Rimuovi questo handler
            MessageDispatcher.getInstance().registerHandler(joinRandomLobbyController);

            // Sostituisci la vista corrente
            Scene scene = vbox.getScene();
            scene.setRoot(joinRandomLobbyView);

        } catch (IOException e) {
            e.printStackTrace();
            result_text.setText("Error loading join random lobby view");
        }
    }

    private void loadGameView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
            Parent gameView = loader.load();

            BuildController gameController = loader.getController();
            MessageDispatcher.getInstance().unregisterHandler(this); // Rimuovi questo handler
            MessageDispatcher.getInstance().registerHandler(gameController);

            // Sostituisci la vista corrente
            Scene scene = vbox.getScene();
            scene.setRoot(gameView);

        } catch (IOException e) {
            e.printStackTrace();
            result_text.setText("Error loading game view");
        }
    }

    @Override
    public void handleMessage(MessageType eventType, String username, Object... args) {
        switch (eventType) {
            case USERNAME_OK -> {
                javafx.application.Platform.runLater(() -> setLobbySelection());
            }
            case CREATE_LOBBY_OK -> {
                // Puoi gestire la creazione della lobby qui se necessario
                javafx.application.Platform.runLater(() -> {
                    System.out.println("Lobby created successfully from MainController");
                });
            }
            case JOIN_LOBBY_OK -> {
                // Gestisce il join della lobby
                javafx.application.Platform.runLater(() -> {
                    System.out.println("Joined lobby successfully from MainController");
                    // Potresti caricare una vista della lobby/waiting room
                });
            }
            case JOIN_RANDOM_LOBBY_OK -> {
                // Gestisce il join random della lobby
                javafx.application.Platform.runLater(() -> {
                    System.out.println("Joined random lobby successfully from MainController");
                    // Potresti caricare una vista della lobby/waiting room
                });
            }
            case GAME_STARTED_OK -> {
                javafx.application.Platform.runLater(() -> loadGameView());
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