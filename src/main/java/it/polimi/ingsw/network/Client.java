package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.TUI.ViewTui;

public abstract class Client {

    protected String username;
    protected GameController gameController;
    protected UserState state;
    protected Lobby lobby;

    protected final ViewTui viewTui;


    public Client() {
        this.state = UserState.USERNAME;
        this.username = null;
        this.gameController = null;
        this.lobby = null;

        this.viewTui = new ViewTui(this);
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public ViewTui getViewTui() {
        return viewTui;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
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
        setState(UserState.LOBBY_SELECTION);
    }

    public abstract void closeConnection();

    public abstract void send(MessageType messageType, Object... args);

}