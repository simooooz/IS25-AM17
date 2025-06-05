package it.polimi.ingsw;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.discovery.DiscoveryServer;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.rmi.RMIClient;
import it.polimi.ingsw.network.rmi.RMIServer;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.Server;
import it.polimi.ingsw.view.GUI.JavaFxInterface;
import it.polimi.ingsw.view.TUI.InputUtility;
import javafx.application.Application;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args[0].equals("client")) {

            System.out.println("Using client...");
            int clientType = InputUtility.requestInt("Press 1 to choose socket client or 2 for RMI: ", false, 1, 2);

            if (clientType == 1) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("gui")) {
                        // Avvia GUI
                        Application.launch(JavaFxInterface.class, args);
                        return;
                    } else if (args[0].equalsIgnoreCase("tui")) {
                        // Avvia TUI
                        new ClientSocket(); // Costruttore originale che avvia la TUI
                        return;
                    }
                }

                // Altrimenti chiedi all'utente
                Scanner scanner = new Scanner(System.in);
                System.out.println("Scegli l'interfaccia:");
                System.out.println("1. TUI (Text User Interface)");
                System.out.println("2. GUI (Graphical User Interface)");
                System.out.print("Scelta (1 o 2): ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        System.out.println("Avvio TUI...");
                        new ClientSocket(); // Costruttore originale
                        break;
                    case "2":
                        System.out.println("Avvio GUI...");
                        Application.launch(JavaFxInterface.class, args);
                        break;
                    default:
                        System.out.println("Scelta non valida. Avvio TUI per default...");
                        new ClientSocket(); // Costruttore originale
                        break;
                }
            }
            else if (clientType == 2)
                new RMIClient();
            else
                System.exit(-1);
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
