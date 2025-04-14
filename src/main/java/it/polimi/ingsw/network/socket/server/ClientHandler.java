package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.network.exceptions.ServerException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/**
 * Manages (server point of view) the connection between a client and the server via socket.
 * It encapsulates two-way communication of serialized objects,
 * connection monitoring via heartbeat, and asynchronous listening for incoming messages from the client.
 */
public class ClientHandler {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private final RefToUser user;

    private ListenLoop listenLoop;

    /**
     * Constructor
     *
     * @param connectionCode the unique identifier for this connection
     * @param socket         the socket for this connection
     * @throws IOException if there's an error setting up the connection
     */
    public ClientHandler(String connectionCode, Socket socket) throws IOException {
        this.socket = socket;
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
        this.user = new RefToUser(connectionCode);
        this.listenLoop = new ListenLoop(connectionCode, user);
    }

    public void send(Object data, CompletableFuture<Void> completion) throws ServerException {
        try {
            this.output.writeObject(data);
            this.output.flush();
            completion.complete(null);
        } catch (IOException e) {
            throw new ServerException("[CLIENT CONNECTION] Error while sending object");
        }
    }

    public Object read() throws ServerException {
        try {
            Object obj = input.readObject();
            if (obj == null)
                throw new ServerException();
            return obj;
        } catch (IOException | ClassNotFoundException | ServerException e) {
            throw new ServerException("[CLIENT CONNECTION] Object is null or could not be read");
        }
    }

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
