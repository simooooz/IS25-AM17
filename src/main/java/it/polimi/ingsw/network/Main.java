package it.polimi.ingsw.network;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.SingleArgMessage;
import it.polimi.ingsw.network.messages.lobby.CreateLobbyMessage;
import it.polimi.ingsw.network.messages.lobby.JoinLobbyMessage;
import it.polimi.ingsw.network.messages.net.DisconnectMessage;
import it.polimi.ingsw.network.socket.client.ServerHandler;
import it.polimi.ingsw.network.socket.server.Server;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        if (args[0].equals("client")) {

            System.out.println("Using Client...");
            ServerHandler client = new ServerHandler(Constants.DEFAULT_HOST, Constants.DEFAULT_PORT);

            Scanner scanner = new Scanner(System.in);
            String input;

            while (true) {
                input = scanner.nextLine();
                String[] params = input.split(" ");

                Message message;
                switch (params[0]) {
                    case "set-user":
                        message = new SingleArgMessage<>(MessageType.SET_USERNAME, params[1]);
                        break;
                    case "create-lobby":
                        message = new CreateLobbyMessage(
                                params[1],
                                Integer.parseInt(params[2]),
                                Integer.parseInt(params[3])
                        );
                        break;
                    case "join-lobby":
                        message = new JoinLobbyMessage(params[1]);
                        break;
                    case "exit":
                        message = new DisconnectMessage();
                        break;
                    default:
                        continue;
                }

                client.getUser().send(message);
            }

        } else if (args[0].equals("server")) {

            System.out.println("Starting server...");
            try {
                Server.getInstance(Constants.DEFAULT_PORT);
            } catch (ServerException _) {
                System.exit(-1);
            }

        }

    }

}
