package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class UserOfClient {

    private final List<NetworkEventListener> listeners;
    private final ClientSocket clientSocket;

    public UserOfClient(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
        this.listeners = new ArrayList<>();
    }

    public void send(Message message) {
        try {
            this.clientSocket.sendObject(message);
        } catch (ClientException e) {
            notifyConnectionClosed("ERROR" + e.getMessage());
        }
    }

    public void receive(Message message) {
        try {
            notifyMessageReceived(message);
            message.execute(this);
        } catch (RuntimeException e) {
            System.err.println("[USER OF CLIENT] Receive method has caught a RuntimeException: " + e.getMessage());
            this.send(new ErrorMessage());
        }
    }


    public void addNetworkEventListener(NetworkEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeNetworkEventListener(NetworkEventListener listener) {
        listeners.remove(listener);
    }

    public void notifyMessageReceived(Message message) {
        for (NetworkEventListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }

    public void notifyConnectionEstablished() {
        for (NetworkEventListener listener : listeners) {
            listener.onConnectionEstablished();
        }
    }

    public void notifyConnectionClosed(String reason) {
        for (NetworkEventListener listener : listeners) {
            listener.onConnectionClosed(reason);
        }
    }

}
