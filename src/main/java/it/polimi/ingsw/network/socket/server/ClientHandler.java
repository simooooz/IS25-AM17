package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.network.User;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Server-side socket connection handler.
 * Manages bidirectional communication with socket clients and message processing.
 */
public class ClientHandler extends User {

    /**
     * TCP socket connection to the client
     */
    private Socket socket;

    /**
     * Output stream for sending serialized objects to the client
     */
    private ObjectOutputStream output;

    /**
     * Input stream for receiving serialized objects from the client
     */
    private ObjectInputStream input;

    /** Thread for listening to client messages */
    private ListenLoop listenLoop;

    /**
     * @param connectionCode unique connection identifier
     * @param socket client socket connection
     * @throws IOException if connection setup fails
     */
    public ClientHandler(String connectionCode, Socket socket) throws IOException {
        super(connectionCode, null);
        this.socket = socket;
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
        this.listenLoop = new ListenLoop(this);
    }

    /**
     * Sends a serialized object to a client with connection cleanup on error.
     *
     * @param data object to send
     * @throws ServerException if sending fails
     */
    public void sendObject(Object data) throws ServerException {
        try {
            this.output.reset(); // Use reset otherwise it sends a previous instance of the objects
            this.output.writeObject(data);
            this.output.flush();
        } catch (IOException e) {
            System.out.println("[CLIENT HANDLER - sendObject] " + System.currentTimeMillis());
            e.printStackTrace();
            SocketServer.getInstance().closeConnection(connectionCode);
        }
    }

    /**
     * Reads a serialized object from a client with connection cleanup on error.
     *
     * @return received object
     * @throws ServerException if reading fails or object is null
     */
    public Object readObject() throws ServerException {
        try {
            return input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[CLIENT HANDLER - readObject] " + System.currentTimeMillis());
            e.printStackTrace();
            SocketServer.getInstance().closeConnection(connectionCode);
            throw new ServerException("[CLIENT CONNECTION] Object is null or could not be read");
        }
    }

    /**
     * Sends game event to a client as a message.
     *
     * @param event game event to send
     */
    @Override
    public void sendEvent(Event event) {
        Message message = Constants.createMessage(event.eventType(), event.getArgs());

        try {
            sendObject(message);
        } catch (ServerException e) {
            // Everything should be closed
        }
    }

    /**
     * Processes received a message with error handling.
     *
     * @param message received message to process
     */
    public void receive(Message message) {
        try {
            message.execute(this);
        } catch (RuntimeException e) {
            System.err.println("[CLIENT HANDLER] Receive method has caught a RuntimeException: " + e.getMessage());
            e.printStackTrace();
            try {
                Message errorMessage = Constants.createMessage(MessageType.ERROR, e.getMessage());
                this.sendObject(errorMessage);
            } catch (ServerException e1) {
                // Everything should be closed
            }
        }
    }

    /**
     * Closes connection and cleans up all resources.
     */
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
