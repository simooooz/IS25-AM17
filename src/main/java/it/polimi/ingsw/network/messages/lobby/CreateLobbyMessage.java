package it.polimi.ingsw.network.messages.lobby;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.socket.server.User;

import java.util.concurrent.CompletableFuture;

public class CreateLobbyMessage extends Message {

    private final String name;
    private final Integer maxPlayers;
    private final Boolean learnerMode;

    public CreateLobbyMessage(String name, Integer maxPlayers, Boolean learnerMode) {
        super(MessageType.CREATE_LOBBY);
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.learnerMode = learnerMode;
    }

    @Override
    public void execute(User user) {

        try {
            MatchController.getInstance().createNewGame(user.getUsername(), maxPlayers, name, learnerMode);
            user.send(new ZeroArgMessage(MessageType.CREATE_LOBBY_OK));
        } catch (PlayerAlreadyInException e) {
            user.send(new ErrorMessage(e.getMessage()));
        }

    }

}
