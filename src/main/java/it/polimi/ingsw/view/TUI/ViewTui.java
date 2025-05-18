package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.TUI.graphics.StandardShipBoardTUI;
import it.polimi.ingsw.view.TUI.graphics.LearnerFlightShipBoardTUI;

import java.util.Scanner;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ViewTui {

    private final Client client;
    private final Scanner scanner;

    private final DisplayUpdater displayUpdater;
    private final BlockingQueue<String> networkMessageQueue = new LinkedBlockingQueue<>();


    // Constants for board dimensions
    private static final int BOARD_ROWS = 5;
    private static final int BOARD_COLS = 7;

    /**
     * Constructs a new ViewTui with the given client controller.
     *
     * @param client the client that handles communication logic
     */
    public ViewTui(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.displayUpdater = new DisplayUpdater(this.client);

        // Initialize with a standard ship board by default
        // Will be replaced with the appropriate board when joining a lobby or game
        // this.shipBoard = new StandardShipBoardTUI(null, BOARD_ROWS, BOARD_COLS);
    }

    private void processUserInput(String input) {
        switch (client.getState()) {
            case USERNAME:
                if (input.length() < 3 || input.length() > 18) {}
                client.send(MessageType.SET_USERNAME, input);
                break;

            case LOBBY_SELECTION:
                switch (input) {
                    case "1" -> handleCreateLobby();
                    case "2" -> handleJoinLobby();
                    case "3" -> handleJoinRandomLobby();
                    case "4" -> handleDisconnect();
                    default -> Chroma.println("Option not valid. Please try again.", Chroma.RED);
                }
                break;

            case IN_LOBBY:
                handleInLobby(input);
                break;

            case IN_GAME:
                handleInGame(input);
                break;
        }
    }

    /**
     * Handles the lobby creation process.
     */
    private void handleCreateLobby() {
        clear();
        Chroma.println("CREATE LOBBY (press q to go back to main menù)", Chroma.WHITE_BOLD);

        String lobbyName = InputUtility.requestString("Name of the lobby: ", true, 3, 18);
        if (lobbyName == null) {
            displayUpdater.updateDisplay();
            return;
        }
        Integer maxPlayers = InputUtility.requestInt("Number of players (2-4): ", true, 2, 4);
        if (maxPlayers == null) {
            displayUpdater.updateDisplay();
            return;
        }
        Boolean learnerFlight = InputUtility.requestBoolean("Learner flight? (true/false): ", true);
        if (learnerFlight == null) {
            displayUpdater.updateDisplay();
            return;
        }

        client.send(MessageType.CREATE_LOBBY, lobbyName, maxPlayers, learnerFlight);
    }

    /**
     * Handles the process of joining a specified existing lobby.
     */
    private void handleJoinLobby() {
        clear();
        Chroma.println("JOIN LOBBY (press q to go back to main menù)", Chroma.WHITE_BOLD);

        String lobbyName = InputUtility.requestString("Name of the lobby: ", true, 3, 18);
        if (lobbyName == null) {
            displayUpdater.updateDisplay();
            return;
        }

        client.send(MessageType.JOIN_LOBBY, lobbyName);
    }

    /**
     * Handles the process of joining a random lobby.
     */
    private void handleJoinRandomLobby() {
        clear();
        Chroma.println("JOIN RANDOM LOBBY (press q to go back to main menù)", Chroma.WHITE_BOLD);

        Boolean learnerFlight = InputUtility.requestBoolean("Learner flight? (true/false): ", true);
        if (learnerFlight == null) {
            displayUpdater.updateDisplay();
            return;
        }

        client.send(MessageType.JOIN_RANDOM_LOBBY, learnerFlight);
    }

    // utility to print a grid of components
    public static String gridOfComponents(List<Component> components, int componentsPerRow) {
        StringBuilder output = new StringBuilder();

        List<List<Component>> componentsRows = new ArrayList<>();
        for (int i = 0; i < components.size(); i += componentsPerRow) {
            int end_row = Math.min(i + componentsPerRow, components.size());
            componentsRows.add(components.subList(i, end_row));
        }

        for (List<Component> row : componentsRows) {
            List<String[]> printed = new ArrayList<>();
            for (Component component : row) {
                printed.add(component.toString().split("\n"));
            }

            int height = printed.getFirst().length;

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < printed.size(); j++) {
                    output.append(printed.get(j)[i]);
                    if (j < printed.size() - 1) {
                        output.append("  ");
                    }
                }
                output.append("\n");
            }
        }

        return output.toString();
    }

    /**
     * Manages the in-lobby status.
     * You stay there until the game starts, or you leave the lobby.
     */
    private void handleInLobby(String input) {
        if (input.equals("q")) {
            boolean sure = InputUtility.requestBoolean("You sure? (y/n)", false);
            if (sure)
                client.send(MessageType.LEAVE_GAME);
        }
    }

    private void handleInGame(String input) {
        switch (client.getGameController().getState(client.getUsername())) {

        }
    }

    /**
     * Clears the screen.
     */
    public void clear() {
//        try {
//            String operatingSystem = System.getProperty("os.name");
//
//            // Windows
//            if (operatingSystem.contains("Windows")) {
//                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
//            }
//            // Unix/Linux/MacOS
//            else {
//                // ANSI escape code to clear the screen
//                System.out.print("\033[H\033[2J");
//                System.out.flush();
//
//                // Otherwise, it can be used this
//                // new ProcessBuilder("clear").inheritIO().start().waitFor();
//            }
//        } catch (Exception e) {
//            // print many lines as fallback
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
//        }
    }

    /**
     * Shows the title screen.
     */
    public void start() {
        clear();
        System.out.println("Welcome to");
        Chroma.println(
                """
                        
                         ██████╗  █████╗ ██╗      █████╗ ██╗  ██╗██╗   ██╗    ████████╗██████╗ ██╗   ██╗ ██████╗██╗  ██╗███████╗██████╗ ███████╗
                        ██╔════╝ ██╔══██╗██║     ██╔══██╗╚██╗██╔╝╚██╗ ██╔╝    ╚══██╔══╝██╔══██╗██║   ██║██╔════╝██║ ██╔╝██╔════╝██╔══██╗██╔════╝
                        ██║  ███╗███████║██║     ███████║ ╚███╔╝  ╚████╔╝        ██║   ██████╔╝██║   ██║██║     █████╔╝ █████╗  ██████╔╝███████╗
                        ██║   ██║██╔══██║██║     ██╔══██║ ██╔██╗   ╚██╔╝         ██║   ██╔══██╗██║   ██║██║     ██╔═██╗ ██╔══╝  ██╔══██╗╚════██║
                        ╚██████╔╝██║  ██║███████╗██║  ██║██╔╝ ██╗   ██║          ██║   ██║  ██║╚██████╔╝╚██████╗██║  ██╗███████╗██║  ██║███████║
                         ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝          ╚═╝   ╚═╝  ╚═╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚══════╝
                        """, Chroma.ORANGE
        );
        System.out.println("Press ENTER to continue...");
        scanner.nextLine();
        displayUpdater.updateDisplay();

        Thread displayThread = new Thread(displayUpdater);
        displayThread.setDaemon(true);
        displayThread.start();

        while (true) {
            String input = scanner.nextLine();
            processUserInput(input);
        }
    }

    public void handleDisconnect() {
        // TODO send message disconnect ?
        Chroma.println("Bye!", Chroma.YELLOW_BOLD);
        client.closeConnection();
        System.exit(0);
    }

    public BlockingQueue<String> getNetworkMessageQueue() {
        return networkMessageQueue;
    }

}