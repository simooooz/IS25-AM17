package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.network.exceptions.ServerException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class Server extends Thread {

    private static Server instance;
    private final ServerSocket serverSocket;
    private final HashMap<String, ClientConnection> connections;
    private final int port;

    public Server(int port) throws ServerException {
        if (port < 0 || port > 65353)
            throw new ServerException("Port is not valid");

        this.port = port;
        connections = new HashMap<>();

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new ServerException("Server cannot be started");
        }
    }

    public static Server getInstance() throws ServerException {
        if (instance != null) {
            return instance;
        }
        throw new ServerException("Server is not instantiated");
    }

    public static Server getInstance(int port) throws ServerException {
        if (instance == null) {
            instance = new Server(port);
        }
        return instance;
    }

    private String openConnection() throws ServerException {
        String connectionCode = UUID.randomUUID().toString();

        synchronized (this.connections) {
            if (this.connections.containsKey(connectionCode))
                throw new ServerException("Connection codes must be unique");
        }

        // TODO forse mi devo sincronizzare a serverSocket
        try {
            Socket socket = serverSocket.accept();
            // TODO capiamo socket.setSoTimeout();
            ClientConnection clientConnection = new ClientConnection(connectionCode, socket);

            synchronized (this.connections) {
                if (this.connections.containsKey(connectionCode)) {
                    clientConnection.close(); // Close the connection
                    throw new ServerException("[SERVER] Created a none unique connection");
                }
                this.connections.put(connectionCode, clientConnection);
            }

        } catch (IOException e) {
            throw new ServerException("[SERVER] Error accepting client connection: " + e.getMessage());
        }

        return connectionCode;
    }

    public void closeConnection(String connectionCode) {
        synchronized (this.connections) {
            ClientConnection conn = this.connections.get(connectionCode);
            if (conn != null) {
                conn.close();
                this.connections.remove(connectionCode);
            }
        }
    }

    public void sendObject(String connectionCode, Object data) throws ServerException {
        ClientConnection conn;
        synchronized (this.connections) {
            conn = this.connections.get(connectionCode);
        }

        if (conn == null)
            throw new ServerException("[SERVER] ClientConnection not found for this code: " + connectionCode);

        try {
            conn.sendObject(data);
        } catch (ServerException e) {
            this.closeConnection(connectionCode);
            throw e;
        }
    }

    public Object receiveObject(String connectionCode) throws ServerException {
        ClientConnection conn;
        synchronized (this.connections) {
            conn = this.connections.get(connectionCode);
        }

        if (conn == null)
            throw new ServerException("[SERVER] ClientConnection not found for this code: " + connectionCode);

        try {
            return conn.readObject();
        } catch (ServerException e) {
            this.closeConnection(connectionCode);
            throw e;
        }
    }

    // TODO check if username is taken method?

    @Override
    public void run() {
        while (isAlive()) {

            try {
                String connectionCode = this.openConnection();
            } catch (ServerException e) {
                System.err.println("[SERVER] Error while opening a new connection: " + e.getMessage());
            }

        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("[SERVER] Error while closing server");
        }
    }


}
