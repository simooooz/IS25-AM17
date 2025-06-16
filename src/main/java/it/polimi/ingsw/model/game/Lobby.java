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
import java.util.Random;

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
     * player's username who created the lobby
     */
    private String master;

    public static final int MIN_PLAYERS = 2;
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
     * @param master      master's username
     * @param maxPlayers  max players allowed in a lobby
     * @param learnerMode true if is a test flight
     */
    public Lobby(String name, String master, int maxPlayers, boolean learnerMode) {
        this.state = LobbyState.WAITING;
        this.id = name;
        this.master = master;
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

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isLearnerMode() {
        return learnerMode;
    }

    /**
     * Check whether there are conditions to eliminate the lobby
     */
    public boolean toDelete() {
        if (state == LobbyState.WAITING)
            return players.isEmpty();
        return (state == LobbyState.IN_GAME) && (players.size() < MIN_PLAYERS);
    }

    /**
     * Adds a player and if the lobby is full initializes an instance of {@link GameController}
     *
     * @param username player's username
     */
    public void addPlayer(String username) {
        if (hasPlayer(username)) throw new PlayerAlreadyInException("Player's already in");

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
        players.remove(username);

        List<String> toNotify = new ArrayList<>(players);
        toNotify.add(username);
        EventContext.emit(new LeftLobbyEvent(username, toNotify));

        if (!players.isEmpty())
            master = master.equals(username) ? players.get(new Random().nextInt(players.size())) : master;

        if (toDelete() && this.state == LobbyState.IN_GAME)
            this.endGame();
        else if (this.state == LobbyState.IN_GAME)
            this.game.leaveGame(username);
    }

    /**
     * Init the {@link GameController} associated with the lobby
     */
    private void initGame() {
        this.state = LobbyState.IN_GAME;
        this.game = new GameController(players, learnerMode);
        this.game.startMatch();
    }

    public void endGame() {
        this.state = LobbyState.GAME_ENDED;
        // TODO game ended non può essere settato se il gioco finisce da solo perché sul model
        // TODO ha senso usarlo?
        this.game.endGame();
    }

}
