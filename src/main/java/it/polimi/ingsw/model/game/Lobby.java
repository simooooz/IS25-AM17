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
 *
 * <p>Key responsibilities include:
 * <ul>
 * <li>Managing player joins, leaves, and reconnections</li>
 * <li>Enforcing player count limits (2-4 players)</li>
 * <li>Handling different game modes (learner mode vs standard mode)</li>
 * <li>Automatically starting games when the lobby is full</li>
 * <li>Emitting appropriate events for lobby state changes</li>
 * </ul>
 *
 * <p>The lobby supports two main states:
 * <ul>
 * <li>{@code WAITING} - Players can join and leave freely</li>
 * <li>{@code IN_GAME} - Game is active, players can reconnect but not join for the first time</li>
 * </ul>
 *
 * @author Generated Javadoc
 * @version 1.0
 * @since 1.0
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
     * Maximum number of players allowed in this lobby (between 2 and 4).
     */
    private final int maxPlayers;

    /**
     * List of usernames of players currently in the lobby.
     * The order represents the join order of players.
     */
    private final List<String> players;

    /**
     * Constructs a new Lobby with the specified parameters.
     * The lobby starts in WAITING state with no players.
     *
     * <p>The lobby automatically enforces player count constraints and
     * will start a game when the maximum number of players is reached.
     *
     * @param name        the unique identifier and name for this lobby
     * @param maxPlayers  the maximum number of players allowed (must be between 2 and 4)
     * @param learnerMode true if this lobby should use learner mode gameplay,
     *                    false for standard/advanced mode
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

    /**
     * Returns the game controller associated with this lobby.
     * This will be null if the lobby is still in WAITING state.
     *
     * @return the GameController instance, or null if no game is active
     */
    public GameController getGame() {
        return game;
    }

    /**
     * Sets the game controller for this lobby.
     * This method is typically called internally when initializing a new game.
     *
     * @param game the GameController instance to associate with this lobby
     */
    public void setGame(GameController game) {
        this.game = game;
    }

    /**
     * Returns the current state of the lobby.
     *
     * @return the current LobbyState (WAITING or IN_GAME)
     */
    public LobbyState getState() {
        return state;
    }

    /**
     * Returns the unique identifier of this lobby.
     *
     * @return the lobby ID/name
     */
    public String getGameID() {
        return id;
    }

    /**
     * Returns a list of player usernames currently in the lobby.
     * The returned list reflects the current players and their join order.
     *
     * @return a list of player usernames in join order
     */
    public List<String> getPlayers() {
        return players;
    }

    /**
     * Checks if this lobby is configured for learner mode.
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
     * If the lobby reaches maximum capacity after adding the player,
     * the game will automatically start.
     *
     * <p>This method emits the following events:
     * <ul>
     * <li>{@code JoinedLobbyEvent} - notifying that the player joined</li>
     * <li>{@code CreatedLobbyEvent} - updating lobby information</li>
     * </ul>
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
     * If the lobby is in IN_GAME state, this method also handles
     * leaving the active game. Emits a LeftLobbyEvent to notify
     * remaining players.
     *
     * <p>This method emits:
     * <ul>
     * <li>{@code LeftLobbyEvent} - notifying remaining players of the departure</li>
     * </ul>
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
     * <p>This method emits the following events:
     * <ul>
     * <li>{@code JoinedLobbyEvent} - notifying that the player rejoined</li>
     * <li>{@code CreatedLobbyEvent} - updating lobby information</li>
     * <li>{@code SyncAllEvent} - synchronizing game state (if game is active)</li>
     * </ul>
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
     * <p>This is a private method that handles the internal game initialization
     * process and should not be called directly.
     */
    private void initGame() {
        this.game = new GameController(players, learnerMode);
        this.game.startMatch();
        this.state = LobbyState.IN_GAME;
    }
}
