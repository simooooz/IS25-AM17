package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.client.model.game.ClientLobby;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the waiting room interface.
 * Handles lobby state updates and player management.
 */
public class WaitingRoomController implements MessageHandler {

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
        currentPlayers = new ArrayList<>();

        Platform.runLater(() -> {
            initializeLobbyState();
        });
    }

    /**
     * Initializes the lobby state from the client's current lobby data.
     */
    private void initializeLobbyState() {
        ClientLobby lobby = App.getClientInstance().getLobby();
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
            if (lobbyNameLabel != null) {
                lobbyNameLabel.setText("Lobby: " + lobbyName);
            }
            if (maxPlayersLabel != null) {
                maxPlayersLabel.setText("Max Players: " + maxPlayers);
            }
            if (gameModeLabel != null) {
                gameModeLabel.setText("Mode: " + (isLearnerMode ? "Learner" : "Standard"));
            }
            updateStatus(maxPlayers);
        });
    }

    /**
     * Updates the status message based on current player count.
     */
    private void updateStatus(int maxPlayers) {
        Platform.runLater(() -> {
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
        });
    }

    /**
     * Handles the leave lobby button action.
     */
    @FXML
    private void handleLeaveLobby() {
        App.getClientInstance().send(MessageType.LEAVE_GAME);
        // Don't navigate immediately - wait for server response
    }

    /**
     * Updates the players list display.
     */
    private void updatePlayersList() {
        Platform.runLater(() -> {
            if (playersContainer == null || playerCountLabel == null) return;

            ClientLobby lobby = App.getClientInstance().getLobby();
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
        });
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

            Scene scene = findCurrentScene();
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

    /**
     * Finds the current scene by checking all available components.
     */
    private Scene findCurrentScene() {
        // Prova tutti i componenti FXML disponibili
        if (lobbyNameLabel != null && lobbyNameLabel.getScene() != null) {
            return lobbyNameLabel.getScene();
        }
        if (statusLabel != null && statusLabel.getScene() != null) {
            return statusLabel.getScene();
        }
        if (playersContainer != null && playersContainer.getScene() != null) {
            return playersContainer.getScene();
        }
        if (loadingIndicator != null && loadingIndicator.getScene() != null) {
            return loadingIndicator.getScene();
        }
        if (gameModeLabel != null && gameModeLabel.getScene() != null) {
            return gameModeLabel.getScene();
        }
        if (maxPlayersLabel != null && maxPlayersLabel.getScene() != null) {
            return maxPlayersLabel.getScene();
        }
        if (playerCountLabel != null && playerCountLabel.getScene() != null) {
            return playerCountLabel.getScene();
        }
        return null;
    }

    /**
     * Displays error message in the status label.
     */
    private void showError(String message) {
        if (statusLabel != null) {
            Platform.runLater(() -> {
                statusLabel.setText("Error: " + message);
                statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
            });
        }
    }

    @Override
    public void handleMessage(GameEvent event) {
        switch (event.eventType()) {
            case JOINED_LOBBY_EVENT -> {
                // Another player joined the lobby
                if (event.getArgs().length > 0) {
                    String playerName = event.getArgs()[0].toString();
                    if (!currentPlayers.contains(playerName)) {
                        currentPlayers.add(playerName);
                        updatePlayersList();
                        System.out.println("Player " + playerName + " joined the lobby");
                    }
                }
            }
            case LEFT_LOBBY_EVENT -> {
                // A player left the lobby
                if (event.getArgs().length > 0) {
                    String playerName = event.getArgs()[0].toString();
                    String currentUsername = App.getClientInstance().getUsername();

                    if (playerName.equals(currentUsername)) {
                        // This player left - navigate back to main menu
                        Platform.runLater(() -> navigateToScene("/fxml/menu.fxml", MenuController.class));
                    } else {
                        // Another player left - update the list
                        currentPlayers.remove(playerName);
                        updatePlayersList();
                        System.out.println("Player " + playerName + " left the lobby");
                    }
                }
            }
            case MATCH_STARTED_EVENT -> {
                // Game is starting - navigate to build screen
                Platform.runLater(() -> navigateToScene("/fxml/build.fxml", BuildController.class));
            }
            case ERROR -> {
                // Server returned an error
                if (event.getArgs().length > 0) {
                    Platform.runLater(() -> showError(event.getArgs()[0].toString()));
                } else {
                    Platform.runLater(() -> showError("Unknown error occurred"));
                }
            }
            default -> {
                System.out.println("Unhandled message in WaitingRoom: " + event.eventType());
            }
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return List.of(
                MessageType.JOINED_LOBBY_EVENT,
                MessageType.LEFT_LOBBY_EVENT,
                MessageType.MATCH_STARTED_EVENT,
                MessageType.ERROR
        ).contains(messageType);
    }
}
