package it.polimi.ingsw.client.model.game;

import it.polimi.ingsw.client.controller.ClientGameController;
import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.common.model.enums.LobbyState;
import it.polimi.ingsw.common.model.events.lobby.JoinedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientLobby {

    /**
     * {@link ClientGameController} reference, null if game not started
     */
    private ClientGameController game = null;
    /**
     * unique id for the lobby
     */
    private final String id;
    /**
     * {@link LobbyState}
     */
    private LobbyState state;
    /**
     * learner flag
     */
    private final boolean learnerMode;

    /**
     * max num of players master wants to be accepted
     */
    private final int maxPlayers;
    /**
     * players in the lobby
     */
    private final List<String> players;

    /**
     * Constructor
     *
     * @param name        lobby's id
     * @param learnerMode true if is a test flight
     * @param maxPlayers  max players allowed in a lobby
     */
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

    /**
     * Init the {@link ClientGameController} associated with the lobby
     */
    public void initGame() {
        this.game = new ClientGameController(players, learnerMode);
        this.game.matchStarted();
        this.state = LobbyState.IN_GAME;
    }

}