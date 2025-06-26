package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.common.model.events.game.ErrorEvent;
import it.polimi.ingsw.common.model.events.lobby.SetLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.JoinedLobbyEvent;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
import it.polimi.ingsw.view.GUI.MessageHandler;
import it.polimi.ingsw.view.GUI.SceneManager;
import javafx.animation.PauseTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

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
    @FXML private VBox sidePanelContent;

    /**
     * Initializes the menu interface components.
     */
    @FXML
    public void initialize() {
        MessageDispatcher.getInstance().registerHandler(this);
        setStatus("");
    }


    @FXML
    private void handleCreateLobby() {
        showCreateLobbyPanel();
    }

    @FXML
    private void handleJoinLobby() {
        showJoinLobbyPanel();
    }

    @FXML
    private void handleJoinRandom() {
        showJoinRandomPanel();
    }

    @FXML
    private void handleExitGame() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void closeSidePanel() {
        sidePanel.setVisible(false);
        setStatus("");
    }

    /**
     * Displays create lobby panel with input fields and mode selection.
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

        learnerModeBtn.setOnAction(_ -> {
            if (!selectedMode[0]) {
                selectedMode[0] = true;
                learnerModeBtn.getStyleClass().clear();
                learnerModeBtn.getStyleClass().add("toggle-button-selected");
                standardModeBtn.getStyleClass().clear();
                standardModeBtn.getStyleClass().add("toggle-button");
            }
        });

        standardModeBtn.setOnAction(_ -> {
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

        Button createBtn = createCreateLobbyButton(nameField, playersField, selectedMode);

        sidePanelContent.getChildren().addAll(
            nameLabel, nameField,
            playersLabel, playersField,
            modeLabel, modeButtonsBox,
            createBtn
        );

        sidePanel.setVisible(true);
        setStatus("Enter lobby details");
    }

    private Button createCreateLobbyButton(TextField nameField, TextField playersField, boolean[] selectedMode) {
        Button createBtn = new Button("Create Lobby");
        createBtn.setOnAction(_ -> {
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
            closeSidePanel();
        });
        return createBtn;
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
        joinBtn.setOnAction(_ -> joinLobbyById(idField.getText().trim()));

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
        learnerModeBtn.setOnAction(_ -> {
            setStatus("Finding learner random lobby...");
            App.getClientInstance().send(MessageType.JOIN_RANDOM_LOBBY, true);
            closeSidePanel();
        });

        Button standardModeBtn = new Button("Standard Mode");
        standardModeBtn.setOnAction(_ -> {
            setStatus("Finding standard random lobby...");
            App.getClientInstance().send(MessageType.JOIN_RANDOM_LOBBY, false);
            closeSidePanel();
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
        closeSidePanel();
    }

    /**
     * Updates the status label with a normal message.
     */
    private void setStatus(String message) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("error-text", "success-text");
        statusLabel.getStyleClass().add("status-text");
    }

    /**
     * Updates the status label with an error message in red.
     */
    private void setErrorStatus(String message) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-text", "success-text");
        statusLabel.getStyleClass().add("error-text");
    }

    @Override
    public void handleMessage(Event event) {
        switch (event) {
            case SetLobbyEvent _, JoinedLobbyEvent _ -> SceneManager.navigateToScene("/fxml/waitingRoom.fxml", this, null);
            case ErrorEvent e -> {
                setErrorStatus(e.message());
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(_ -> setErrorStatus(""));
                pause.play();
            }
            default -> {}
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
