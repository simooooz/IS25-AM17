package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.ServerBasis;
import it.polimi.ingsw.network.User;
import it.polimi.ingsw.network.exceptions.ServerException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class Server extends ServerBasis implements Runnable {

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
            throw new ServerException("[SOCKET SERVER] Port is not valid");

        connections = new HashMap<>();

        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("[SOCKET SERVER] Server started on port " + port);
        } catch (IOException e) {
            throw new ServerException("[SOCKET SERVER] Server cannot be started: " + e.getMessage());
        }

        new Thread(this).start();
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
        throw new ServerException("[SOCKET SERVER] Server is not instantiated");
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
        String connectionCode = UUID.randomUUID().toString();

        synchronized (this.connections) {
            if (this.connections.containsKey(connectionCode))
                throw new ServerException("[SOCKET SERVER] Connection codes must be unique");
        }

        try {
            Socket socket = serverSocket.accept();
            socket.setSoTimeout(Constants.SOCKET_TIMEOUT);
            ClientHandler connection = new ClientHandler(connectionCode, socket);

            synchronized (this.connections) {
                if (this.connections.containsKey(connectionCode)) {
                    connection.close(); // Close the connection
                    throw new ServerException("[SOCKET SERVER] Created a none unique connection");
                }
                this.connections.put(connectionCode, connection);
            }
            return connectionCode;
        } catch (IOException e) {
            throw new ServerException("[SOCKET SERVER] Error accepting client connection: " + e.getMessage());
        }
    }

    public void closeConnection(String connectionCode) {
        synchronized (this.connections) {
            ClientHandler conn = this.connections.get(connectionCode);
            if (conn != null) {
                User.removeUser(conn); // TODO da cambiare e mettere inattivo
                conn.close();
                this.connections.remove(connectionCode);
            }
        }
        System.out.println("[SOCKET SERVER] Connection " + connectionCode + " closed");
    }

    public void stop() {
        Thread.currentThread().interrupt();

        // Close all active client connections
        synchronized (this.connections) {
            for (String connectionCode : this.connections.keySet()) {
                ClientHandler conn = this.connections.get(connectionCode);
                if (conn != null) {
                    User.removeUser(conn); // TODO da cambiare e mettere inattivo
                    conn.close();
                    System.out.println("[SOCKET SERVER] Connection " + connectionCode + " closed");
                }
            }
            this.connections.clear();
        }

        // Close the server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("[SOCKET SERVER] Server socket closed");
            }
        } catch (IOException e) {
            System.err.println("[SOCKET SERVER] Error while closing server socket: " + e.getMessage());
        }

        // Reset the singleton instance
        instance = null;

        System.out.println("[SOCKET SERVER] Server shutdown completed");
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {

            try {
                String connectionCode = this.openConnection();
                System.out.println("[SOCKET SERVER] Connection " + connectionCode + " opened");
            } catch (ServerException e) {
                System.err.println("[SOCKET SERVER] Error while opening a new connection: " + e.getMessage());
            }

        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("[SERVER] Error while closing server");
        }
    }

}
