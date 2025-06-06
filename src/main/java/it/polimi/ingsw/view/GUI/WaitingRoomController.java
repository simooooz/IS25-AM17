package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.messages.MessageType;
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

public class WaitingRoomController implements MessageHandler {

    @FXML private Label lobbyNameLabel;
    @FXML private Label gameModeLabel;
    @FXML private Label maxPlayersLabel;
    @FXML private Label playerCountLabel;
    @FXML private Label statusLabel;
    @FXML private VBox playersContainer;
    @FXML private HBox loadingIndicator;

    // Cache della Scene per evitare problemi di null
    private Scene cachedScene;

    private String lobbyName;
    private int maxPlayers;
    private boolean isLearnerMode;
    private List<String> currentPlayers;

    @FXML
    public void initialize() {
        currentPlayers = new ArrayList<>();
        setupLoadingAnimation();

        // Cache della Scene
        Platform.runLater(() -> {
            cacheScene();
            if (cachedScene == null) {
                Platform.runLater(this::cacheScene);
            }

            // AGGIUNTA: Inizializza lo stato della lobby se disponibile
            initializeLobbyState();
        });
    }

    private void initializeLobbyState() {
        // Controlla se abbiamo già una lobby disponibile
        if (JavaFxInterface.getClientInstance().getLobby() != null) {
            Lobby lobby = JavaFxInterface.getClientInstance().getLobby();
            String lobbyName = lobby.getGameID();
            int players = lobby.getMaxPlayers();
            boolean isLearnerMode = lobby.isLearnerMode();

            setLobbyInfo(lobbyName, players, isLearnerMode);

            currentPlayers.clear();
            currentPlayers.addAll(lobby.getPlayers());
            updatePlayersList();
        }
    }

    private void cacheScene() {
        if (lobbyNameLabel != null && lobbyNameLabel.getScene() != null) {
            cachedScene = lobbyNameLabel.getScene();
        } else if (statusLabel != null && statusLabel.getScene() != null) {
            cachedScene = statusLabel.getScene();
        } else if (playersContainer != null && playersContainer.getScene() != null) {
            cachedScene = playersContainer.getScene();
        }
    }

    private void setupLoadingAnimation() {
        // Simple loading animation - you can enhance this with Timeline and transitions
        // For now, it's just visual
    }

    public void setLobbyInfo(String lobbyName, int maxPlayers, boolean isLearnerMode) {
        this.lobbyName = lobbyName;
        this.maxPlayers = maxPlayers;
        this.isLearnerMode = isLearnerMode;

        Platform.runLater(() -> {
            if (lobbyNameLabel != null) {
                lobbyNameLabel.setText("Lobby: " + lobbyName);
            }
            if (maxPlayersLabel != null) {
                maxPlayersLabel.setText("Max Players: " + maxPlayers);
            }
            if (gameModeLabel != null) {
                // FIX: Correzione logica per learner/advanced mode
                gameModeLabel.setText("Mode: " + (isLearnerMode ? "Learner" : "Advanced"));
            }
            updateStatus();
        });
    }


    private void updateStatus() {
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

    @FXML
    private void handleLeaveLobby() {
        JavaFxInterface.getClientInstance().send(MessageType.LEAVE_GAME);
        // Navigate back to main menu
        navigateToMainMenu();
    }

    private void navigateToMainMenu() {
        navigateToScene("/fxml/preGame.fxml", MainController.class);
    }

    private void navigateToGame() {
        navigateToScene("/fxml/buildPage.fxml", BuildController.class);
    }

    // Metodo unificato per la navigazione (come negli altri controller)
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
        if (lobbyNameLabel != null && lobbyNameLabel.getScene() != null) {
            cachedScene = lobbyNameLabel.getScene();
            return cachedScene;
        } else if (statusLabel != null && statusLabel.getScene() != null) {
            cachedScene = statusLabel.getScene();
            return cachedScene;
        } else if (playersContainer != null && playersContainer.getScene() != null) {
            cachedScene = playersContainer.getScene();
            return cachedScene;
        }

        return null;
    }

    private void showError(String message) {
        if (statusLabel != null) {
            Platform.runLater(() -> {
                statusLabel.setText("Error: " + message);
                statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
            });
        }
    }

    @Override
    public void handleMessage(MessageType eventType, String username, Object... args) {
        switch (eventType) {
            case CREATE_LOBBY_OK, JOIN_LOBBY_OK -> {
                if (JavaFxInterface.getClientInstance().getLobby() != null) {
                    Lobby lobby = JavaFxInterface.getClientInstance().getLobby();
                    String lobbyName = lobby.getGameID();
                    int players = lobby.getMaxPlayers();
                    boolean isLearnerMode = lobby.isLearnerMode();

                    setLobbyInfo(lobbyName, players, isLearnerMode);

                    currentPlayers.clear();
                    currentPlayers.addAll(lobby.getPlayers());
                    updatePlayersList();
                }
            }
            case LEAVE_GAME -> {
                if (args.length > 0) {
                    String playerName = args[0].toString();
                    currentPlayers.remove(playerName);
                    updatePlayersList();
                }
            }
            case GAME_STARTED_OK -> {
                Platform.runLater(() -> navigateToGame());
            }
            case LEAVE_GAME_OK -> {
                Platform.runLater(() -> navigateToMainMenu());
            }
            case ERROR -> {
                if (args.length > 0) {
                    Platform.runLater(() -> showError(args[0].toString()));
                }
            }
        }
    }

    // FIX: Modifica updatePlayersList per rimuovere il playerCountLabel dalla lista
    private void updatePlayersList() {
        Platform.runLater(() -> {
            if (playersContainer == null || playerCountLabel == null) return;

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

            // FIX: Update player count correttamente (senza aggiungerlo al container)
            playerCountLabel.setText(currentPlayers.size() + "/" + maxPlayers + " players");

            updateStatus();
        });
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return List.of(
                MessageType.CREATE_LOBBY_OK,
                MessageType.JOIN_LOBBY_OK,
                MessageType.LEAVE_GAME,
                MessageType.GAME_STARTED_OK,
                MessageType.LEAVE_GAME_OK,
                MessageType.ERROR
        ).contains(messageType);
    }
}