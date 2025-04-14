package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;

import java.net.Socket;

public class User {

    private final ServerHandler clientSocket;
    private String username;
    private GameController gameController;

    public User(ServerHandler clientSocket) {
        this.clientSocket = clientSocket;
        this.username = null;
        this.gameController = null;
    }

    public void send(Message message) {
        try {
            this.clientSocket.send(message);
        } catch (ClientException e) {
            System.err.println("[CLIENT] Error while sending message: " + e.getMessage());
            // Everything should be closed
        }
    }

    public void receive(Message message) {
        try {
            message.execute(this);
        } catch (RuntimeException e) {
            System.err.println("[CLIENT] Receive method has caught a RuntimeException: " + e.getMessage());
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
    public ServerHandler getSocket() {
        return this.clientSocket;
    }

}
