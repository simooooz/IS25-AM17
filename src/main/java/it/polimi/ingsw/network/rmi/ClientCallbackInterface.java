package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.network.messages.MessageType;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI callback interface for server-to-client communication.
 * This interface allows the server to send asynchronous notifications
 * and game events to connected clients through RMI callbacks.
 * <p>
 * The callback mechanism enables bidirectional communication in the
 * RMI architecture, where the server can initiate communication with
 * clients to notify them of game state changes, events, or other
 * important information without the client having to poll the server.
 */
public interface ClientCallbackInterface extends Remote {

    /**
     * Notifies the client of a game event or server message.
     * The server calls this method to send asynchronous notifications
     * to the client, such as game state updates, player actions, lobby changes,
     * or any other events that require client notification.
     * <p>
     * The method uses a flexible parameter system where the event type
     * determines the meaning and expected types of the parameters.
     *
     * @param eventType the type of event being notified, which determines
     *                  how the parameters should be interpreted
     * @param params    variable number of parameters containing event-specific data.
     *                  The types and meaning of these parameters depend on the eventType:
     *                  <ul>
     *                  <li>For lobby events: may contain lobby name, player count, etc.</li>
     *                  <li>For game events: may contain component IDs, positions, scores, etc.</li>
     *                  <li>For system events: may contain error messages, connection status, etc.</li>
     *                  </ul>
     * @throws RemoteException if an RMI communication error occurs during the callback
     */
    void notifyGameEvent(MessageType eventType, Object... params) throws RemoteException;

}
