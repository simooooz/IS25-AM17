package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.client.model.game.ClientLobby;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.common.model.events.game.ErrorEvent;
import it.polimi.ingsw.common.model.events.game.MatchStartedEvent;
import it.polimi.ingsw.common.model.events.game.SyncAllEvent;
import it.polimi.ingsw.common.model.events.lobby.JoinedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
import it.polimi.ingsw.view.GUI.MessageHandler;
import it.polimi.ingsw.view.GUI.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * Controller for the waiting room interface.
 * Handles lobby state updates and player management.
 */
public class WaitingRoomController implements MessageHandler {

    private Client client;

    @FXML private Label lobbyNameLabel;
    @FXML private Label gameModeLabel;
    @FXML private Label maxPlayersLabel;
    @FXML private Label playerCountLabel;
    @FXML private Label statusLabel;
    @FXML private VBox playersContainer;
    @FXML private HBox loadingIndicator;

    private List<String> currentPlayers;

    @FXML
    public void initialize() {
        MessageDispatcher.getInstance().registerHandler(this);
        this.client = App.getClientInstance();
        currentPlayers = new ArrayList<>();

        ClientLobby lobby = client.getLobby();
        if (lobby != null) {
            setLobbyInfo(lobby.getGameID(), lobby.getMaxPlayers(), lobby.isLearnerMode());

            currentPlayers.clear();
            currentPlayers.addAll(lobby.getPlayers());
            updatePlayersList();
        }
    }

    /**
     * Sets the lobby information and updates the UI.
     */
    public void setLobbyInfo(String lobbyName, int maxPlayers, boolean isLearnerMode) {
        Platform.runLater(() -> {
            if (lobbyNameLabel != null)
                lobbyNameLabel.setText("Lobby: " + lobbyName);
            if (maxPlayersLabel != null)
                maxPlayersLabel.setText("Max Players: " + maxPlayers);
            if (gameModeLabel != null)
                gameModeLabel.setText("Mode: " + (isLearnerMode ? "Learner" : "Standard"));
            updateStatus(maxPlayers);
        });
    }

    /**
     * Updates the status message based on current player count.
     */
    private void updateStatus(int maxPlayers) {
        if (statusLabel == null || loadingIndicator == null) return;

        if (currentPlayers.size() == maxPlayers) {
            statusLabel.setText("Lobby is full! Game will start automatically.");
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 16px; -fx-font-weight: bold;");
            loadingIndicator.setVisible(false);
        } else {
            statusLabel.setText("Waiting for players to join... (" + currentPlayers.size() + "/" + maxPlayers + ")");
            statusLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 16px; -fx-font-weight: bold;");
            loadingIndicator.setVisible(true);
        }
    }

    /**
     * Handles the leave lobby button action.
     */
    @FXML
    private void handleLeaveLobby() { client.send(MessageType.LEAVE_GAME); }

    /**
     * Updates the players list display.
     */
    private void updatePlayersList() {
        ClientLobby lobby = client.getLobby();
        if (lobby == null) return;

        int maxPlayers = lobby.getMaxPlayers();

        // Clear current player displays
        playersContainer.getChildren().clear();

        // Add each player
        for (int i = 0; i < currentPlayers.size(); i++) {
            String playerName = currentPlayers.get(i);
            Label playerLabel = new Label((i + 1) + ". " + playerName);
            playerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
            playersContainer.getChildren().add(playerLabel);
        }

        // Add empty slots
        for (int i = currentPlayers.size(); i < maxPlayers; i++) {
            Label emptySlot = new Label((i + 1) + ". Waiting for player...");
            emptySlot.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-font-style: italic;");
            playersContainer.getChildren().add(emptySlot);
        }

        // Update player count
        playerCountLabel.setText(currentPlayers.size() + "/" + maxPlayers + " players");

        updateStatus(maxPlayers);
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
    }
    
    private void skipBuildPhase() {
        Map<Integer, ImageView> componentMap = new HashMap<>();
        List<ClientComponent> components = client.getGameController().getModel().getBoard().getMapIdComponents().values().stream().toList();

        for (ClientComponent c : components) {
            if (c.isInserted()) { // Only inserted components
                ImageView iv = new ImageView();
                iv.setFitWidth(70);
                iv.setFitHeight(70);
                iv.setPreserveRatio(true);
                iv.setId("component_" + c.getId());

                String imagePath = "/images/tiles/GT-new_tiles_16_for web" + c.getId() + ".jpg";
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
                iv.setImage(image);
                iv.setRotate(90*c.getRotationsCounter());

                componentMap.put(c.getId(), iv);
            }
        }
        System.out.println(componentMap.size());
        SceneManager.navigateToScene("/fxml/gameFlight.fxml", this, (FlightPhaseController controller) ->
            controller.setImageMap(componentMap)
        );
    }

    @Override
    public void handleMessage(Event event) {
        switch (event) {
            case JoinedLobbyEvent e -> {
                String playerName = e.username();
                if (!currentPlayers.contains(playerName)) {
                    currentPlayers.add(playerName);
                    updatePlayersList();
                }
            }
            case LeftLobbyEvent e -> {
                if (e.getArgs().length > 0) {
                    String playerName = e.username();
                    String currentUsername = client.getUsername();

                    if (playerName.equals(currentUsername))
                        SceneManager.navigateToScene("/fxml/menu.fxml", this, null);
                    else {
                        currentPlayers.remove(playerName);
                        updatePlayersList();
                    }
                }
            }
            case MatchStartedEvent _ -> SceneManager.navigateToScene("/fxml/build.fxml", this, null);
            case SyncAllEvent e -> {
                if (e.dto().playersState.containsValue(PlayerState.BUILD) || e.dto().playersState.containsValue(PlayerState.LOOK_CARD_PILE))
                    SceneManager.navigateToScene("/fxml/build.fxml", this, null);
                else
                    skipBuildPhase();
            }
            case ErrorEvent e -> showError(e.message());
            default -> {}
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return List.of(
                MessageType.JOINED_LOBBY_EVENT,
                MessageType.LEFT_LOBBY_EVENT,
                MessageType.MATCH_STARTED_EVENT,
                MessageType.SYNC_ALL_EVENT,
                MessageType.ERROR
        ).contains(messageType);
    }

}
