package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.ServerBasis;
import it.polimi.ingsw.network.User;
import it.polimi.ingsw.network.exceptions.ServerException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A server implementation utilizing sockets to handle client connections.
 * This class extends {@code ServerBasis} and implements {@code Runnable}.
 * It manages client connections and operations such as accepting and closing connections,
 * and periodically checks for inactive clients.
 */
public class SocketServer extends ServerBasis implements Runnable {

    private static SocketServer instance;

    private final ServerSocket serverSocket;
    private final HashMap<String, ClientHandler> connections;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * @param port on which the server will listen
     * @throws ServerException if the port is invalid or the server cannot be started
     */
    public SocketServer(int port) throws ServerException {
        if (port < 0 || port > 65535)
            throw new ServerException("[SOCKET SERVER] Port is not valid");

        connections = new HashMap<>();

        try {
            this.serverSocket = new ServerSocket(port);
            scheduler.scheduleAtFixedRate(this::checkActiveClients, Constants.SERVER_CHECK_INTERVAL, Constants.SERVER_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
            System.out.println("[SOCKET SERVER] Server started on port " + port);
        } catch (IOException e) {
            throw new ServerException("[SOCKET SERVER] Server cannot be started: " + e.getMessage());
        }

        new Thread(this).start();
    }

    /**
     * Singleton instance of the server
     *
     * @return server instance
     * @throws ServerException if the server is not instantiated
     */
    public static SocketServer getInstance() throws ServerException {
        if (instance != null) {
            return instance;
        }
        throw new ServerException("[SOCKET SERVER] Server is not instantiated");
    }

    public static SocketServer getInstance(int port) throws ServerException {
        if (instance == null) {
            instance = new SocketServer(port);
        }
        return instance;
    }

    /**
     * Opens a connection with a client
     *
     * @throws ServerException if there's an error accepting the client connection
     */
    private void openConnection() throws ServerException {
        String connectionCode = UUID.randomUUID().toString();

        synchronized (this.connections) {
            if (this.connections.containsKey(connectionCode))
                throw new ServerException("[SOCKET SERVER] Connection codes must be unique");
        }

        try {
            Socket socket = serverSocket.accept();
            socket.setSoTimeout(Constants.NETWORK_TIMEOUT);
            socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);
            ClientHandler connection = new ClientHandler(connectionCode, socket);

            synchronized (this.connections) {
                if (this.connections.containsKey(connectionCode)) {
                    connection.close(); // Close the connection
                    throw new ServerException("[SOCKET SERVER] Created a none unique connection");
                }
                this.connections.put(connectionCode, connection);
            }
        } catch (IOException e) {
            throw new ServerException("[SOCKET SERVER] Error accepting client connection: " + e.getMessage());
        }
    }

    public void closeConnection(String connectionCode) {
        synchronized (this.connections) {
            ClientHandler conn = this.connections.get(connectionCode);
            if (conn != null) {
                User.removeUser(conn);
                conn.close();
                this.connections.remove(connectionCode);
            }
        }
    }

    private void checkActiveClients() {
        long now = System.currentTimeMillis();
        Map<String, ClientHandler> connectionsCopy;

        synchronized (connections) {
            connectionsCopy = new HashMap<>(connections);
        }

        for (Map.Entry<String, ClientHandler> entry : connectionsCopy.entrySet()) {
            ClientHandler clientHandler = entry.getValue();
            String connectionCode = entry.getKey();
            if (now - clientHandler.getLastPing() > Constants.NETWORK_TIMEOUT)
                closeConnection(connectionCode);
        }

    }


    /**
     * Stops the server and releases all resources.
     */
    public void stop() {
        scheduler.shutdownNow();
        Thread.currentThread().interrupt();

        synchronized (this.connections) {
            for (String connectionCode : this.connections.keySet()) {
                ClientHandler conn = this.connections.get(connectionCode);
                if (conn != null)
                    conn.close();
            }
            this.connections.clear();
        }

        try {
            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();
        } catch (IOException e) {
            // Error while closing server socket, ignore it
        }
    }

    /**
     * Continuously runs the main server loop, accepting incoming client connections
     * until the thread is interrupted.
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {

            try {
                this.openConnection();
            } catch (ServerException e) {
                // Error while opening a new connection
                // Ignore it
            }

        }

        stop();
    }

}
