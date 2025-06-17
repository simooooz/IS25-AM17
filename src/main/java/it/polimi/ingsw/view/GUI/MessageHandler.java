package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;

public interface MessageHandler {

    void handleMessage(GameEvent event);

    boolean canHandle(MessageType messageType);

}