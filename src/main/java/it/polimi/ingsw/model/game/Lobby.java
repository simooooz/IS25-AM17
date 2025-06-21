package it.polimi.ingsw.model.game;

import it.polimi.ingsw.common.model.enums.LobbyState;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.lobby.CreatedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.JoinedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    /**
     * {@link GameController} reference, null if game not started
     */
    private transient GameController game = null;
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
     * @param maxPlayers  max players allowed in a lobby
     * @param learnerMode true if is a test flight
     */
    public Lobby(String name, int maxPlayers, boolean learnerMode) {
        this.state = LobbyState.WAITING;
        this.id = name;
        this.learnerMode = learnerMode;

        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
    }

    public GameController getGame() {
        return game;
    }

    public void setGame(GameController game) {
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

    public boolean isLearnerMode() {
        return learnerMode;
    }

    /**
     * Check whether there are conditions to eliminate the lobby
     */
    public boolean toDelete() {
        return players.isEmpty();
    }

    /**
     * Adds a player and if the lobby is full initializes an instance of {@link GameController}
     *
     * @param username player's username
     */
    public void addPlayer(String username) {
        if (players.contains(username)) throw new PlayerAlreadyInException("Player's already in");

        players.add(username);
        EventContext.emit(new JoinedLobbyEvent(username));
        EventContext.emit(new CreatedLobbyEvent(id, players, learnerMode, maxPlayers));

        if (players.size() == maxPlayers && state == LobbyState.WAITING)
            this.initGame();
    }

    /**
     * Checks if the player is in the lobby
     * @param username player's username
     */
    public boolean hasPlayer(String username) {
        return players.contains(username);
    }

    /**
     * Removes a player and if the {@link LobbyState} is IN_GAME then notifies the {@link GameController}
     *
     * @param username player's username
     */
    public void removePlayer(String username) {
        if (this.state == LobbyState.IN_GAME)
            this.game.leaveGame(username);

        players.remove(username);

        List<String> toNotify = new ArrayList<>(players);
        toNotify.add(username);
        EventContext.emit(new LeftLobbyEvent(username, toNotify));
    }

    public void rejoinPlayer(String username) {
        if (players.contains(username)) throw new PlayerAlreadyInException("Player's already in");

        players.add(username);

        if (this.game != null)
            this.game.rejoinGame(username);

        EventContext.emit(new JoinedLobbyEvent(username));
        EventContext.emit(new CreatedLobbyEvent(id, players, learnerMode, maxPlayers));

    }

    /**
     * Init the {@link GameController} associated with the lobby
     */
    private void initGame() {
        this.game = new GameController(players, learnerMode);
        this.game.startMatch();
        this.state = LobbyState.IN_GAME;
    }

}