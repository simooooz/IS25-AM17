package it.polimi.ingsw.controller;

/**
 * Contains all methods for managing a player's interaction with a game
 */
public interface MatchControllerInterface {

    void createNewGame(String username, int numOfPlayers, String name);
    void joinRandomGame(String username);
    void joinGame(String username, String gameID);
    void leaveGame(String username);
    void reconnectToGame(String username, int gameID);

}
