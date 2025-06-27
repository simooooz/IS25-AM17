package it.polimi.ingsw.client.model.game;

import it.polimi.ingsw.client.controller.ClientGameController;
import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.common.model.enums.LobbyState;
import it.polimi.ingsw.common.model.events.lobby.JoinedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A read-only representation of a lobby on the client side.
 */
public class ClientLobby {

    private ClientGameController game = null;
    private final String id;
    private LobbyState state;
    private final boolean learnerMode;

    private final int maxPlayers;
    private final List<String> players;

    public ClientLobby(String name, List<String> players, boolean learnerMode, int maxPlayers) {
        this.state = LobbyState.WAITING;
        this.id = name;
        this.learnerMode = learnerMode;

        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>(players);
    }

    public ClientGameController getGame() {
        return game;
    }

    public void setGame(ClientGameController game) {
        this.game = game;
    }

    public LobbyState getState() {
        return state;
    }

    public String getGameID() {
        return id;
    }

    public List<String> getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isLearnerMode() {
        return learnerMode;
    }

    public void addPlayer(String username) {
        players.add(username);

        if (this.state == LobbyState.IN_GAME)
            this.game.playerRejoined(username);

        ClientEventBus.getInstance().publish(new JoinedLobbyEvent(username));
    }

    public void removePlayer(String username) {
        players.remove(username);

        if (this.state == LobbyState.IN_GAME)
            this.game.playerLeft(username);

        ClientEventBus.getInstance().publish(new LeftLobbyEvent(username, null));
    }

    public void initGame() {
        this.game = new ClientGameController(players, learnerMode);
        this.state = LobbyState.IN_GAME;
        this.game.matchStarted();
    }

}