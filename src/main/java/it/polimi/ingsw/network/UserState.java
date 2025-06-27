package it.polimi.ingsw.network;

/**
 * Enumeration representing the different states a user can be in during their
 * game session. These states define the user's current position
 * in the game flow and determine which scenario is available to them.
 */
public enum UserState {

    /**
     * Initial state when a user first connects to the server.
     * In this state, the user must set their username before proceeding.
     */
    USERNAME,

    /**
     * State after successfully setting a username.
     * The user can create a new lobby, join an existing lobby by name,
     * or join a random lobby with specified parameters.
     */
    LOBBY_SELECTION,

    /**
     * State when the user is in a lobby, but the game hasn't started yet.
     * The user can wait for other players to join or leave the lobby.
     */
    IN_LOBBY,

    /**
     * State the user is in once the game has started.
     * All actions related to game logic are linked to this state
     * and managed through PlayerState enum.
     */
    IN_GAME
}
