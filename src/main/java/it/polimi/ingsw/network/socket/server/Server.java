package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.exceptions.ServerException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class Server extends Thread {

    private static Server instance;

    private final ServerSocket serverSocket;
    private final HashMap<String, ClientHandler> connections;

    /**
     * Constructor
     *
     * @param port on which the server will listen
     * @throws ServerException if the port is invalid or the server cannot be started
     */
    public Server(int port) throws ServerException {
        if (port < 0 || port > 65535)
            throw new ServerException("Port is not valid");

        connections = new HashMap<>();

        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("[SERVER] Server started on port " + port);
        } catch (IOException e) {
            throw new ServerException("[SERVER] Server cannot be started: " + e.getMessage());
        }

        this.start();
    }

    /**
     * singleton instance of the server
     *
     * @return server instance
     * @throws ServerException if the server is not instantiated
     */
    public static Server getInstance() throws ServerException {
        if (instance != null) {
            return instance;
        }
        throw new ServerException("Server is not instantiated");
    }

    /**
     * Get or create the singleton instance of the server
     *
     * @param port the port on which the server will listen
     * @return the server instance
     * @throws ServerException if the server cannot be instantiated
     */
    public static Server getInstance(int port) throws ServerException {
        if (instance == null) {
            instance = new Server(port);
        }
        return instance;
    }

    public HashMap<String, ClientHandler> getConnections() {
        synchronized (this.connections) {
            return connections;
        }
    }

    /**
     * Opens a connection with a client
     *
     * @return the connection code
     * @throws ServerException if there's an error accepting the client connection
     */
    private String openConnection() throws ServerException {
        String connectionCode = UUID.randomUUID().toString();

        synchronized (this.connections) {
            if (this.connections.containsKey(connectionCode))
                throw new ServerException("Connection codes must be unique");
        }

        try {
            Socket socket = serverSocket.accept();
            socket.setSoTimeout(Constants.SOCKET_TIMEOUT);
            ClientHandler connection = new ClientHandler(connectionCode, socket);

            synchronized (this.connections) {
                if (this.connections.containsKey(connectionCode)) {
                    connection.close(); // Close the connection
                    throw new ServerException("[SERVER] Created a none unique connection");
                }
                this.connections.put(connectionCode, connection);
            }
            return connectionCode;
        } catch (IOException e) {
            throw new ServerException("[SERVER] Error accepting client connection: " + e.getMessage());
        }
    }

    public void closeConnection(String connectionCode) {
        synchronized (this.connections) {
            ClientHandler conn = this.connections.get(connectionCode);
            if (conn != null) {
                conn.close();
                this.connections.remove(connectionCode);
            }
        }
        System.out.println("[SERVER] Connection " + connectionCode + " closed");
    }

    /**
     * Sends an object to a client
     *
     * @param connectionCode the unique identifier for the connection
     * @param data           the object to send
     * @throws ServerException if the connection doesn't exist or there's an error sending the data
     */
    public void sendObject(String connectionCode, Object data) throws ServerException {
        ClientHandler conn;
        synchronized (this.connections) {
            conn = this.connections.get(connectionCode);
        }

        if (conn == null)
            throw new ServerException("[SERVER] Connection " + connectionCode + " not found");

        try {
            conn.sendObject(data);
        } catch (ServerException e) {
            this.closeConnection(connectionCode);
            throw e;
        }
    }

    /**
     * Receives an object from a client
     *
     * @param connectionCode the unique identifier for the connection
     * @return the received object
     * @throws ServerException if the connection doesn't exist or there's an error receiving the data
     */
    public Object receiveObject(String connectionCode) throws ServerException {
        ClientHandler conn;
        synchronized (this.connections) {
            conn = this.connections.get(connectionCode);
        }

        if (conn == null)
            throw new ServerException("[SERVER] Connection " + connectionCode + " not found");

        try {
            return conn.readObject();
        } catch (ServerException e) {
            this.closeConnection(connectionCode);
            throw e;
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {

            try {
                String connectionCode = this.openConnection();
                System.out.println("[SERVER] Connection " + connectionCode + " opened");
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
