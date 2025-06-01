package it.polimi.ingsw.network.discovery;

import it.polimi.ingsw.Constants;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class DiscoveryClient {

    public static ServerInfo findServer() {

        ServerInfo serverInfo = null;

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.setSoTimeout(Constants.DISCOVERY_TIMEOUT);

            byte[] messageData = Constants.DISCOVERY_MESSAGE.getBytes();
            List<InetAddress> broadcastAddresses = getBroadcastAddresses();

            for (InetAddress broadcastAddress : broadcastAddresses) {
                DatagramPacket packet = new DatagramPacket(
                        messageData, messageData.length,
                        broadcastAddress, Constants.DISCOVERY_PORT
                );
                socket.send(packet);
            }

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < Constants.DISCOVERY_TIMEOUT) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(responsePacket);

                    String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    if (response.startsWith(Constants.DISCOVERY_RESPONSE)) { // Response will be GALAXY_TRUCKER_SERVER_FOUND:IP:SOCKET_PORT:RMI_PORT
                        String[] parts = response.split(":");
                        if (parts.length == 4) {
                            String ip = parts[1];
                            int socketPort = Integer.parseInt(parts[2]);
                            int rmiPort = Integer.parseInt(parts[3]);

                            serverInfo = new ServerInfo(ip, socketPort, rmiPort);
                            break;
                        }
                    }
                } catch (SocketTimeoutException e) {
                    // Timeout exception, just go ahead
                    break;
                }
            }

            socket.close();
        } catch (IOException e) {
            // Do nothing, serverInfo will be returned as null
        }

        return serverInfo;
    }

    private static List<InetAddress> getBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp())
                continue;

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast != null)
                    broadcastList.add(broadcast);
            }
        }

        return broadcastList;
    }

}
