package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.exceptions.LobbyNotFoundException;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;

/**
 * Contains all methods for managing a player's interaction with a game
 */
public interface MatchControllerInterface {

    void createNewGame(String username, int numOfPlayers, String name) throws PlayerAlreadyInException;
    void joinRandomGame(String username) throws LobbyNotFoundException, PlayerAlreadyInException;
    void joinGame(String username, String gameID) throws LobbyNotFoundException, PlayerAlreadyInException;
    void leaveGame(String username) throws LobbyNotFoundException;
    void reconnectToGame(String username, int gameID);

}
