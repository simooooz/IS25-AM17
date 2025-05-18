package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.rmi.RMIClient;
import it.polimi.ingsw.network.rmi.RMIServer;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.Server;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args[0].equals("client")) {

            System.out.println("Using client...");
            int clientType = InputUtility.requestInt("Press 1 to choose socket client or 2 for RMI: ", false, 1, 2);

            if (clientType == 1)
                new ClientSocket(Constants.DEFAULT_HOST, Constants.DEFAULT_SOCKET_PORT);
            else if (clientType == 2)
                new RMIClient(Constants.DEFAULT_HOST, Constants.DEFAULT_RMI_PORT);
            else
                System.exit(-1);

        } else if (args[0].equals("server")) {
            System.out.println("Starting server...");
            try {
                Server.getInstance(Constants.DEFAULT_SOCKET_PORT);
                RMIServer.getInstance(Constants.DEFAULT_RMI_PORT);
            } catch (ServerException _) {
                System.exit(-1);
            }
        }
    }

}
