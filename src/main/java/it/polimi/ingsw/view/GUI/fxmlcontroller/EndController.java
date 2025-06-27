package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
import it.polimi.ingsw.view.GUI.MessageHandler;
import it.polimi.ingsw.view.GUI.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.net.URL;
import java.util.*;

/**
 * Controller class for the end game screen in GUI.
 * This controller manages the display of the final game results, including
 * player rankings based on credits earned and provides functionality to
 * exit the game.
 *
 * <p>The end screen displays all players sorted by their credits in descending
 * order, with special styling for top positions and a winner badge for the
 * first place player.</p>
 *
 * <p>This controller handles the transition back to the main menu when the
 * player decides to leave the game.</p>
 */
public class EndController implements MessageHandler, Initializable {

    /**
     * Client instance used to communicate with the server and access game data.
     */
    private Client client;

    /**
     * VBox container that holds the ranking display with all player positions.
     * This container is populated dynamically with player ranking rows.
     */
    @FXML
    private VBox rankingContainer;

    /**
     * Initializes the controller when the FXML file is loaded.
     * Sets up the message handler registration, retrieves the client instance,
     * and populates the ranking display.
     *
     * @param url The location used to resolve relative paths for the root object,
     *            or null if not known
     * @param resourceBundle The resources used to localize the root object,
     *                      or null if not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MessageDispatcher.getInstance().registerHandler(this);
        this.client = App.getClientInstance();
        getRanking();
    }

    private void getRanking() {
        List<ClientPlayer> players = client.getGameController().getModel().getBoard().getAllPlayers();

        List<ClientPlayer> ranking = players.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getCredits(), p1.getCredits()))
                .toList();

        // Create a row for each player in the ranking
        for (int i = 0; i < ranking.size(); i++) {
            ClientPlayer player = ranking.get(i);
            HBox playerRow = createGalaxyTruckerRow(player, i + 1);
            rankingContainer.getChildren().add(playerRow);
        }
    }

    private HBox createGalaxyTruckerRow(ClientPlayer player, int position) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(20.0);
        row.getStyleClass().addAll("panel", position <= 3 ? "rank-" + position : "rank-other");

        // Position number
        Label positionLabel = new Label(position + "°");
        positionLabel.getStyleClass().addAll("label", "position-label");
        positionLabel.setStyle("-fx-min-width: 50px;");

        VBox playerInfo = new VBox();
        playerInfo.setSpacing(3.0);

        // Player name
        Label nameLabel = new Label(player.getUsername());
        nameLabel.getStyleClass().addAll("label", "player-name");

        // Player score (credits)
        Label scoreLabel = new Label("$" + player.getCredits());
        scoreLabel.getStyleClass().addAll("status-text", "player-score");

        playerInfo.getChildren().addAll(nameLabel, scoreLabel);

        row.getChildren().add(positionLabel);
        row.getChildren().add(playerInfo);

        // Add winner badge for first place
        if (position == 1) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Label winnerLabel = new Label("BEST TRUCKER");
            winnerLabel.getStyleClass().add("status-text");

            row.getChildren().addAll(spacer, winnerLabel);
        }

        return row;
    }

    @FXML
    private void exitGame() {
        client.send(MessageType.LEAVE_GAME);
    }

    /**
     * Handles incoming game events, specifically LeftLobbyEvent messages.
     * When the current user successfully leaves the lobby, navigates back
     * to the main menu scene.
     *
     * @param event The game event to process, expected to be a LeftLobbyEvent
     */
    @Override
    public void handleMessage(Event event) {
        if (Objects.requireNonNull(event) instanceof LeftLobbyEvent e) {
            if (e.username().equals(client.getUsername()))
                SceneManager.navigateToScene("/fxml/menu.fxml", this, null);
        }
    }

    /**
     * Determines whether this controller can handle a specific message type.
     * This controller only handles LEFT_LOBBY_EVENT messages.
     *
     * @param messageType The type of message to check for compatibility
     * @return true if the message type is LEFT_LOBBY_EVENT, false otherwise
     */
    @Override
    public boolean canHandle(MessageType messageType) {
        return messageType == MessageType.LEFT_LOBBY_EVENT;
    }
}