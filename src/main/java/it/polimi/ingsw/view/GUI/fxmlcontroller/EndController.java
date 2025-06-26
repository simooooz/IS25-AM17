package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
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

public class EndController implements MessageHandler, Initializable {

    private Client client;

    @FXML
    private VBox rankingContainer;

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

        // Player row
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

        // Position
        Label positionLabel = new Label(position + "°");
        positionLabel.getStyleClass().addAll("label", "position-label");
        positionLabel.setStyle("-fx-min-width: 50px;");

        VBox playerInfo = new VBox();
        playerInfo.setSpacing(3.0);

        // Name
        Label nameLabel = new Label(player.getUsername());
        nameLabel.getStyleClass().addAll("label", "player-name");

        // Score
        Label scoreLabel = new Label("$" + player.getCredits());
        scoreLabel.getStyleClass().addAll("status-text", "player-score");

        playerInfo.getChildren().addAll(nameLabel, scoreLabel);

        row.getChildren().add(positionLabel);
        row.getChildren().add(playerInfo);

        // Badge for the winner
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

    @Override
    public void handleMessage(GameEvent event) {
        if (Objects.requireNonNull(event) instanceof LeftLobbyEvent e) {
            if (e.username().equals(client.getUsername()))
                SceneManager.navigateToScene("/fxml/menu.fxml", this, null);
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return messageType == MessageType.LEFT_LOBBY_EVENT;
    }

}