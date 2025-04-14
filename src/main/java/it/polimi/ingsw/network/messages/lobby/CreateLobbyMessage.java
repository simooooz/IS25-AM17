package it.polimi.ingsw.network.messages.lobby;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.socket.server.RefToUser;

import java.util.concurrent.CompletableFuture;

public class CreateLobbyMessage extends Message {

    private final String name;
    private final Integer maxPlayers;
    private final Integer gameType;

    public CreateLobbyMessage(String name, Integer maxPlayers, Integer gameType) {
        super(MessageType.CREATE_LOBBY);
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.gameType = gameType;
    }

    @Override
    public void execute(RefToUser user) {

        try {
            MatchController.getInstance().createNewGame(user.getUsername(), maxPlayers, name);
        } catch (PlayerAlreadyInException e) {
            throw new RuntimeException(e);
        }
        user.send(new ZeroArgMessage(MessageType.CREATE_LOBBY_OK), new CompletableFuture<>());

    }

}
