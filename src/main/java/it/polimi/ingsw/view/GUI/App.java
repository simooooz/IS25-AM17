package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.common.model.events.game.ErrorEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.rmi.RMIClient;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.view.UserInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

/**
 * Main application class for the GUI client implementation.
 *
 * This class serves as the entry point for the JavaFX GUI application and implements
 * the {@link UserInterface} contract. It manages the application lifecycle, network
 * client initialization, and event handling between the client model and the GUI layer.
 *
 * @see Application
 * @see UserInterface
 * @see ClientEventBus
 * @see MessageDispatcher
 * @see SceneManager
 */
public class App extends Application implements UserInterface {

    /**
     * The client instance used for network communication.
     * This can be either a {@link ClientSocket} or {@link RMIClient} depending
     * on the network type specified during initialization.
     */
    private static Client client;

    /**
     * Initializes and starts the GUI application with network connectivity.

     *
     * The network client is created based on the specified network type:
     * - Type 1: Socket-based communication ({@link ClientSocket})
     * - Other: RMI-based communication ({@link RMIClient})
     *
     * @param networkType the type of network connection to use (1 for Socket, other for RMI)
     * @param ip the server IP address to connect to. If blank, uses default server address
     */
    @Override
    public void start(int networkType, String ip) {
        ClientEventBus.getInstance().subscribe(this);

        Platform.startup(() -> {
            start(new Stage());
            SceneManager.navigateToScene("/fxml/loading.fxml", null, null);

            new Thread(() -> {
                if (ip.isBlank())
                    client = networkType == 1 ? new ClientSocket(this) : new RMIClient(this);
                else
                    client = networkType == 1 ? new ClientSocket(this, ip) : new RMIClient(this, ip);
                Platform.runLater(() -> SceneManager.navigateToScene("/fxml/login.fxml", null, null));
            }).start();
        });
    }

    /**
     * Initializes the JavaFX application stage and resources.
     *
     * @param stage the primary {@link Stage} provided by the JavaFX framework
     * @throws RuntimeException if font loading fails or stage initialization encounters errors
     */
    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResource("/fonts/Audiowide-Regular.ttf").toExternalForm(), 14);
        SceneManager.init(stage);
    }

    /**
     * Returns the current network client instance.
     *
     * @return the current {@link Client} instance, or {@code null} if the client
     *         has not been initialized yet
     */
    public static Client getClientInstance() {
        return client;
    }

    /**
     * Handles incoming events from the client event bus.
     *
     * This method is called when events are received from the server or generated
     * by the client model. Each event is dispatched to the GUI layer using the
     * {@link MessageDispatcher} on the JavaFX Application Thread to ensure
     * thread safety for UI updates.
     *
     * @param events a {@link List} of {@link Event} objects to be processed.
     *              Must not be null, but can be empty.
     * @throws IllegalArgumentException if events list is null
     */
    @Override
    public void onEvent(List<Event> events) {
        for (Event event : events)
            Platform.runLater(() -> MessageDispatcher.getInstance().dispatchMessage(event));
    }

    /**
     * Displays an error message to the user.
     *
     * This method creates an {@link ErrorEvent} with the specified message and
     * dispatches it through the message system for display in the GUI.
     * The error will be handled by registered error message handlers.
     *
     * @param message the error message to display to the user. Should not be null or empty.
     */
    @Override
    public void displayError(String message) {
        MessageDispatcher.getInstance().dispatchMessage(new ErrorEvent(message));
    }
}