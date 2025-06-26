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
 * Manages (client point of view) the connection between a client and the server via socket.
 * It encapsulates two-way communication of serialized objects,
 * connection monitoring via heartbeat, and asynchronous listening for incoming messages from the client.
 */
public class ClientSocket extends Client {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    // private HeartbeatThread heartbeatThread;
    private ListenLoopOfClient listenLoop;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public ClientSocket(UserInterface ui) {
        super(ui);
        this.connect();

        listenLoop = new ListenLoopOfClient(this);

        // Start sending ping
        scheduler.scheduleAtFixedRate(this::sendPing, Constants.HEARTBEAT_INTERVAL, Constants.HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);

        // heartbeatThread = new HeartbeatThread(this, Constants.HEARTBEAT_INTERVAL);
        // heartbeatThread.start();
    }

    public ClientSocket(UserInterface ui, String ip) {
        super(ui);
        this.connect(ip);

        listenLoop = new ListenLoopOfClient(this);

        // Start sending ping
        scheduler.scheduleAtFixedRate(this::sendPing, Constants.HEARTBEAT_INTERVAL, Constants.HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);

        // heartbeatThread = new HeartbeatThread(this, Constants.HEARTBEAT_INTERVAL);
        // heartbeatThread.start();
    }

    private void connect() {
        for (int attempt = 1; attempt <= Constants.MAX_RETRIES; attempt++) {

            try {
                ServerInfo serverInfo = DiscoveryClient.findServer();
                if (serverInfo == null) throw new ClientException();

                this.socket = new Socket(serverInfo.ipAddress, serverInfo.socketPort);
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
                // this.socket.setSoTimeout(Constants.NETWORK_TIMEOUT);
                break;

            } catch (ClientException | IOException e) {
                backoff(attempt);
            }

        }
    }

    private void connect(String ip) {
        for (int attempt = 1; attempt <= Constants.MAX_RETRIES; attempt++) {

            try {
                this.socket = new Socket(ip, Constants.DEFAULT_SOCKET_PORT);
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
                // this.socket.setSoTimeout(Constants.NETWORK_TIMEOUT);
                break;

            } catch (IOException e) {
                backoff(attempt);
            }

        }
    }

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

    private void sendPing() {
        try {
            sendObject(new Heartbeat());
        } catch (ClientException e) {
            // Error while sending ping, do nothing
            // Server-side session will be eventually closed
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void closeConnection() {
        scheduler.shutdownNow();
        if (this.socket == null) return; // Already closed

        if (this.listenLoop != null && this.listenLoop.isAlive()) {
            this.listenLoop.interrupt();
            this.listenLoop = null;
        }

//        if (this.heartbeatThread != null && this.heartbeatThread.isAlive()) {
//            this.heartbeatThread.interrupt();
//            this.heartbeatThread = null;
//        }

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

    public Object readObject() throws ClientException {
        try {
            Object obj = this.input.readObject();
            if (obj == null)
                throw new ClientException();
            return obj;
        } catch (IOException | ClassNotFoundException | ClientException e) {
            this.closeConnection();
            throw new ClientException(e.getMessage());
        }
    }

    public void sendObject(Object data) throws ClientException {
        try {
            this.output.reset(); // Use reset otherwise it sends a previous instance of the objects
            this.output.writeObject(data);
            this.output.flush();
        } catch (IOException e) {
            this.closeConnection();
            throw new ClientException(e.getMessage());
        }
    }

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

    public void receive(Message message) {
        try {
            message.execute(this);
        } catch (RuntimeException e) {
            ui.displayError(e.getMessage());
        }
    }

}