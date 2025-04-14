package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;

public class UserOfClient {

    private final ClientSocket clientSocket;
    private String username;
    private GameController gameController;

    public UserOfClient(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
        this.username = null;
        this.gameController = null;
    }

    public void send(Message message) {
        try {
            this.clientSocket.sendObject(message);
        } catch (ClientException e) {
            System.err.println("[USER OF CLIENT] Error while sending message: " + e.getMessage());
            // Everything should be closed
        }
    }

    public void receive(Message message) {
        try {
            message.execute(this);
        } catch (RuntimeException e) {
            System.err.println("[USER OF CLIENT] Receive method has caught a RuntimeException: " + e.getMessage());
            this.send(new ErrorMessage());
        }
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // todo è sicuro/devo fare una get della socket?
    public ClientSocket getSocket() {
        return this.clientSocket;
    }

}
