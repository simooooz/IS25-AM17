package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.Server;
import it.polimi.ingsw.view.ClientController;

public class Main {

    public static void main(String[] args) {
        if (args[0].equals("client")) {
            System.out.println("Starting client...");
            ClientSocket client = new ClientSocket(Constants.DEFAULT_HOST, Constants.DEFAULT_SOCKET_PORT);

            ClientController controller = new ClientController(client);

        } else if (args[0].equals("server")) {
            System.out.println("Starting server...");
            try {
                Server.getInstance(Constants.DEFAULT_SOCKET_PORT);
            } catch (ServerException _) {
                System.exit(-1);
            }
        }
    }

}
