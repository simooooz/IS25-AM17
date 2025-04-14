package it.polimi.ingsw.network.messages.lobby;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.controller.exceptions.LobbyNotFoundException;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.ZeroArgMessage;
import it.polimi.ingsw.network.socket.server.User;

import java.util.concurrent.CompletableFuture;

public class JoinRandomLobbyMessage extends Message {

    public JoinRandomLobbyMessage() {
        super(MessageType.JOIN_RANDOM_LOBBY);
    }

    @Override
    public void execute(User user) {
        try {
            MatchController.getInstance().joinRandomGame(user.getUsername());
            user.send(new ZeroArgMessage(MessageType.JOIN_RANDOM_LOBBY_OK));
        } catch (LobbyNotFoundException | PlayerAlreadyInException e) {
            user.send(new ErrorMessage(e.getMessage()));
        }
    }

}
