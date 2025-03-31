package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.exceptions.PlayerAlreadyInException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Lobby {

    /**
     * {@link GameController} reference, null if game not started
     */
    private GameController game = null;
    /**
     * unique id for the lobby
     */
    private final String uuid;
    /**
     * {@link LobbyState}
     */
    private LobbyState state;
    /**
     * lobby name
     */
    private final String name;
    /**
     * player's username who created the lobby
     */
    private String master;

    public static final int MIN_PLAYERS = 2;
    public static final int DEFAULT_MAX_PLAYERS = 4;
    /**
     * max num of players master wants to be accepted
     */
    private int maxPlayers = DEFAULT_MAX_PLAYERS;
    /**
     * players in the lobby
     */
    private final List<String> players;

    /**
     * Constructor
     *
     * @param username player's username
     */
    public Lobby(String username) {
        this.state = LobbyState.WAITING;
        this.uuid = UUID.randomUUID().toString();
        this.name = "game" + this.uuid;

        this.master = username;

        this.players = new ArrayList<>();
    }

    /**
     * Constructor
     *
     * @param name       lobby's name
     * @param username   player's username
     * @param maxPlayers max players allowed in lobby
     */
    public Lobby(String name, String username, int maxPlayers) {
        this.state = LobbyState.WAITING;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;

        this.master = username;

        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
    }

    public LobbyState getState() {
        return state;
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
     * @param username player's username
     */
    public void addPlayer(String username) {
        if (hasPlayer(username)) throw new PlayerAlreadyInException("Player's already in");

        players.add(username);
        if (players.size() == maxPlayers)
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
        if (!players.isEmpty())
            master = master.equals(username) ? players.get(new Random().nextInt(players.size())) : null;

        if (this.state == LobbyState.IN_GAME) this.game.playerLeft(username);
    }

    /**
     * Init the {@link GameController} associated with the lobby
     */
    private void initGame() {
        this.state = LobbyState.IN_GAME;

        this.game = new GameController(players);
        this.game.startMatch();
    }

    public void endGame() {
        // todo
    }

}
