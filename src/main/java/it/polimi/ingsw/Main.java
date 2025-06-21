package it.polimi.ingsw;

import it.polimi.ingsw.network.discovery.DiscoveryServer;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.rmi.RMIServer;
import it.polimi.ingsw.network.socket.server.Server;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.TUI.InputUtility;
import it.polimi.ingsw.view.TUI.ViewTui;

public class Main {

    public static void main(String[] args) {
        if (args[0].equals("client")) {

            System.out.println("Using client...");
            int clientType = InputUtility.requestInt("Press 1 to choose TUI or 2 for GUI: ", false, 1, 2);

            if (clientType == 1)
                new ViewTui().start();
            else if (clientType == 2)
                new App().start();

        } else if (args[0].equals("server")) {
            System.out.println("Starting server...");
            try {
                Server.getInstance(Constants.DEFAULT_SOCKET_PORT);
                RMIServer.getInstance(Constants.DEFAULT_RMI_PORT);
                DiscoveryServer.getInstance();

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        Server.getInstance().stop();
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
