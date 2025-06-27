package it.polimi.ingsw.view;

import it.polimi.ingsw.client.model.ClientEventObserver;

/**
 * Interface that defines the contract for user interface implementations in the application.
 * This interface extends ClientEventObserver to enable event handling capabilities and provides
 * essential methods for user interface initialization and error display.
 *
 * <p>This interface serves as an abstraction layer that allows different types of user interfaces
 * (GUI, CLI, etc.) to be used interchangeably within the application architecture.</p>
 *
 * @see ClientEventObserver
 */
public interface UserInterface extends ClientEventObserver {

    /**
     * Displays an error message to the user through the user interface.
     * The implementation should ensure that error messages are presented in a way
     * that is appropriate for the specific user interface type (e.g., dialog boxes
     * for GUI, console output for CLI).
     *
     * @param message the error message to display to the user. Should not be null or empty.
     */
    void displayError(String message);

    /**
     * Starts the user interface with the specified network configuration.
     * This method initializes the user interface and establishes a connection to the
     * game server using the provided network type and IP address.
     *
     * @param networkType the type of network connection to establish.
     *                   Typically 1 for Socket connection, other values for RMI connection.
     * @param ip the IP address of the server to connect to.
     *           If blank or empty, the implementation should use default server configuration.
     */
    void start(int networkType, String ip);
}