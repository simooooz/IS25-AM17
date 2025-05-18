package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.controller.exceptions.LobbyNotFoundException;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.model.game.LobbyState;
import it.polimi.ingsw.network.messages.MessageType;

public abstract class ServerBasis {

    public static boolean setUsername(User user, String username) {
        if (user.getState() != UserState.USERNAME) throw new IllegalStateException("User is not in state USERNAME");

        if (User.isUsernameTaken(username))
            return false;
        return user.setUsername(username);
    }

    public static void createLobby(User user, String name, Integer maxPlayers, Boolean learnerMode) throws PlayerAlreadyInException {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");
        Lobby lobby = MatchController.getInstance().createNewGame(user.getUsername(), maxPlayers, name, learnerMode);
        user.setLobby(lobby);
        user.notifyLobbyEvent(MessageType.CREATE_LOBBY_OK, lobby);
    }

    public static void joinLobby(User user, String lobbyName) throws LobbyNotFoundException, PlayerAlreadyInException {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");
        Lobby lobby = MatchController.getInstance().joinGame(user.getUsername(), lobbyName);
        user.setLobby(lobby);
        user.notifyLobbyEvent(lobby.getState() == LobbyState.IN_GAME ? MessageType.GAME_STARTED_OK : MessageType.JOIN_LOBBY_OK, lobby);
    }

    public static void joinRandomLobby(User user, Boolean learnerMode) throws LobbyNotFoundException, PlayerAlreadyInException {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");
        Lobby lobby = MatchController.getInstance().joinRandomGame(user.getUsername(), learnerMode);
        user.setLobby(lobby);
        user.notifyLobbyEvent(lobby.getState() == LobbyState.IN_GAME ? MessageType.GAME_STARTED_OK : MessageType.JOIN_RANDOM_LOBBY_OK, lobby);
    }

    public static void leaveGame(User user) {
        // TODO check state
        MatchController.getInstance().leaveGame(user.getUsername());
        user.notifyLobbyEvent(MessageType.LEAVE_GAME_OK, user.getLobby());
        user.setLobby(null);
        user.setGameController(null); // Giusto?
    }

    public static void pickComponent(User client, Integer id) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().pickComponent(client.getUsername(), id);
    }

    public static void releaseComponent(User client, Integer id) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().releaseComponent(client.getUsername(), id);
    }

}
