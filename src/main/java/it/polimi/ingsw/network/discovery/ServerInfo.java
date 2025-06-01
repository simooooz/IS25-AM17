package it.polimi.ingsw.network.discovery;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerInfo {

    public String ipAddress;
    public int socketPort;
    public int rmiPort;

    public ServerInfo(String ip, int socketPort, int rmiPort) {
        this.ipAddress = ip;
        this.socketPort = socketPort;
        this.rmiPort = rmiPort;
    }

    public static String getLocalIPAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp())
                continue;

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address && !addr.isLoopbackAddress())
                    return addr.getHostAddress();
            }
        }
        return "localhost";
    }

}
