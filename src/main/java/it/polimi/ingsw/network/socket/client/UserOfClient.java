package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class UserOfClient {

    private final ClientSocket clientSocket;
    private String username;
    private GameController gameController;
    private final List<NetworkEventListener> listeners = new ArrayList<>();

    public UserOfClient(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
        this.username = null;
        this.gameController = null;
    }

    public void addNetworkEventListener(NetworkEventListener listener) {
        listeners.add(listener);
    }

    public void removeNetworkEventListener(NetworkEventListener listener) {
        listeners.remove(listener);
    }

    public void send(Message message) {
        try {
            this.clientSocket.sendObject(message);
        } catch (ClientException e) {
            System.err.println("[USER OF CLIENT] Error while sending message: " + e.getMessage());
            notifyListenersConnectionClosed(e.getMessage());
        }
    }

    public void receive(Message message) {
        try {
            notifyListenersMessageReceived(message);
            message.execute(this);
        } catch (RuntimeException e) {
            System.err.println("[USER OF CLIENT] Receive method has caught a RuntimeException: " + e.getMessage());
            this.send(new ErrorMessage());
        }
    }

    private void notifyListenersMessageReceived(Message message) {
        for (NetworkEventListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }

    private void notifyListenersConnectionClosed(String reason) {
        for (NetworkEventListener listener : listeners) {
            listener.onConnectionClosed(reason);
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

    public ClientSocket getSocket() {
        return this.clientSocket;
    }
}
