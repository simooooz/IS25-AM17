package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocket {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private UserOfClient user;

    private HeartbeatThread heartbeatThread;
    private ListenLoopOfClient listenLoop;

    public ClientSocket() {
        this.connect("127.0.0.1", 0);

        this.user = new UserOfClient(this);

        // TODO heartbeat interval
        heartbeatThread = new HeartbeatThread(this, 0);
        heartbeatThread.start();

        listenLoop = new ListenLoopOfClient(this, user);
        listenLoop.start();
    }

    public void connect(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            // TODO capiamo forse system exit dopo un po' di retry
        }
    }

    public void close() {
        if (this.listenLoop.isAlive()) {
            this.listenLoop.interrupt();
        }
        this.listenLoop = null;

        if (this.heartbeatThread.isAlive()) {
            this.heartbeatThread.interrupt();
        }
        this.heartbeatThread = null;

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

        this.user = null;
    }

    public Object readObject() throws ClientException {
        try {
            Object obj = this.input.readObject();
            if (obj == null)
                throw new ClientException("Empty object");
            return obj;
        } catch (IOException | ClassNotFoundException | ClientException e) {
            this.close();
            throw new ClientException("[CLIENT SOCKET] Object is null or could not be read");
        }
    }

    public void sendObject(Object data) throws ClientException {
        try {
            this.output.writeObject(data);
            this.output.flush();
        } catch (IOException e) {
            this.close();
            throw new ClientException("[CLIENT SOCKET] Error while writing object");
        }
    }

}
