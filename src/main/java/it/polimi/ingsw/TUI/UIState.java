package it.polimi.ingsw.TUI;

/**
 * Enum representing the possible UI states in the text user interface.
 */
public enum UIState {
    USERNAME,
    USERNAME_ALREADY_TAKEN(),
    LOBBY_SELECTION,
    IN_LOBBY,
    IN_GAME
}