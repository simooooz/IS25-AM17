package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.view.TUI.UIState;

public interface NetworkEventListener {
    void onMessageReceived(Message message);
    void onConnectionEstablished();
    void onConnectionClosed(String reason);

    void onUIStateChanged();
}
