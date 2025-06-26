package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.network.messages.MessageType;

public interface MessageHandler {

    void handleMessage(Event event);

    boolean canHandle(MessageType messageType);

}