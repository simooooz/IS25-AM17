package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.network.messages.MessageType;

/**
 * Interface for handling specific types of messages in the GUI layer.
 *
 * This interface defines a contract for message handlers that can process
 * specific types of events. Implementations of this interface should handle
 * the processing of events and indicate which message types they can handle.
 *
 * @see Event
 * @see MessageType
 */
public interface MessageHandler {

    /**
     * Handles the processing of a specific event.
     *
     * This method is called when an event needs to be processed by this handler.
     * Implementations should contain the logic to appropriately handle the event,
     * which may include updating the GUI, triggering other actions, or modifying
     * the application state.
     *
     * @param event the {@link Event} to be processed. Must not be null.
     * @throws IllegalArgumentException if the event is null or of an unsupported type
     * @throws RuntimeException if an error occurs during event processing
     */
    void handleMessage(Event event);

    /**
     * Determines whether this handler can process messages of the specified type.
     *
     * @param messageType the {@link MessageType} to check for compatibility.
     *                   Must not be null.
     * @return {@code true} if this handler can process messages of the specified type,
     *         {@code false} otherwise
     * @throws IllegalArgumentException if messageType is null
     */
    boolean canHandle(MessageType messageType);
}