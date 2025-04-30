package it.polimi.ingsw.TUI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.Server;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify 'client' or 'server' as argument");
            return;
        }

        if (args[0].equals("client")) {
            System.out.println("Starting client...");
            ClientSocket client = new ClientSocket(Constants.DEFAULT_HOST, Constants.DEFAULT_SOCKET_PORT);

            // Creare e avviare la TUI
            ViewTui tui = new ViewTui(client);
            tui.start();

        } else if (args[0].equals("server")) {
            System.out.println("Starting server...");
            try {
                Server.getInstance(Constants.DEFAULT_SOCKET_PORT);
            } catch (ServerException _) {
                System.exit(-1);
            }
        } else {
            System.out.println("Invalid argument. Use 'client' or 'server'");
        }
    }
}