package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.exceptions.ServerException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {

    private static Server instance;

    private final ServerSocket listenSocket;
    private final ConcurrentHashMap<String, ClientHandler> connections;

    /**
     * Constructor
     *
     * @param port on which the server will listen
     * @throws ServerException if the port is invalid or the server cannot be started
     */
    public Server(int port) throws ServerException {
        if (port < 0 || port > 65535)
            throw new ServerException("Port is not valid");

        connections = new ConcurrentHashMap<>();

        try {
            this.listenSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            throw new ServerException("Server cannot be started " + e.getMessage());
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

    /**
     * Opens a connection with a client
     *
     * @return the connection code
     * @throws ServerException if there's an error accepting the client connection
     */
    private String openConnection() throws ServerException {
        try {

            Socket socket = listenSocket.accept();
            socket.setSoTimeout(Constants.SOCKET_TIMEOUT);

            String connectionCode;
            synchronized (this.connections) {
                do {
                    connectionCode = UUID.randomUUID().toString();
                } while (this.connections.containsKey(connectionCode));

                ClientHandler connection = new ClientHandler(connectionCode, socket);
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
    public void send(String connectionCode, Object data, CompletableFuture<Void> completion) throws ServerException {
        ClientHandler conn;
        synchronized (this.connections) {
            conn = this.connections.get(connectionCode);
        }

        if (conn == null)
            throw new ServerException("[SERVER] connection " + connectionCode + " not found");

        try {
            conn.send(data, completion);
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
    public Object receive(String connectionCode) throws ServerException {
        ClientHandler conn;
        synchronized (this.connections) {
            conn = this.connections.get(connectionCode);
        }

        if (conn == null)
            throw new ServerException("[SERVER] connection " + connectionCode + " not found");

        try {
            return conn.read();
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
            listenSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("[SERVER] Error while closing server");
        }
    }

}
