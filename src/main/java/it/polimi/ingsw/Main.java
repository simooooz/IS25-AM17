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

            while (true) {
                input = scanner.nextLine();
                String[] params = input.split(" ");

                Message message = null;
                switch (params[0]) {
                    case "set-user":
                        message = new SingleArgMessage<>(MessageType.SET_USERNAME, params[1]);
                        break;
                    case "create-lobby":
                        message = new CreateLobbyMessage(
                                params[1],
                                Integer.parseInt(params[2]),
                                Boolean.getBoolean(params[3])
                        );
                        break;
                    case "join-lobby":
                        message = new JoinLobbyMessage(params[1]);
                        break;
                    case "exit":
                        System.exit(0);
                        break;
                    default:
                        continue;
                }

                client.getUser().send(message);
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
