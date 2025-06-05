package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.messages.MessageType;

public interface MessageHandler {

    void handleMessage(MessageType eventType, String username, Object... args);

    boolean canHandle(MessageType messageType);

}