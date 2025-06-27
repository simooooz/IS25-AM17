package it.polimi.ingsw.network;

import it.polimi.ingsw.client.controller.ClientGameController;
import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.client.model.game.ClientLobby;
import it.polimi.ingsw.common.model.events.lobby.SetLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.UsernameOkEvent;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.UserInterface;

/**
 * Abstract class representing a client in the game.
 * Manages the client's state, connection to the server, and user interface.
 * Serves as a base for concrete client implementations.
 */
public abstract class Client {

    /**
     * The client's username
     */
    protected String username;

    /**
     * Current state of the client
     */
    protected UserState state;

    /**
     * Lobby the client is connected to, null if not in a lobby
     */
    protected ClientLobby lobby;

    /**
     * User interface used by the client
     */
    protected UserInterface ui;

    /**
     * @param ui the user interface to be used by this client
     */
    public Client(UserInterface ui) {
        this.ui = ui;
        this.state = UserState.USERNAME;
        this.username = null;
        this.lobby = null;
    }

    public ClientGameController getGameController() {
        return lobby.getGame();
    }

    public ClientLobby getLobby() {
        return lobby;
    }

    public void setLobby(ClientLobby lobby) {
        this.lobby = lobby;
        ClientEventBus.getInstance().publish(lobby == null ? new LeftLobbyEvent(username, null) : new SetLobbyEvent(lobby.getGameID(), lobby.getPlayers(), lobby.isLearnerMode(), lobby.getMaxPlayers()));
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

    /**
     * Closes the client's connection.
     * Abstract method that subclasses must implement
     * to handle connection-specific closing procedures.
     */
    public abstract void closeConnection();

    /**
     * Sends a message to the server.
     * Abstract method that subclasses must implement
     * to handle connection-specific message sending.
     *
     * @param messageType the type of message to send
     * @param args        the message arguments, variable number of parameters
     */
    public abstract void send(MessageType messageType, Object... args);

}
