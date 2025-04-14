package it.polimi.ingsw;

import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.SingleArgMessage;
import it.polimi.ingsw.network.messages.lobby.CreateLobbyMessage;
import it.polimi.ingsw.network.messages.lobby.JoinLobbyMessage;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.Server;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        if (args[0].equals("client")) {

            System.out.println("Using Client...");
            ClientSocket client = new ClientSocket(Constants.DEFAULT_HOST, Constants.DEFAULT_SOCKET_PORT);

            Scanner scanner = new Scanner(System.in);
            String input;
            boolean usernameSet = false;  // Flag to track if username has been set

            System.out.println("Please set your username using 'set-user <username>' before using other commands.");

            while (true) {
                input = scanner.nextLine();
                String[] params = input.split(" ");

                // Check if command is set-user or not
                if (params[0].equals("set-user") && params.length > 1) {
                    Message message = new SingleArgMessage<>(MessageType.SET_USERNAME, params[1]);
                    client.getUser().send(message);
                    usernameSet = true;  // Mark username as set
                    System.out.println("Username set successfully. You can now use other commands.");
                    continue;
                }

                // If username is not set and command is not set-user, deny access
                if (!usernameSet) {
                    System.out.println("You must set your username first using 'set-user <username>'");
                    continue;
                }

                // Username is set, process other commands
                Message message;
                switch (params[0]) {
                    case "create-lobby":
                        if (params.length >= 4) {
                            message = new CreateLobbyMessage(
                                    params[1],
                                    Integer.parseInt(params[2]),
                                    Boolean.getBoolean(params[3])
                            );
                            client.getUser().send(message);
                        } else {
                            System.out.println("Invalid format. Use: create-lobby <name> <maxPlayers> <timeout>");
                        }
                        break;
                    case "join-lobby":
                        if (params.length >= 2) {
                            message = new JoinLobbyMessage(params[1]);
                            client.getUser().send(message);
                        } else {
                            System.out.println("Invalid format. Use: join-lobby <lobbyName>");
                        }
                        break;
                    case "exit":
                        System.exit(0);
                        break;// Exit the program
                    default:
                        System.out.println("Unknown command. Available commands: create-lobby, join-lobby, exit");
                        break;
                }
            }

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
