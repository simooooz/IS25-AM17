package it.polimi.ingsw.controller;

import it.polimi.ingsw.common.model.events.game.GameErrorEvent;
import it.polimi.ingsw.controller.exceptions.LobbyNotFoundException;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.common.model.enums.LobbyState;

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
    public synchronized List<GameEvent> createNewGame(String username, int maxPlayers, String name, boolean learnerMode) {
        EventContext.clear();

        try {
            if (checkIfIsAnotherLobby(username)) throw new PlayerAlreadyInException("Player is already in another lobby");
            if (lobbies.containsKey(name)) throw new IllegalArgumentException("Lobby name already exists, choose another one");
            Lobby lobby = new Lobby(name, maxPlayers, learnerMode);
            lobbies.put(name, lobby);
            lobby.addPlayer(username); // Join in the newly created lobby
        } catch (RuntimeException e) {
            EventContext.emit(new GameErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    /**
     * Makes you join the desired not started match yet
     *
     * @param username player's username
     * @param gameID   game to join
     */
    public synchronized List<GameEvent> joinGame(String username, String gameID) {
        EventContext.clear();

        try {
            if (checkIfIsAnotherLobby(username)) throw new PlayerAlreadyInException("Player is already in another lobby");
            Optional<Lobby> lobbyOptional = Optional.ofNullable(lobbies.get(gameID));
            Lobby lobby = lobbyOptional
                    .filter(l -> l.getState() == LobbyState.WAITING)
                    .orElseThrow(() -> new LobbyNotFoundException("Specified lobby not found or cannot be joined"));
            lobby.addPlayer(username);
        } catch (RuntimeException e) {
            EventContext.emit(new GameErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    /**
     * Makes you join a random not started match yet
     *
     * @param username player's username
     */
    public synchronized List<GameEvent> joinRandomGame(String username, boolean learnerMode) {
        EventContext.clear();

        try {
            if (checkIfIsAnotherLobby(username)) throw new PlayerAlreadyInException("Player is already in another lobby");
            List<Lobby> availableLobbies = this.lobbies.values().stream()
                    .filter(l -> l.getState() == LobbyState.WAITING && l.isLearnerMode() == learnerMode)
                    .toList();
            if (availableLobbies.isEmpty()) throw new LobbyNotFoundException("No lobbies available");
            Lobby lobby = availableLobbies.get(new Random().nextInt(availableLobbies.size()));
            lobby.addPlayer(username);
        } catch (RuntimeException e) {
            EventContext.emit(new GameErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    /**
     * Allows a player to leave the lobby/game
     *
     * @param username player's username
     */
    public synchronized List<GameEvent> leaveGame(String username) {
        EventContext.clear();

        try {
            Optional<Map.Entry<String, Lobby>> lobbyEntry = lobbies.entrySet().stream()
                    .filter(e -> e.getValue().hasPlayer(username))
                    .findFirst();
            if (lobbyEntry.isEmpty()) throw new LobbyNotFoundException("Lobby not found");

            Lobby lobby = lobbyEntry.get().getValue();
            lobby.removePlayer(username);
            if (lobby.toDelete())
                lobbies.remove(lobby.getGameID());
        } catch (RuntimeException e) {
            EventContext.emit(new GameErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    /**
     * Allows a player who was previously joined the game to reconnect
     *
     * @param username player's username
     * @param gameID   id of lobby/game
     */
    public synchronized List<GameEvent> rejoinGame(String username, String gameID) {
        EventContext.clear();

        try {
            if (checkIfIsAnotherLobby(username)) throw new PlayerAlreadyInException("Player is already in another lobby");

            Optional<Lobby> lobbyOptional = Optional.ofNullable(lobbies.get(gameID));
            Lobby lobby = lobbyOptional
                    .filter(l -> l.getState() == LobbyState.IN_GAME)
                    .orElseThrow(() -> new LobbyNotFoundException("Specified lobby not found or cannot be joined"));

            lobby.rejoinPlayer(username);
        } catch (RuntimeException e) { // Don't rejoin and return empty list
            EventContext.clear();
            return List.of();
        }

        return EventContext.getAndClear();
    }

    public synchronized Lobby getLobby(String username) {
        return lobbies.values().stream().filter(lobby -> lobby.hasPlayer(username)).findFirst().orElse(null);
    }

    private boolean checkIfIsAnotherLobby(String username) {
        return lobbies.entrySet().stream().anyMatch(e -> e.getValue().hasPlayer(username));
    }

}
