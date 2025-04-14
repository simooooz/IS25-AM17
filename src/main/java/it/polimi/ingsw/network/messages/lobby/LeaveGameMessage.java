package it.polimi.ingsw.network.messages.lobby;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.controller.exceptions.LobbyNotFoundException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.ZeroArgMessage;
import it.polimi.ingsw.network.socket.server.RefToUser;

import java.util.concurrent.CompletableFuture;

public class LeaveGameMessage extends Message {

    public LeaveGameMessage() {
        super(MessageType.LEAVE_GAME);
    }

    @Override
    public void execute(RefToUser user) {
        try {
            MatchController.getInstance().leaveGame(user.getUsername());
            user.send(new ZeroArgMessage(MessageType.LEAVE_GAME_OK), new CompletableFuture<>());
        } catch (LobbyNotFoundException e) {
            user.send(new ErrorMessage(e.getMessage()), new CompletableFuture<>());
        }
    }

}
