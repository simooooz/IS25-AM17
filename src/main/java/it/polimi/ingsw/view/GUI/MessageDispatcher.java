package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.view.GUI.fxmlcontroller.MessageHandler;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MessageDispatcher {

    private static MessageDispatcher instance;
    private final List<MessageHandler> handlers;
    private final Queue<GameEvent> pendingEvents = new LinkedList<>();
    private boolean isTransitioning = false;

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

    public void setTransitioning(boolean transitioning) {
        this.isTransitioning = transitioning;
        if (!transitioning) {
            flushPendingEvents();
        }
    }

    private void flushPendingEvents() {
        while (!pendingEvents.isEmpty()) {
            GameEvent event = pendingEvents.poll();

            for (MessageHandler handler : handlers)
                if (handler.canHandle(event.eventType()))
                    handler.handleMessage(event);
        }
    }

    public void dispatchMessage(GameEvent event) {
        Platform.runLater(() -> {
            if (isTransitioning) {
                pendingEvents.offer(event);
                return;
            }

            for (MessageHandler handler : handlers)
                if (handler.canHandle(event.eventType()))
                    handler.handleMessage(event);
        });
    }

}