package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.network.exceptions.UserNotFoundException;
import it.polimi.ingsw.network.socket.server.RefToUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    /**
     * max num of players master wants to be accepted
     */
    private int maxPlayers ;
    /**
     * players in the lobby
     */
    private final List<String> players;

    /**
     * Constructor
     *
     * @param gameID     lobby's ID
     * @param name       lobby's name
     * @param username   player's username
     * @param maxPlayers max players allowed in lobby
     */
    public Lobby(String gameID, String name, String username, int maxPlayers) {
        this.state = LobbyState.WAITING;
        this.uuid = gameID;
        this.name = name;
        this.master = username;

        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
    }

    public LobbyState getState() {
        return state;
    }

    public String getGameID() {
        return uuid;
    }

    public List<String> getPlayers() {
        return players;
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
    public void addPlayer(String username) throws PlayerAlreadyInException {
//        if (this.players.contains(username)) throw new PlayerAlreadyInException("Player's already in");

        players.add(username);
        System.out.println("Player " + username + " joined!");
        System.out.println(players.size());
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
            master = master.equals(username) ? players.get(new Random().nextInt(players.size())) : master;

        if (this.state == LobbyState.IN_GAME) this.game.playerLeft(username);
    }

    /**
     * Init the {@link GameController} associated with the lobby
     */
    private void initGame() {
        this.state = LobbyState.IN_GAME;
        this.game = new GameController(players);

        try {
            for (String username : players) // Set GameController for each user
                RefToUser.getUser(username).setGameController(game);
        } catch (UserNotFoundException e) {
            this.state = LobbyState.WAITING;
            throw new RuntimeException("Error initializing game");
        }

        this.game.startMatch();
    }

    // TODO
    public void endGame() {
        // todo
    }

}
