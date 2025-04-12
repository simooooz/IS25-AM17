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
public class MatchController implements MatchControllerInterface {

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
     * @param maxPlayers max allowed players
     * @param name       lobby's name
     */
    public synchronized void createNewGame(String username, int maxPlayers, String name) throws PlayerAlreadyInException {
        if (maxPlayers <= 0 || maxPlayers > 4) throw new IllegalArgumentException("maxPlayers must be between 1 and 4");
        String gameID = UUID.randomUUID().toString();
        Lobby lobby = new Lobby(gameID, name, username, maxPlayers);
        lobbies.put(gameID, lobby);
        lobby.addPlayer(username); // Join in the newly created lobby
    }

    /**
     * Makes you join the desired not started match yet
     *
     * @param username player's username
     * @param gameID   game to join
     */
    @Override
    public synchronized void joinGame(String username, String gameID) throws LobbyNotFoundException, PlayerAlreadyInException {
        // TODO check se è già in un'altra lobby?
        Optional<Lobby> lobbyOptional = Optional.ofNullable(lobbies.get(gameID));
        Lobby lobby = lobbyOptional.filter(l -> l.getState() == LobbyState.WAITING)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found or not in WAITING state"));

        lobby.addPlayer(username);
    }

    /**
     * Makes you join a random not started match yet
     *
     * @param username player's username
     */
    @Override
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
    @Override
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
    @Override
    public void reconnectToGame(String username, int gameID) {
        // todo
    }

}
