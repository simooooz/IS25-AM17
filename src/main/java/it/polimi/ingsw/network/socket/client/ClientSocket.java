package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.discovery.DiscoveryClient;
import it.polimi.ingsw.network.discovery.ServerInfo;
import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.UserInterface;

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

    public ClientSocket(UserInterface ui) {
        super(ui);
        this.connect();

        heartbeatThread = new HeartbeatThread(this, Constants.HEARTBEAT_INTERVAL);
        heartbeatThread.start();

        listenLoop = new ListenLoopOfClient(this);
    }

    private void connect() {
        for (int attempt = 1; attempt <= Constants.MAX_RETRIES; attempt++) {

            try {
                ServerInfo serverInfo = DiscoveryClient.findServer();
                if (serverInfo == null) throw new ClientException();

                this.socket = new Socket(serverInfo.ipAddress, serverInfo.socketPort);
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
                this.socket.setSoTimeout(Constants.SOCKET_TIMEOUT);
                break;

            } catch (ClientException | IOException e) {
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

        }
    }

    @Override
    public void closeConnection() {
        if (this.socket == null) return; // Already closed

        if (this.listenLoop != null && this.listenLoop.isAlive()) {
            this.listenLoop.interrupt();
            this.listenLoop = null;
        }

        if (this.heartbeatThread != null && this.heartbeatThread.isAlive()) {
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
        } catch (RuntimeException e) {
            ui.displayError(e.getMessage());
        }
    }

}