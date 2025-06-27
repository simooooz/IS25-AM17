package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.common.model.events.Event;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Singleton class responsible for dispatching events to registered message handlers
 * in a JavaFX application.
 *
 * This class implements the Observer pattern and provides a centralized mechanism
 * for routing events to appropriate handlers. It supports event queueing during
 * UI transitions to prevent processing conflicts and ensures all operations
 * are executed on the JavaFX Application Thread.
 *
 * The dispatcher maintains a registry of {@link MessageHandler} instances and
 * routes incoming events to handlers that can process them based on the event type.
 *
 * @see MessageHandler
 * @see Event
 */
public class MessageDispatcher {

    /**
     * The singleton instance of the MessageDispatcher.
     */
    private static MessageDispatcher instance;

    /**
     * List of registered message handlers that can process events.
     */
    private final List<MessageHandler> handlers;

    /**
     * Queue for storing events that arrive during UI transitions.
     */
    private final Queue<Event> pendingEvents = new LinkedList<>();

    /**
     * Flag indicating whether the UI is currently in a transitioning state.
     * When true, incoming events are queued instead of being processed immediately.
     */
    private boolean isTransitioning = false;

    private MessageDispatcher() {
        this.handlers = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of MessageDispatcher.
     *
     * Creates a new instance if one doesn't exist. This method is not thread-safe
     * and should be called from the main application thread during initialization.
     *
     * @return the singleton {@link MessageDispatcher} instance
     */
    public static MessageDispatcher getInstance() {
        if (instance == null) {
            instance = new MessageDispatcher();
        }
        return instance;
    }

    /**
     * Registers a message handler to receive events.
     *
     * The handler will be added to the list of registered handlers and will
     * receive events for which it indicates it can handle via the
     * {@link MessageHandler#canHandle(it.polimi.ingsw.network.messages.MessageType)} method.
     *
     * @param handler the {@link MessageHandler} to register. Must not be null.
     * @throws IllegalArgumentException if handler is null
     */
    public void registerHandler(MessageHandler handler) {
        handlers.add(handler);
    }

    /**
     * Unregisters a previously registered message handler.
     *
     * Removes the handler from the list of registered handlers. The handler
     * will no longer receive events after this method is called.
     *
     * @param handler the {@link MessageHandler} to unregister. If the handler
     *               is not currently registered, this method has no effect.
     */
    public void unregisterHandler(MessageHandler handler) {
        handlers.remove(handler);
    }

    /**
     * Sets the transitioning state of the dispatcher.
     *
     * When transitioning is set to true, incoming events will be queued instead
     * of being processed immediately. When set to false, any queued events will
     * be processed immediately.
     *
     * @param transitioning {@code true} to queue incoming events, {@code false}
     *                     to process events immediately and flush any pending events
     */
    public void setTransitioning(boolean transitioning) {
        this.isTransitioning = transitioning;
        if (!transitioning) {
            flushPendingEvents();
        }
    }

    /**
     * Processes all events currently in the pending queue.
     *
     * This method is called automatically when transitioning is set to false.
     * It processes events in FIFO order, dispatching each event to all handlers
     * that can process it.
     */
    private void flushPendingEvents() {
        while (!pendingEvents.isEmpty()) {
            Event event = pendingEvents.poll();

            for (MessageHandler handler : handlers)
                if (handler.canHandle(event.eventType()))
                    handler.handleMessage(event);
        }
    }

    /**
     * Dispatches an event to all registered handlers that can process it.
     *
     * If the dispatcher is currently in transitioning mode, the event will be
     * queued for later processing. Otherwise, the event is immediately dispatched
     * to all compatible handlers.
     */
    public void dispatchMessage(Event event) {
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