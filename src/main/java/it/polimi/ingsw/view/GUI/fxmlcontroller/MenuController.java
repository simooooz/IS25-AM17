package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the main menu interface.
 * Handles lobby creation, joining, and side panel management.
 *
 * @author Your Name
 * @version 1.0
 */
public class MenuController implements MessageHandler {

    @FXML private Label statusLabel;
    @FXML private AnchorPane sidePanel;
    @FXML private Label sidePanelTitle;
    @FXML private VBox sidePanelContent;

    /**
     * Initializes the menu interface components.
     */
    @FXML
    public void initialize() {
        setStatus("");
    }


    @FXML
    private void handleCreateLobby(ActionEvent event) {
        showCreateLobbyPanel();
    }

    @FXML
    private void handleJoinLobby(ActionEvent event) {
        showJoinLobbyPanel();
    }

    @FXML
    private void handleJoinRandom(ActionEvent event) {
        showJoinRandomPanel();
    }

    @FXML
    private void handleExitGame(ActionEvent event) {
        Platform.exit();
    }


    @FXML
    private void closeSidePanel(ActionEvent event) {
        sidePanel.setVisible(false);
        setStatus("");
    }

    /**
     * Displays the create lobby panel with input fields and mode selection.
     */
    private void showCreateLobbyPanel() {
        sidePanelContent.getChildren().clear();

        Label nameLabel = new Label("Lobby Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter lobby name");

        Label playersLabel = new Label("Max Players:");
        TextField playersField = new TextField("4");
        playersField.setPromptText("2-4 players");

        Label modeLabel = new Label("Select Game Mode:");

        // Mode selection buttons
        Button learnerModeBtn = new Button("Learner Mode");
        Button standardModeBtn = new Button("Standard Mode");

        final boolean[] selectedMode = {true}; // true = learner, false = standard

        // Initially set learner as selected
        learnerModeBtn.getStyleClass().add("toggle-button-selected");
        standardModeBtn.getStyleClass().add("toggle-button");

        learnerModeBtn.setOnAction(e -> {
            if (!selectedMode[0]) {
                selectedMode[0] = true;
                learnerModeBtn.getStyleClass().clear();
                learnerModeBtn.getStyleClass().add("toggle-button-selected");
                standardModeBtn.getStyleClass().clear();
                standardModeBtn.getStyleClass().add("toggle-button");
            }
        });

        standardModeBtn.setOnAction(e -> {
            if (selectedMode[0]) {
                selectedMode[0] = false;
                standardModeBtn.getStyleClass().clear();
                standardModeBtn.getStyleClass().add("toggle-button-selected");
                learnerModeBtn.getStyleClass().clear();
                learnerModeBtn.getStyleClass().add("toggle-button");
            }
        });

        // Create HBox for mode buttons
        javafx.scene.layout.HBox modeButtonsBox = new javafx.scene.layout.HBox(10);
        modeButtonsBox.setAlignment(javafx.geometry.Pos.CENTER);
        modeButtonsBox.getChildren().addAll(learnerModeBtn, standardModeBtn);

        Button createBtn = new Button("Create Lobby");
        createBtn.setOnAction(e -> {
            String lobbyName = nameField.getText().trim();
            String maxPlayers = playersField.getText().trim();

            // Validation
            if (lobbyName.isEmpty()) {
                setErrorStatus("Please enter a lobby name");
                return;
            }

            if (maxPlayers.isEmpty()) {
                setErrorStatus("Please enter max players");
                return;
            }

            try {
                int players = Integer.parseInt(maxPlayers);
                if (players < 2 || players > 4) {
                    setErrorStatus("Max players must be between 2 and 4");
                    return;
                }
            } catch (NumberFormatException ex) {
                setErrorStatus("Max players must be a valid number");
                return;
            }

            // Send create lobby request to server
            setStatus("Creating lobby...");
            App.getClientInstance().send(MessageType.CREATE_LOBBY, lobbyName, Integer.valueOf(maxPlayers), selectedMode[0]);
            closeSidePanel(null);
        });

        sidePanelContent.getChildren().addAll(
                nameLabel, nameField,
                playersLabel, playersField,
                modeLabel, modeButtonsBox,
                createBtn
        );

        sidePanel.setVisible(true);
        setStatus("Enter lobby details");
    }

    /**
     * Displays the join lobby panel with lobby ID input.
     */
    private void showJoinLobbyPanel() {
        sidePanelContent.getChildren().clear();

        Label idLabel = new Label("Lobby ID:");
        TextField idField = new TextField();
        idField.setPromptText("Enter lobby ID");

        idField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                joinLobbyById(idField.getText().trim());
            }
        });

        Button joinBtn = new Button("Join Lobby");
        joinBtn.setOnAction(e -> joinLobbyById(idField.getText().trim()));

        sidePanelContent.getChildren().addAll(idLabel, idField, joinBtn);
        sidePanel.setVisible(true);
        setStatus("Enter lobby ID");
    }

    /**
     * Displays the join random lobby panel with game mode selection.
     */
    private void showJoinRandomPanel() {
        sidePanelContent.getChildren().clear();

        Label modeLabel = new Label("Select Game Mode:");

        Button learnerModeBtn = new Button("Learner Mode");
        learnerModeBtn.setOnAction(e -> {
            setStatus("Finding learner random lobby...");
            App.getClientInstance().send(MessageType.JOIN_RANDOM_LOBBY, true);
            closeSidePanel(null);
        });

        Button standardModeBtn = new Button("Standard Mode");
        standardModeBtn.setOnAction(e -> {
            setStatus("Finding standard random lobby...");
            App.getClientInstance().send(MessageType.JOIN_RANDOM_LOBBY, false);
            closeSidePanel(null);
        });

        sidePanelContent.getChildren().addAll(modeLabel, learnerModeBtn, standardModeBtn);
        sidePanel.setVisible(true);
        setStatus("Choose your game mode");
    }

    /**
     * Attempts to join a lobby with the specified ID.
     */
    private void joinLobbyById(String lobbyId) {
        if (lobbyId.isEmpty()) {
            setErrorStatus("Please enter a lobby ID");
            return;
        }

        setStatus("Joining lobby...");
        App.getClientInstance().send(MessageType.JOIN_LOBBY, lobbyId);
        closeSidePanel(null);
    }

    /**
     * Updates the status label with a normal message.
     */
    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.getStyleClass().removeAll("error-text", "success-text");
            statusLabel.getStyleClass().add("status-text");
        }
    }

    /**
     * Updates the status label with an error message in red.
     */
    private void setErrorStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.getStyleClass().removeAll("status-text", "success-text");
            statusLabel.getStyleClass().add("error-text");
        }
    }


    /**
     * Navigates to a new scene by loading the specified FXML file and controller.
     */
    private <T extends MessageHandler> void navigateToScene(String fxmlPath, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            T controller = loader.getController();
            MessageDispatcher.getInstance().unregisterHandler(this);
            MessageDispatcher.getInstance().registerHandler(controller);

            Scene scene = sidePanel.getScene();
            if (scene != null) {
                scene.setRoot(view);
            }

        } catch (IOException e) {
            e.printStackTrace();
            setErrorStatus("Error loading interface");
        }
    }

    @Override
    public void handleMessage(GameEvent event) {
        switch (event.eventType()) {
            case CREATED_LOBBY_EVENT, JOINED_LOBBY_EVENT -> Platform.runLater(() -> {
                // Server confirmed lobby creation/join - navigate to waiting room
                navigateToScene("/fxml/waitingRoom.fxml", WaitingRoomController.class);
            });
            case ERROR -> Platform.runLater(() -> {
                // Server returned error
                setErrorStatus("Error: Unable to process request");
            });
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return List.of(
                MessageType.CREATED_LOBBY_EVENT,
                MessageType.JOINED_LOBBY_EVENT,
                MessageType.ERROR
        ).contains(messageType);
    }
}
