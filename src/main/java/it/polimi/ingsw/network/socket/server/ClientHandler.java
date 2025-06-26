package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.User;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Manages (server point of view) the connection between a client and the server via socket.
 * It encapsulates two-way communication of serialized objects,
 * connection monitoring via heartbeat, and asynchronous listening for incoming messages from the client.
 */
public class ClientHandler extends User {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private ListenLoop listenLoop;

    /**
     * Constructor
     *
     * @param connectionCode the unique identifier for this connection
     * @param socket         the socket for this connection
     * @throws IOException if there's an error setting up the connection
     */
    public ClientHandler(String connectionCode, Socket socket) throws IOException {
        super(connectionCode, null);
        this.socket = socket;
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
        this.listenLoop = new ListenLoop(this);
    }

    public void sendObject(Object data) throws ServerException {
        try {
            this.output.reset(); // Use reset otherwise it sends a previous instance of the objects
            this.output.writeObject(data);
            this.output.flush();
        } catch (IOException e) {
            SocketServer.getInstance().closeConnection(connectionCode);
        }
    }

    public Object readObject() throws ServerException {
        try {
            return input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            SocketServer.getInstance().closeConnection(connectionCode);
            throw new ServerException("[CLIENT CONNECTION] Object is null or could not be read");
        }
    }

    @Override
    public void sendEvent(GameEvent event) {
        Message message = Constants.createMessage(event.eventType(), event.getArgs());

        try {
            sendObject(message);
        } catch (ServerException e) {
            // Everything should be closed
        }
    }

    public void receive(Message message) {
        try {
            message.execute(this);
        } catch (RuntimeException e) {
            // System.err.println("[CLIENT HANDLER] Receive method has caught a RuntimeException: " + e.getMessage());
            try {
                Message errorMessage = Constants.createMessage(MessageType.ERROR, e.getMessage());
                this.sendObject(errorMessage);
            } catch (ServerException e1) {
                // Everything should be closed
            }
        }
    }

    @SuppressWarnings("Duplicates")
    public void close() {
        if (this.socket == null) return; // Already closed

        if (this.listenLoop.isAlive()) {
            this.listenLoop.interrupt();
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
    }

}
