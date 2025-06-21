package it.polimi.ingsw.network;

import it.polimi.ingsw.client.controller.ClientGameController;
import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.client.model.game.ClientLobby;
import it.polimi.ingsw.common.model.events.lobby.CreatedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.UsernameOkEvent;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.TUI.ViewTui;
import it.polimi.ingsw.view.UserInterface;

public abstract class Client {

    protected String username;
    protected UserState state;
    protected ClientLobby lobby;
    protected UserInterface ui;

    public Client(UserInterface ui) {
        this.ui = ui;
        this.state = UserState.USERNAME;
        this.username = null;
        this.lobby = null;
    }

    public ClientGameController getGameController() {
        return lobby.getGame();
    }

    public UserInterface getViewTui() {
        return ui;
    }

    public ClientLobby getLobby() {
        return lobby;
    }

    public void setLobby(ClientLobby lobby) {
        this.lobby = lobby;
        ClientEventBus.getInstance().publish(lobby == null ? new LeftLobbyEvent(username, null) : new CreatedLobbyEvent(lobby.getGameID(), lobby.getPlayers(), lobby.isLearnerMode(), lobby.getMaxPlayers()));
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        ClientEventBus.getInstance().publish(new UsernameOkEvent(username));
    }

    public abstract void closeConnection();

    public abstract void send(MessageType messageType, Object... args);

}