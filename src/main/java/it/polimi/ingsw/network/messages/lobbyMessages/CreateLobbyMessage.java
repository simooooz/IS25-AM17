package it.polimi.ingsw.network.messages.lobbyMessages;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.socket.server.User;

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
    public void execute(User user) {
        try {
            MatchController.getInstance().createNewGame(user.getUsername(), maxPlayers, name);
            user.send(new ZeroArgMessage(MessageType.CREATE_LOBBY_OK));
        } catch (PlayerAlreadyInException e) {
            user.send(new ErrorMessage(e.getMessage()));
        }
    }

}