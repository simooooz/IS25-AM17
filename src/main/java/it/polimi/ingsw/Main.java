package it.polimi.ingsw;

import it.polimi.ingsw.network.discovery.DiscoveryServer;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.rmi.RMIServer;
import it.polimi.ingsw.network.socket.server.SocketServer;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.TUI.InputUtility;
import it.polimi.ingsw.view.TUI.ViewTui;

import java.net.SocketException;

/**
 * Main class for the Galaxy Trucker application.
 * This class serves as the entry point for both client and server modes of the application.
 *
 * <p>The application can be launched in two modes:
 * <ul>
 * <li><strong>Client mode:</strong> Provides options to run either a Text User Interface (TUI)
 *     or Graphical User Interface (GUI) client</li>
 * <li><strong>Server mode:</strong> Starts all server components including Socket server,
 *     RMI server, and Discovery server</li>
 * </ul>
 */
public class Main {

    /**
     * Main entry point of the application.
     *
     */
    public static void main(String[] args) {
        if (args[0].equals("client")) {

            System.out.println("Starting client...");
            int networkType = InputUtility.requestInt("Press 1 to choose SOCKET or 2 for RMI: ", false, 1, 2);

            String ip;
            do {
                ip = InputUtility.requestString("Insert server ip address or skip if you want to use discovery: ", false, 0, 20);
            } while (!ip.isBlank() && !Constants.isValidIPv4(ip));

            int clientType = InputUtility.requestInt("Press 1 to choose TUI or 2 for GUI: ", false, 1, 2);

            if (clientType == 1)
                new ViewTui().start(networkType, ip);
            else if (clientType == 2)
                new App().start(networkType, ip);

        } else if (args[0].equals("server")) {

            System.out.println("Starting server...");
            try {
                SocketServer.getInstance(Constants.DEFAULT_SOCKET_PORT);
                RMIServer.getInstance(Constants.DEFAULT_RMI_PORT);
                DiscoveryServer.getInstance();

                try {
                    String address = Constants.getIPv4Address();
                    System.out.println("[ALL] Server IPv4 address: " + address);
                } catch (SocketException e) {
                    System.err.println("Unable to found IPv4 address...");
                }

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        SocketServer.getInstance().stop();
                        RMIServer.getInstance().stop();
                        DiscoveryServer.getInstance().stop();
                    } catch (ServerException e) {
                        // Ignore it
                    }
                }));

            } catch (ServerException _) {
                System.exit(-1);
            }
        }
    }

}