package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.exceptions.LobbyNotFoundException;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.model.game.LobbyState;

import java.util.*;

/**
 * MainController Class <br>
 * Is the Controller of the controllers, it manages all the available {@link Lobby}
 * and relative games that are running {@link GameController}<br>
 * Allowing players to create, join, reconnect, leave and delete games
 */
public class MatchController {

    /**
     * Singleton Pattern, instance of the class
     */
    private static MatchController instance = null;

    /**
     * List of running games
     * For implementing AF: "multiple games"
     */
    private final Map<String, Lobby> lobbies;


    /**
     * Init an empty List of GameController
     * For implementing AF: "multiple games"
     */
    private MatchController() {
        lobbies = new HashMap<>();
    }

    /**
     * Singleton Pattern
     *
     * @return the only one instance of the MainController class
     */
    public synchronized static MatchController getInstance() {
        if (instance == null) {
            instance = new MatchController();
        }
        return instance;
    }


    /**
     * It creates a new lobby, and then if {@link LobbyState} is READY ==> init a {@link GameController}
     *
     * @param username   player's username
     * @param maxPlayers max number of allowed players
     * @param name       lobby's name
     */
    public synchronized Lobby createNewGame(String username, int maxPlayers, String name, boolean learnerMode) {
        if (maxPlayers < 2 || maxPlayers > 4) throw new IllegalArgumentException("Max number of allowed players must be between 2 and 4");
        Optional<Map.Entry<String, Lobby>> lobbyEntry = lobbies.entrySet().stream()
                .filter(e -> e.getValue().hasPlayer(username))
                .findFirst();
        if (lobbyEntry.isPresent()) throw new PlayerAlreadyInException("Player is already in another lobby");

        if (lobbies.containsKey(name)) throw new IllegalArgumentException("Lobby name already exists, choose another one");

        Lobby lobby = new Lobby(name, username, maxPlayers, learnerMode);
        lobbies.put(name, lobby);

        lobby.addPlayer(username); // Join in the newly created lobby
        return lobby;
    }

    /**
     * Makes you join the desired not started match yet
     *
     * @param username player's username
     * @param gameID   game to join
     */
    public synchronized Lobby joinGame(String username, String gameID) {
        Optional<Map.Entry<String, Lobby>> lobbyEntry = lobbies.entrySet().stream()
                .filter(e -> e.getValue().hasPlayer(username))
                .findFirst();
        if (lobbyEntry.isPresent()) throw new PlayerAlreadyInException("Player is already in another lobby");

        Optional<Lobby> lobbyOptional = Optional.ofNullable(lobbies.get(gameID));
        Lobby lobby = lobbyOptional
                .filter(l -> l.getState() == LobbyState.WAITING)
                .orElseThrow(() -> new LobbyNotFoundException("Specified lobby not found or cannot be joined"));

        lobby.addPlayer(username);
        return lobby;
    }

    /**
     * Makes you join a random not started match yet
     *
     * @param username player's username
     */
    public synchronized Lobby joinRandomGame(String username, boolean learnerMode) {
        Optional<Map.Entry<String, Lobby>> lobbyEntry = lobbies.entrySet().stream()
                .filter(e -> e.getValue().hasPlayer(username))
                .findFirst();
        if (lobbyEntry.isPresent()) throw new PlayerAlreadyInException("Player is already in another lobby");

        List<Lobby> availableLobbies = this.lobbies.values().stream()
                .filter(l -> l.getState() == LobbyState.WAITING && l.isLearnerMode() == learnerMode)
                .toList();
        if (availableLobbies.isEmpty()) throw new LobbyNotFoundException("No lobbies available");

        Lobby lobby = availableLobbies.get(new Random().nextInt(availableLobbies.size()));
        lobby.addPlayer(username);
        return lobby;
    }

    /**
     * Allows a player to leave the lobby/game
     *
     * @param username player's username
     */
    public synchronized void leaveGame(String username) {
        Optional<Map.Entry<String, Lobby>> lobbyEntry = lobbies.entrySet().stream()
                .filter(e -> e.getValue().hasPlayer(username))
                .findFirst();
        if (lobbyEntry.isEmpty()) throw new LobbyNotFoundException("Lobby not found");

        Lobby lobby = lobbyEntry.get().getValue();
        lobby.removePlayer(username);
        if (lobby.toDelete())
            this.delete(lobby);
    }

    /**
     * Handles the elimination of the lobby/game
     *
     * @param lobby to delete
     */
    private void delete(Lobby lobby) {
        lobbies.remove(lobby.getGameID());
        if (lobby.getState() == LobbyState.IN_GAME)
            lobby.endGame();
    }

    /**
     * Allows a player who was previously joined the game to reconnect
     *
     * @param username player's username
     * @param gameID   id of lobby/game
     */
    public void reconnectToGame(String username, int gameID) {
        // todo
    }

}
