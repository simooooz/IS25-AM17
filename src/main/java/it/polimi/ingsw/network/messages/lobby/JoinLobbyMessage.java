package it.polimi.ingsw.network.messages.lobby;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.controller.exceptions.LobbyNotFoundException;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.ZeroArgMessage;
import it.polimi.ingsw.network.socket.server.User;

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
            MatchController.getInstance().joinGame(user.getUsername(), name);
            user.send(new ZeroArgMessage(MessageType.JOIN_LOBBY_OK));
        } catch (LobbyNotFoundException | PlayerAlreadyInException e) {
            user.send(new ErrorMessage(e.getMessage()));
        }
    }

}
