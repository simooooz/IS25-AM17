package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.discovery.DiscoveryClient;
import it.polimi.ingsw.network.discovery.ServerInfo;
import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.UserInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * RMI client implementation that handles communication with the RMI server.
 * This class extends the base Client class and provides RMI-specific
 * functionality for connecting to and communicating with the game server.
 * <p>
 * The client manages its own session, handles server discovery or direct connection,
 * implements connection retry logic with exponential backoff, and maintains
 * connection health through periodic ping messages.
 */
public class RMIClient extends Client {

    /**
     * Unique session identifier for this client instance
     */
    private final String sessionCode;

    /**
     * Reference to the remote server interface
     */
    private RMIServerInterface server;

    /**
     * Callback object for receiving messages from the server
     */
    private ClientCallback clientCallback;

    /**
     * Scheduled executor service for sending periodic ping messages
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * @param ui the user interface for displaying messages and errors
     */
    public RMIClient(UserInterface ui) {
        super(ui);

        System.setProperty("sun.rmi.transport.tcp.responseTimeout", String.valueOf(Constants.NETWORK_TIMEOUT));
        System.setProperty("sun.rmi.transport.tcp.handshakeTimeout", "10000");

        this.sessionCode = UUID.randomUUID().toString();
        connect();

        // Start sending ping
        scheduler.scheduleAtFixedRate(this::sendPing, Constants.HEARTBEAT_INTERVAL, Constants.HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * @param ui the user interface for displaying messages and errors
     * @param ip the IP address of the server to connect to
     */
    public RMIClient(UserInterface ui, String ip) {
        super(ui);

        System.setProperty("sun.rmi.transport.tcp.responseTimeout", String.valueOf(Constants.NETWORK_TIMEOUT));
        System.setProperty("sun.rmi.transport.tcp.handshakeTimeout", "10000");

        this.sessionCode = UUID.randomUUID().toString();
        connect(ip);

        // Start sending ping
        scheduler.scheduleAtFixedRate(this::sendPing, Constants.HEARTBEAT_INTERVAL, Constants.HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * Establishes connection to the server using automatic discovery.
     * Uses retry logic with exponential backoff on connection failures.
     */
    private void connect() {
        for (int attempt = 1; attempt <= Constants.MAX_RETRIES; attempt++) {

            try {
                ServerInfo serverInfo = DiscoveryClient.findServer();
                if (serverInfo == null) throw new ClientException();

                Registry registry = LocateRegistry.getRegistry(serverInfo.ipAddress, serverInfo.rmiPort);
                server = (RMIServerInterface) registry.lookup("ServerRMI");

                clientCallback = new ClientCallback(this);
                server.registerClient(sessionCode, clientCallback);
                break;

            } catch (ClientException | NotBoundException | RemoteException e) {
                backoff(attempt);
            }

        }
    }

    /**
     * Establishes connection to a specific server IP address.
     *
     * @param ip the IP address of the server to connect to
     */
    private void connect(String ip) {
        for (int attempt = 1; attempt <= Constants.MAX_RETRIES; attempt++) {

            try {
                Registry registry = LocateRegistry.getRegistry(ip, Constants.DEFAULT_RMI_PORT);
                server = (RMIServerInterface) registry.lookup("ServerRMI");

                clientCallback = new ClientCallback(this);
                server.registerClient(sessionCode, clientCallback);
                break;

            } catch (NotBoundException | RemoteException e) {
                backoff(attempt);
            }

        }
    }

    /**
     * Implements exponential backoff strategy for connection retries.
     *
     * @param attempt the current attempt number (1-based)
     */
    @SuppressWarnings("Duplicates")
    private void backoff(int attempt) {
        if (attempt == Constants.MAX_RETRIES) {
            System.out.println("[RMI CLIENT] Could not find or connect to server");
            System.exit(-1);
        }

        int delay = Math.min(Constants.BASE_DELAY * (int) Math.pow(2, attempt - 1), Constants.MAX_DELAY);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.exit(-1);
        }
    }

    /**
     * Sends a ping message to the server to maintain connection health.
     * This method is called periodically by the scheduler to verify
     * that the connection is still active. If the ping fails, the
     * server will eventually close the session.
     */
    private void sendPing() {
        try {
            server.ping(sessionCode);
        } catch (RemoteException e) {
            // Error while sending ping, do nothing
            // Server-side session will be eventually closed
        }
    }

    /**
     * Closes the connection to the server and cleans up resources.
     * Shuts down the ping scheduler, unregisters the client from the server,
     * and unexports the callback object to prevent memory leaks.
     */
    @Override
    public void closeConnection() {
        scheduler.shutdownNow();

        try {
            server.unregisterClient(sessionCode);
            UnicastRemoteObject.unexportObject(clientCallback, true);
        } catch (RemoteException e) {
            // Error while unregistering client
            // Do nothing
        }

    }

    /**
     * Sends a message to the server based on the message type and arguments.
     * This method acts as a dispatcher, converting the generic message format
     * into specific RMI method calls on the server interface.
     *
     * @param messageType the type of message to send
     * @param args        the arguments for the message, which are cast to appropriate types
     *                    based on the message type
     */
    @SuppressWarnings("unchecked")
    @Override
    public void send(MessageType messageType, Object... args) {
        try {
            switch (messageType) {
                case SET_USERNAME -> server.setUsernameHandler(sessionCode, (String) args[0]);
                case CREATE_LOBBY ->
                        server.createLobbyHandler(sessionCode, (String) args[0], (Integer) args[1], (Boolean) args[2]);
                case JOIN_LOBBY -> server.joinLobbyHandler(sessionCode, (String) args[0]);
                case JOIN_RANDOM_LOBBY -> server.joinRandomLobbyHandler(sessionCode, (Boolean) args[0]);
                case LEAVE_GAME -> server.leaveGameHandler(sessionCode);
                case PICK_COMPONENT -> server.pickComponentHandler(sessionCode, (Integer) args[0]);
                case RELEASE_COMPONENT -> server.releaseComponentHandler(sessionCode, (Integer) args[0]);
                case RESERVE_COMPONENT -> server.reserveComponentHandler(sessionCode, (Integer) args[0]);
                case INSERT_COMPONENT ->
                        server.insertComponentHandler(sessionCode, (Integer) args[0], (Integer) args[1], (Integer) args[2], (Integer) args[3]);
                case MOVE_COMPONENT ->
                        server.moveComponentHandler(sessionCode, (Integer) args[0], (Integer) args[1], (Integer) args[2], (Integer) args[3]);
                case ROTATE_COMPONENT ->
                        server.rotateComponentHandler(sessionCode, (Integer) args[0], (Integer) args[1]);
                case LOOK_CARD_PILE -> server.lookCardPileHandler(sessionCode, (Integer) args[0]);
                case RELEASE_CARD_PILE -> server.releaseCardPileHandler(sessionCode);
                case MOVE_HOURGLASS -> server.moveHourglassHandler(sessionCode);
                case SET_READY -> server.setReadyHandler(sessionCode);
                case CHECK_SHIP -> server.checkShipHandler(sessionCode, (List<Integer>) args[0]);
                case CHOOSE_ALIEN -> server.chooseAlienHandler(sessionCode, (Map<Integer, AlienType>) args[0]);
                case CHOOSE_SHIP_PART -> server.chooseShipPartHandler(sessionCode, (Integer) args[0]);
                case DRAW_CARD -> server.drawCardHandler(sessionCode);
                case ACTIVATE_CANNONS ->
                        server.activateCannonsHandler(sessionCode, (List<Integer>) args[0], (List<Integer>) args[1]);
                case ACTIVATE_ENGINES ->
                        server.activateEnginesHandler(sessionCode, (List<Integer>) args[0], (List<Integer>) args[1]);
                case ACTIVATE_SHIELD -> server.activateShieldHandler(sessionCode, (Integer) args[0]);
                case UPDATE_GOODS ->
                        server.updateGoodsHandler(sessionCode, (Map<Integer, List<ColorType>>) args[0], (List<Integer>) args[1]);
                case REMOVE_CREW -> server.removeCrewHandler(sessionCode, (List<Integer>) args[0]);
                case ROLL_DICES -> server.rollDicesHandler(sessionCode);
                case GET_BOOLEAN -> server.getBooleanHandler(sessionCode, (Boolean) args[0]);
                case GET_INDEX -> server.getIndexHandler(sessionCode, (Integer) args[0]);
                case END_FLIGHT -> server.endFlightHandler(sessionCode);
            }
        } catch (RemoteException | RuntimeException e) {
            ui.displayError(e.getMessage());
        }
    }

}
