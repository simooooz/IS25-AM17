package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.discovery.DiscoveryClient;
import it.polimi.ingsw.network.discovery.ServerInfo;
import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.socket.Heartbeat;
import it.polimi.ingsw.view.UserInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Client-side socket connection.
 * Manages bidirectional communication with socket clients and message processing.
 */
public class ClientSocket extends Client {

    /**
     * TCP socket connection to the client
     */
    private Socket socket;

    /**
     * Output stream for sending serialized objects to the server
     */
    private ObjectOutputStream output;

    /**
     * Input stream for receiving serialized objects from the server
     */
    private ObjectInputStream input;

    /** Thread for listening to server messages */
    private ListenLoopOfClient listenLoop;

    /** Scheduler for periodic heartbeat messages */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Creates client with automatic server discovery.
     *
     * @param ui user interface
     */
    public ClientSocket(UserInterface ui) {
        super(ui);
        this.connect();

        listenLoop = new ListenLoopOfClient(this);

        // Start sending ping
        scheduler.scheduleAtFixedRate(this::sendPing, Constants.HEARTBEAT_INTERVAL, Constants.HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates client with specific IP address.
     *
     * @param ui user interface
     * @param ip server IP address
     */
    public ClientSocket(UserInterface ui, String ip) {
        super(ui);
        this.connect(ip);

        listenLoop = new ListenLoopOfClient(this);

        // Start sending ping
        scheduler.scheduleAtFixedRate(this::sendPing, Constants.HEARTBEAT_INTERVAL, Constants.HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * Connects using server discovery with retry logic.
     */
    private void connect() {
        for (int attempt = 1; attempt <= Constants.MAX_RETRIES; attempt++) {

            try {
                ServerInfo serverInfo = DiscoveryClient.findServer();
                if (serverInfo == null) throw new ClientException();

                this.socket = new Socket(serverInfo.ipAddress, serverInfo.socketPort);
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
                this.socket.setSoTimeout(Constants.NETWORK_TIMEOUT);
                this.socket.setKeepAlive(true);
                this.socket.setTcpNoDelay(true);
                break;

            } catch (ClientException | IOException e) {
                backoff(attempt);
            }

        }
    }

    /**
     * Connects to specific IP address with retry logic.
     *
     * @param ip server IP address
     */
    private void connect(String ip) {
        for (int attempt = 1; attempt <= Constants.MAX_RETRIES; attempt++) {

            try {
                this.socket = new Socket(ip, Constants.DEFAULT_SOCKET_PORT);
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
                this.socket.setSoTimeout(Constants.NETWORK_TIMEOUT);
                this.socket.setKeepAlive(true);
                this.socket.setTcpNoDelay(true);
                break;

            } catch (IOException e) {
                backoff(attempt);
            }

        }
    }

    /**
     * Handles exponential backoff between connection attempts.
     *
     * @param attempt current attempt number
     */
    @SuppressWarnings("Duplicates")
    private void backoff(int attempt) {
        if (attempt == Constants.MAX_RETRIES) {
            System.out.println("[CLIENT SOCKET] Could not find or connect to server");
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
     * Sends heartbeat message to server.
     */
    private void sendPing() {
        try {
            sendObject(new Heartbeat());
        } catch (ClientException e) {
            // Error while sending ping, do nothing
            // Server-side session will be eventually closed
        }
    }

    /**
     * Closes connection and shuts down scheduler.
     */
    @SuppressWarnings("Duplicates")
    @Override
    public void closeConnection() {
        scheduler.shutdownNow();
        if (this.socket == null) return; // Already closed

        if (this.listenLoop != null && this.listenLoop.isAlive()) {
            this.listenLoop.interrupt();
            try {
                this.listenLoop.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            this.listenLoop = null;
        }

        try {
            this.input.close();
        } catch (IOException _) {
        } finally {
            this.input = null;
        }

        try {
            this.output.close();
        } catch (IOException _) {
        } finally {
            this.output = null;
        }

        try {
            this.socket.close();
        } catch (IOException _) {
        } finally {
            this.socket = null;
        }

        System.out.println("[CLIENT SOCKET] Closing connection...");
    }

    /**
     * Reads serialized object from server.
     *
     * @return deserialized object
     * @throws ClientException if reading fails
     */
    public Object readObject() throws ClientException {
        try {
            Object obj = this.input.readObject();
            if (obj == null)
                throw new ClientException();
            return obj;
        } catch (IOException | ClassNotFoundException | ClientException e) {
            System.out.println("[CLIENT SOCKET - sendObject] " + System.currentTimeMillis());
            e.printStackTrace();
            this.closeConnection();
            throw new ClientException(e.getMessage());
        }
    }

    /**
     * Sends serialized object to server.
     *
     * @param data object to send
     * @throws ClientException if sending fails
     */
    public void sendObject(Object data) throws ClientException {
        try {
            this.output.reset(); // Use reset otherwise it sends a previous instance of the objects
            this.output.writeObject(data);
            this.output.flush();
        } catch (IOException e) {
            System.out.println("[CLIENT SOCKET - sendObject] " + System.currentTimeMillis());
            e.printStackTrace();
            this.closeConnection();
            throw new ClientException(e.getMessage());
        }
    }

    /**
     * Sends message to server.
     *
     * @param messageType message type
     * @param args message arguments
     */
    @Override
    public void send(MessageType messageType, Object... args) {
        try {
            Message message = Constants.createMessage(messageType, args);
            this.sendObject(message);
        } catch (ClientException e) {
            // System.err.println("[CLIENT SOCKET] Error while sending message: " + e.getMessage());
            // Everything should be closed
        }
    }

    /**
     * Processes received message from server.
     *
     * @param message received message
     */
    public void receive(Message message) {
        try {
            message.execute(this);
        } catch (RuntimeException e) {
            ui.displayError(e.getMessage());
        }
    }

}
