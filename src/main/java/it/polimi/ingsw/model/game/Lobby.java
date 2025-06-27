package it.polimi.ingsw.model.game;

import it.polimi.ingsw.common.model.enums.LobbyState;
import it.polimi.ingsw.common.model.events.game.SyncAllEvent;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.lobby.SetLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.JoinedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game lobby that manages player connections and game initialization.
 * The Lobby class serves as a waiting room where players can join before a game starts,
 * and handles the transition from waiting state to active gameplay.
 */
public class Lobby {

    /**
     * The game controller instance. Marked as transient to exclude from serialization.
     * This field is null when the lobby is in WAITING state and gets initialized
     * when the game starts.
     */
    private transient GameController game = null;

    /**
     * Unique identifier for this lobby, also serves as the game name.
     */
    private final String id;

    /**
     * Current state of the lobby (WAITING or IN_GAME).
     */
    private LobbyState state;

    /**
     * Flag indicating whether this lobby uses learner mode gameplay.
     * This setting affects the type of board and rules used in the game.
     */
    private final boolean learnerMode;

    /**
     * Maximum number of players allowed in a lobby (between 2 and 4).
     */
    private final int maxPlayers;

    /**
     * List of usernames of players currently in the lobby.
     */
    private final List<String> players;

    /**
     * @param name        the unique identifier and name for this lobby
     * @param maxPlayers  the maximum number of players allowed (must be between 2 and 4)
     * @param learnerMode true if this lobby should use learner mode gameplay,
     *                    false for standard mode
     * @throws IllegalArgumentException if maxPlayers is not between 2 and 4 (inclusive)
     */
    public Lobby(String name, int maxPlayers, boolean learnerMode) {
        if (maxPlayers < 2 || maxPlayers > 4)
            throw new IllegalArgumentException("Max number of allowed players must be between 2 and 4");

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

    /**
     * Checks if this is a learner mode lobby.
     *
     * @return true if learner mode is enabled, false otherwise
     */
    public boolean isLearnerMode() {
        return learnerMode;
    }

    /**
     * Determines if this lobby should be deleted.
     * A lobby is marked for deletion when it has no players remaining.
     *
     * @return true if the lobby is empty and should be deleted, false otherwise
     */
    public boolean toDelete() {
        return players.isEmpty();
    }

    /**
     * Adds a new player to the lobby.
     *
     * @param username the username of the player to add
     * @throws PlayerAlreadyInException if the player is already in the lobby
     */
    public void addPlayer(String username) {
        if (players.contains(username)) throw new PlayerAlreadyInException("Player's already in");

        players.add(username);
        EventContext.emit(new JoinedLobbyEvent(username));
        EventContext.emit(new SetLobbyEvent(id, players, learnerMode, maxPlayers));

        if (players.size() == maxPlayers && state == LobbyState.WAITING)
            this.initGame();
    }

    /**
     * Checks if a specific player is currently in the lobby.
     *
     * @param username the username to check for
     * @return true if the player is in the lobby, false otherwise
     */
    public boolean hasPlayer(String username) {
        return players.contains(username);
    }

    /**
     * Removes a player from the lobby.
     *
     * @param username the username of the player to remove
     */
    public void removePlayer(String username) {
        if (this.state == LobbyState.IN_GAME)
            this.game.leaveGame(username);

        players.remove(username);

        List<String> toNotify = new ArrayList<>(players);
        EventContext.emit(new LeftLobbyEvent(username, toNotify));
    }

    /**
     * Handles a player rejoining the lobby, typically after a disconnection.
     * This method allows players to reconnect to an active game or rejoin
     * a waiting lobby. If a game is in progress, the player will be
     * synchronized with the current game state.
     *
     * @param username the username of the player rejoining
     * @throws PlayerAlreadyInException if the player is already in the lobby
     */
    public void rejoinPlayer(String username) {
        if (players.contains(username)) throw new PlayerAlreadyInException("Player's already in");

        players.add(username);

        EventContext.emit(new JoinedLobbyEvent(username));
        EventContext.emit(new SetLobbyEvent(id, players, learnerMode, maxPlayers));

        if (this.game != null) {
            this.game.rejoinGame(username);
            EventContext.emit(new SyncAllEvent(game.toDTO()));
        }
    }

    /**
     * Initializes and starts a new game when the lobby is full.
     * This method is called automatically when the maximum number of players
     * is reached. It creates a new GameController, starts the match,
     * and transitions the lobby state to IN_GAME.
     *
     */
    private void initGame() {
        this.game = new GameController(players, learnerMode);
        this.game.startMatch();
        this.state = LobbyState.IN_GAME;
    }
}
