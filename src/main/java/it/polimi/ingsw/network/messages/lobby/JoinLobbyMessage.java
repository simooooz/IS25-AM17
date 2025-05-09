package it.polimi.ingsw.network.messages.lobby;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.controller.exceptions.LobbyNotFoundException;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.socket.server.ClientHandler;
import it.polimi.ingsw.network.socket.server.Server;
import it.polimi.ingsw.network.socket.server.User;

import java.util.HashMap;
import java.util.List;

public class JoinLobbyMessage extends Message {

    private final String name;

    public JoinLobbyMessage(String name) {
        super(MessageType.JOIN_LOBBY);
        this.name = name;
    }

    @Override
    public void execute(User user) {
        // TODO capiamo se fare recconect player
        try {
            Lobby lobby = MatchController.getInstance().joinGame(user.getUsername(), name);
            user.send(new SingleArgMessage<>(MessageType.JOIN_LOBBY_OK, lobby));

            try {
                HashMap<String, ClientHandler> allUsers = Server.getInstance().getConnections();
                List<User> users = allUsers.values().stream().map(ClientHandler::getUser).toList();

                // notify all users in the lobby of the update
                synchronized (users) {
                    for (User u : users) {
                        if (lobby.getPlayers().contains(u.getUsername())) {
                            u.send(new SingleArgMessage<>(MessageType.LOBBY_UPDATE_OK, lobby));
                        }
                    }
                }
            } catch (ServerException e) {
                throw new RuntimeException(e);
            }
        } catch (LobbyNotFoundException | PlayerAlreadyInException e) {
            user.send(new ErrorMessage(e.getMessage()));
        }
    }

}
