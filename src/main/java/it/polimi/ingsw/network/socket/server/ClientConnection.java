package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.network.exceptions.ServerException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final User user;
    private ListenLoop listenLoop;

    public ClientConnection(String connectionCode, Socket socket) throws IOException {
        this.socket = socket;
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
        this.user = new User(connectionCode);
        this.listenLoop = new ListenLoop(connectionCode, user);
        listenLoop.start();
    }

    public void sendObject(Object data) throws ServerException {
        try {
            this.output.writeObject(data);
            this.output.flush();
        } catch (IOException e) {
            throw new ServerException("[CLIENT CONNECTION] Error while sending object");
        }
    }

    public Object readObject() throws ServerException {
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
        } catch (IOException _) {}
        finally {
            this.input = null;
        }

        try {
            this.output.close();
        } catch (IOException _) {}
        finally {
            this.output = null;
        }

        try {
            this.socket.close();
        } catch (IOException _) {}
        finally {
            this.socket = null;
        }
    }

}
