package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * RMI remote interface for game server management.
 * Defines all available methods for client-server interaction
 * through the RMI protocol.
 */
public interface RMIServerInterface extends Remote {

    /**
     * Registers a client with the server by associating it with a session code.
     *
     * @param sessionCode the unique session code for the client
     * @param callback the callback interface for communications to the client
     * @throws RemoteException if an RMI communication error occurs
     */
    void registerClient(String sessionCode, ClientCallbackInterface callback) throws RemoteException;

    /**
     * Unregisters a client from the server.
     *
     * @param sessionCode the session code of the client to remove
     * @throws RemoteException if an RMI communication error occurs
     */
    void unregisterClient(String sessionCode) throws RemoteException;

    /**
     * Checks the connection with the client (ping operation).
     * Used to verify that the connection is still active.
     *
     * @param sessionCode the client's session code
     * @throws RemoteException if an RMI communication error occurs
     */
    void ping(String sessionCode) throws RemoteException;

    /**
     * Sets the username for the specified session.
     *
     * @param sessionCode the session code
     * @param username the username to associate with the session
     * @throws RemoteException if an RMI communication error occurs
     */
    void setUsernameHandler(String sessionCode, String username) throws RemoteException;

    /**
     * Creates a new game lobby.
     *
     * @param sessionCode the session code of the creator
     * @param name the name of the lobby
     * @param maxPlayers the maximum number of players
     * @param learnerMode if true, enables beginner mode
     * @throws RemoteException if an RMI communication error occurs
     */
    void createLobbyHandler(String sessionCode, String name, Integer maxPlayers, Boolean learnerMode) throws RemoteException;

    /**
     * Joins the player to an existing lobby specified by name.
     *
     * @param sessionCode the player's session code
     * @param lobbyName the name of the lobby to join
     * @throws RemoteException if an RMI communication error occurs
     */
    void joinLobbyHandler(String sessionCode, String lobbyName) throws RemoteException;

    /**
     * Joins the player to a random available lobby.
     *
     * @param sessionCode the player's session code
     * @param learnerMode if true, searches for lobbies in beginner mode
     * @throws RemoteException if an RMI communication error occurs
     */
    void joinRandomLobbyHandler(String sessionCode, Boolean learnerMode) throws RemoteException;

    /**
     * Removes the player from the current game.
     *
     * @param sessionCode the player's session code
     * @throws RemoteException if an RMI communication error occurs
     */
    void leaveGameHandler(String sessionCode) throws RemoteException;

    /**
     * Picks a component from the available pool.
     *
     * @param sessionCode the player's session code
     * @param id the identifier of the component to pick
     * @throws RemoteException if an RMI communication error occurs
     */
    void pickComponentHandler(String sessionCode, Integer id) throws RemoteException;

    /**
     * Releases a previously picked or placed but not welded component.
     *
     * @param sessionCode the player's session code
     * @param id the identifier of the component to release
     * @throws RemoteException if an RMI communication error occurs
     */
    void releaseComponentHandler(String sessionCode, Integer id) throws RemoteException;

    /**
     * Reserves a component for future use.
     *
     * @param sessionCode the player's session code
     * @param id the identifier of the component to reserve
     * @throws RemoteException if an RMI communication error occurs
     */
    void reserveComponentHandler(String sessionCode, Integer id) throws RemoteException;

    /**
     * Inserts a component into the ship.
     *
     * @param sessionCode the player's session code
     * @param id the component identifier
     * @param row the destination row
     * @param col the destination column
     * @param rotations the number of rotations to apply (0-3)
     * @throws RemoteException if an RMI communication error occurs
     */
    void insertComponentHandler(String sessionCode, Integer id, Integer row, Integer col, Integer rotations) throws RemoteException;

    /**
     * Moves an already placed but not welded component to a new position.
     *
     * @param sessionCode the player's session code
     * @param id the identifier of the component to move
     * @param row the new destination row
     * @param col the new destination column
     * @param rotations the number of rotations to apply
     * @throws RemoteException if an RMI communication error occurs
     */
    void moveComponentHandler(String sessionCode, Integer id, Integer row, Integer col, Integer rotations) throws RemoteException;

    /**
     * Rotates a component by a specified number of quarter turns.
     *
     * @param sessionCode the player's session code
     * @param id the identifier of the component to rotate
     * @param num the number of rotations to apply
     * @throws RemoteException if an RMI communication error occurs
     */
    void rotateComponentHandler(String sessionCode, Integer id, Integer num) throws RemoteException;

    /**
     * Examines an available card pile.
     *
     * @param sessionCode the player's session code
     * @param pileIndex the index of the card pile to examine
     * @throws RemoteException if an RMI communication error occurs
     */
    void lookCardPileHandler(String sessionCode, Integer pileIndex) throws RemoteException;

    /**
     * Releases the examination of a card pile.
     *
     * @param sessionCode the player's session code
     * @throws RemoteException if an RMI communication error occurs
     */
    void releaseCardPileHandler(String sessionCode) throws RemoteException;

    /**
     * Moves the hourglass to manage game time.
     *
     * @param sessionCode the player's session code
     * @throws RemoteException if an RMI communication error occurs
     */
    void moveHourglassHandler(String sessionCode) throws RemoteException;

    /**
     * Sets the player as ready for the next phase.
     *
     * @param sessionCode the player's session code
     * @throws RemoteException if an RMI communication error occurs
     */
    void setReadyHandler(String sessionCode) throws RemoteException;

    /**
     * Checks the validity of the built ship and removes specified components.
     *
     * @param sessionCode the player's session code
     * @param toRemove list of component identifiers to remove
     * @throws RemoteException if an RMI communication error occurs
     */
    void checkShipHandler(String sessionCode, List<Integer> toRemove) throws RemoteException;

    /**
     * Chooses aliens to assign to specific cabins.
     *
     * @param sessionCode the player's session code
     * @param aliensIds map associating component IDs with alien types
     * @throws RemoteException if an RMI communication error occurs
     */
    void chooseAlienHandler(String sessionCode, Map<Integer, AlienType> aliensIds) throws RemoteException;

    /**
     * Chooses a ship part.
     *
     * @param sessionCode the player's session code
     * @param partIndex the index of the chosen ship part
     * @throws RemoteException if an RMI communication error occurs
     */
    void chooseShipPartHandler(String sessionCode, Integer partIndex) throws RemoteException;

    /**
     * Draws a card from the deck.
     *
     * @param sessionCode the player's session code
     * @throws RemoteException if an RMI communication error occurs
     */
    void drawCardHandler(String sessionCode) throws RemoteException;

    /**
     * Activates cannons using the specified batteries.
     *
     * @param sessionCode the player's session code
     * @param batteriesIds list of battery IDs to use
     * @param cannonComponentsIds list of cannon component IDs to activate
     * @throws RemoteException if an RMI communication error occurs
     */
    void activateCannonsHandler(String sessionCode, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) throws RemoteException;

    /**
     * Activates engines using the specified batteries.
     *
     * @param sessionCode the player's session code
     * @param batteriesIds list of battery IDs to use
     * @param engineComponentsIds list of engine component IDs to activate
     * @throws RemoteException if an RMI communication error occurs
     */
    void activateEnginesHandler(String sessionCode, List<Integer> batteriesIds, List<Integer> engineComponentsIds) throws RemoteException;

    /**
     * Activates a shield using a specific battery.
     *
     * @param sessionCode the player's session code
     * @param batteryId the ID of the battery to use for the shield
     * @throws RemoteException if an RMI communication error occurs
     */
    void activateShieldHandler(String sessionCode, Integer batteryId) throws RemoteException;

    /**
     * Updates goods in cargo holds.
     *
     * @param sessionCode the player's session code
     * @param cargoHoldsIds map associating cargo hold IDs with goods color types
     * @param batteriesIds list of battery IDs involved in the operation
     * @throws RemoteException if an RMI communication error occurs
     */
    void updateGoodsHandler(String sessionCode, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) throws RemoteException;

    /**
     * Removes crew members from specified cabins.
     *
     * @param sessionCode the player's session code
     * @param cabinsIds list of cabin IDs from which to remove crew
     * @throws RemoteException if an RMI communication error occurs
     */
    void removeCrewHandler(String sessionCode, List<Integer> cabinsIds) throws RemoteException;

    /**
     * Rolls dice to determine random events.
     *
     * @param sessionCode the player's session code
     * @throws RemoteException if an RMI communication error occurs
     */
    void rollDicesHandler(String sessionCode) throws RemoteException;

    /**
     * Handles a boolean response from the player.
     *
     * @param sessionCode the player's session code
     * @param value the boolean value chosen by the player
     * @throws RemoteException if an RMI communication error occurs
     */
    void getBooleanHandler(String sessionCode, Boolean value) throws RemoteException;

    /**
     * Handles a numeric response from the player.
     *
     * @param sessionCode the player's session code
     * @param value the numeric value chosen by the player
     * @throws RemoteException if an RMI communication error occurs
     */
    void getIndexHandler(String sessionCode, Integer value) throws RemoteException;

    /**
     * Ends the current flight earlier.
     *
     * @param sessionCode the player's session code
     * @throws RemoteException if an RMI communication error occurs
     */
    void endFlightHandler(String sessionCode) throws RemoteException;

}
