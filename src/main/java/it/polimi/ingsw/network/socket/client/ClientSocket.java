package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Manages (client point of view) the connection between a client and the server via socket.
 * It encapsulates two-way communication of serialized objects,
 * connection monitoring via heartbeat, and asynchronous listening for incoming messages from the client.
 */
public class ClientSocket extends Client {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private HeartbeatThread heartbeatThread;
    private ListenLoopOfClient listenLoop;

    /**
     * Constructor
     *
     * @param host
     * @param port
     */
    public ClientSocket(String host, int port) {
        this.connect(host, port);

        heartbeatThread = new HeartbeatThread(this, Constants.HEARTBEAT_INTERVAL);
        heartbeatThread.start();

        listenLoop = new ListenLoopOfClient(this);
        this.viewTui.start();
    }

    private void connect(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
            this.socket.setSoTimeout(Constants.SOCKET_TIMEOUT);
        } catch (IOException e) {
            System.out.println("Could not connect to " + ip + ":" + port);
            System.exit(-1);
            // TODO capiamo forse system exit dopo un po' di retry
        }
    }

    @Override
    public void closeConnection() {
        if (this.socket == null) return; // Already closed

        if (this.listenLoop.isAlive()) {
            this.listenLoop.interrupt();
            this.listenLoop = null;
        }

        if (this.heartbeatThread.isAlive()) {
            this.heartbeatThread.interrupt();
            this.heartbeatThread = null;
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

        System.out.println("\nClosing connection...");
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
            System.err.println("[CLIENT SOCKET] Error while sending message: " + e.getMessage());
            // Everything should be closed
        }
    }

    public void receive(Message message) {
        try {
            message.execute(this);

            // Send update to Display Updater thread
            try {
                viewTui.getNetworkMessageQueue().put(message.getMessageType().name());
            } catch (InterruptedException e) {
                // Just ignore it
            }

        } catch (RuntimeException e) {
            System.err.println("[CLIENT SOCKET] Receive method has caught a RuntimeException: " + e.getMessage());
            // TODO view.handleError()
        }
    }

}