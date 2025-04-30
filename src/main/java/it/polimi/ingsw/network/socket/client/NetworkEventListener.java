package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.network.messages.Message;

public interface NetworkEventListener {
    void onMessageReceived(Message message);
    void onConnectionClosed(String reason);
    void onConnectionEstablished();
}