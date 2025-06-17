package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class MessageDispatcher {
    private static MessageDispatcher instance;
    private final List<MessageHandler> handlers;

    private MessageDispatcher() {
        this.handlers = new ArrayList<>();
    }

    public static MessageDispatcher getInstance() {
        if (instance == null) {
            instance = new MessageDispatcher();
        }
        return instance;
    }

    public void registerHandler(MessageHandler handler) {
        handlers.add(handler);
    }

    public void unregisterHandler(MessageHandler handler) {
        handlers.remove(handler);
    }

    public void dispatchMessage(GameEvent event) {
        Platform.runLater(() -> {
            for (MessageHandler handler : handlers)
                if (handler.canHandle(event.eventType()))
                    handler.handleMessage(event);
        });
    }

}