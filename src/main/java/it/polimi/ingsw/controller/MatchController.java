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
     * @param lobbyName  lobby's name
     */
    public synchronized void createNewGame(String username, int maxPlayers, String lobbyName) throws PlayerAlreadyInException {
        if (maxPlayers < 2 || maxPlayers > 4)
            throw new IllegalArgumentException("Max number of allowed players must be between 2 and 4");

        // checks if the player who is creating the lobby is already in another one
        if (
                lobbies.values().stream()
                        .anyMatch(l -> l.getPlayers().contains(username))
        ) throw new PlayerAlreadyInException("[createLobby] Player is already in a lobby!");

        String gameID = UUID.randomUUID().toString();
        Lobby lobby = new Lobby(gameID, lobbyName, username, maxPlayers);
        lobbies.put(gameID, lobby);

        lobby.addPlayer(username);

        // todo for test
        System.out.println(gameID);
    }

    /**
     * Makes you join the desired not started match yet
     *
     * @param username player's username
     * @param gameID   game to join
     */
    public synchronized void joinGame(String username, String gameID) throws LobbyNotFoundException, PlayerAlreadyInException {
        if (
                lobbies.values().stream()
                        .anyMatch(l -> l.getPlayers().contains(username))
        ) throw new PlayerAlreadyInException("[joinGame] Player is already in a lobby!");

        Optional<Lobby> lobbyOptional = Optional.ofNullable(lobbies.get(gameID));
        Lobby lobby = lobbyOptional.filter(l -> l.getState() == LobbyState.WAITING)
                .orElseThrow(() -> new LobbyNotFoundException("Specified lobby not found or cannot be joined"));

        lobby.addPlayer(username);
    }

    /**
     * Makes you join a random not started match yet
     *
     * @param username player's username
     */
    public synchronized void joinRandomGame(String username) throws LobbyNotFoundException, PlayerAlreadyInException {
        List<Lobby> availableLobbies = this.lobbies.values().stream()
                .filter(l -> l.getState() == LobbyState.WAITING)
                .toList();
        if (availableLobbies.isEmpty()) throw new LobbyNotFoundException("No lobbies available");

        Lobby lobby = availableLobbies.get(new Random().nextInt(availableLobbies.size()));
        lobby.addPlayer(username);
    }

    /**
     * Allows a player to leave the lobby/game
     *
     * @param username player's username
     */
    public synchronized void leaveGame(String username) throws LobbyNotFoundException {
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
        switch (lobby.getState()) {
            case WAITING -> lobbies.remove(lobby.getGameID());
            case IN_GAME -> {
                // todo: come si comporta il game controller? metodo endGame() nella lobby? bisogna eliminare tutti i giocatori
            }
        }
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
