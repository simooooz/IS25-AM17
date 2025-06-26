package it.polimi.ingsw.network.discovery;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.exceptions.ServerException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class DiscoveryServer implements Runnable {

    private static DiscoveryServer instance;
    private final DatagramSocket socket;

    private final Thread serverThread;
    private volatile boolean running = true;

    public DiscoveryServer() throws ServerException {
        try {
            socket = new DatagramSocket(Constants.DISCOVERY_PORT);
            socket.setBroadcast(true);
            socket.setSoTimeout(1000);
            System.out.println("[DISCOVERY SERVER] Server started on port " + Constants.DISCOVERY_PORT);
        } catch (SocketException e) {
            throw new ServerException("Discovery server cannot be started");
        }

        serverThread = new Thread(this, "DiscoveryServer-Thread");
        serverThread.start();
    }

    public static DiscoveryServer getInstance() throws ServerException {
        if (instance == null) {
            instance = new DiscoveryServer();
        }
        return instance;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                if (Constants.DISCOVERY_MESSAGE.equals(message)) {
                    DatagramPacket responsePacket = getDatagramPacket(packet);
                    socket.send(responsePacket);
                }
            } catch (SocketTimeoutException e) {
                // Ignore it
            } catch (IOException e) {
                // IOException error, ignore it
            }
        }
    }

    private static DatagramPacket getDatagramPacket(DatagramPacket packet) throws SocketException {
        String localIP = ServerInfo.getLocalIPAddress();
        String response = Constants.DISCOVERY_RESPONSE + ":" + localIP + ":" +
                Constants.DEFAULT_SOCKET_PORT + ":" + Constants.DEFAULT_RMI_PORT;

        byte[] responseData = response.getBytes();
        return new DatagramPacket(
                responseData, responseData.length,
                packet.getAddress(), packet.getPort()
        );
    }

    public void stop() {
        running = false;

        if (socket != null && !socket.isClosed())
            socket.close();
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
            try {
                serverThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        instance = null;
    }

}