package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;

import java.io.IOException;
import java.util.Scanner;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

public class ViewTui {

    private final Client client;
    private final Scanner scanner;

    private final DisplayUpdater displayUpdater;
    private final BlockingQueue<String> networkMessageQueue = new LinkedBlockingQueue<>();
    protected volatile CountDownLatch waitLatch = new CountDownLatch(0);

    private String localCommand = "";

    /**
     * Constructs a new ViewTui with the given client controller.
     *
     * @param client the client that handles communication logic
     */
    public ViewTui(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.displayUpdater = new DisplayUpdater(this.client);
    }

    private void processUserInput(String input) {
        try {
            switch (client.getState()) {
                case USERNAME:
                    if (input.length() < 3 || input.length() > 18)
                        throw new IllegalArgumentException("Username length must be between 3 and 18.");
                    client.send(MessageType.SET_USERNAME, input);
                    break;

                case LOBBY_SELECTION:
                    switch (input) {
                        case "1" -> handleCreateLobby();
                        case "2" -> handleJoinLobby();
                        case "3" -> handleJoinRandomLobby();
                        case "q" -> handleDisconnect();
                        default -> throw new IllegalArgumentException("Command not valid. Please try again.");
                    }
                    break;

                case IN_LOBBY:
                    handleInLobby(input);
                    break;

                case IN_GAME:
                    handleInGame(input);
                    break;
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            Chroma.println(e.getMessage(), Chroma.RED);
            System.out.print("> ");
        }
    }

    /**
     * Handles the lobby creation process.
     */
    private void handleCreateLobby() {
        clear();
        Chroma.println("press 'q' to go back to the menu", Chroma.GREY_BOLD);

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
        Chroma.println("press 'q' to go back to the menu", Chroma.GREY_BOLD);

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
        Chroma.println("press 'q' to go back to the menu", Chroma.GREY_BOLD);

        Boolean learnerFlight = InputUtility.requestBoolean("Learner flight? (true/false): ", true);
        if (learnerFlight == null) {
            displayUpdater.updateDisplay();
            return;
        }

        client.send(MessageType.JOIN_RANDOM_LOBBY, learnerFlight);
    }

    /**
     * Handles user interactions while in the lobby.
     *
     * @param input the user-provided input to be processed. If the input is "q",
     *              the user is prompted for confirmation to leave the lobby.
     */
    private void handleInLobby(String input) {
        Chroma.println("press 'q' to go back to the menu", Chroma.GREY_BOLD);
        if (input.equals("q")) {
            boolean sure = InputUtility.requestBoolean("You sure? (y/n) ", false);
            if (sure)
                client.send(MessageType.LEAVE_GAME);
        }
    }

    /**
     * Handles various in-game actions based on the player's current state.
     *
     * @param input provided by the user to influence the game logic
     */
    private void handleInGame(String input) {
        switch (client.getGameController().getState(client.getUsername())) {
            case BUILD -> {
                String[] commands = input.trim().split(" ");
                switch (commands[0]) {
                    case "pick" -> {
                        ;
                        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
                            client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
                        revertRotation();

                        localCommand = "";
                        client.send(MessageType.PICK_COMPONENT, Integer.parseInt(commands[1]));
                    }
                    case "release" -> {
                        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) { // Previous local command was "insert", revert rotations
                            try {
                                int times = 4 - (Integer.parseInt(localCommand.split(" ")[4]) % 4);
                                client.getGameController().rotateComponent(client.getUsername(), Integer.parseInt(localCommand.split(" ")[1]), times);
                            } catch (RuntimeException e) {
                                // Propagate general exceptions
                                throw new IllegalArgumentException(e.getMessage());
                            }
                        }
                        revertRotation();

                        localCommand = "";
                        client.send(MessageType.RELEASE_COMPONENT, Integer.parseInt(commands[1]));
                    }
                    case "reserve" -> {
                        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) { // Previous local command was "insert", revert rotations
                            try {
                                int times = 4 - (Integer.parseInt(localCommand.split(" ")[4]) % 4);
                                client.getGameController().rotateComponent(client.getUsername(), Integer.parseInt(localCommand.split(" ")[1]), times);
                            } catch (RuntimeException e) {
                                // Propagate general exceptions
                                throw new IllegalArgumentException(e.getMessage());
                            }
                        }
                        revertRotation();

                        localCommand = "";
                        client.send(MessageType.RESERVE_COMPONENT, Integer.parseInt(commands[1]));
                    }
                    case "insert" -> {
                        // Do insert locally
                        try {
                            int row = Integer.parseInt(commands[2]) - 5;
                            int col = Integer.parseInt(commands[3]) - 4;
                            input = String.join(" ", "insert", commands[1], String.valueOf(row), String.valueOf(col));
                            client.getGameController().insertComponent(client.getUsername(), Integer.parseInt(commands[1]), row, col, 0, false);

                            if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("rotate")) // Previous local command was "rotate"
                                localCommand = String.join(" ", "insert", commands[1], String.valueOf(row), String.valueOf(col), localCommand.split(" ")[2]);
                            else if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) { // Previous local command was "insert" (ex. of a reserve)
                                client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
                                localCommand = input + " 0";
                            } else // No previous local command
                                localCommand = input + " 0";

                            displayUpdater.updateDisplay();
                        } catch (RuntimeException e) {
                            // Propagate general exceptions
                            throw new IllegalArgumentException(e.getMessage());
                        }
                    }
                    case "move" -> {
                        // Do move locally
                        try {
                            int row = Integer.parseInt(commands[2]) - 5;
                            int col = Integer.parseInt(commands[3]) - 4;
                            client.getGameController().moveComponent(client.getUsername(), Integer.parseInt(commands[1]), row, col, 0);

                            if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
                                localCommand = String.join(" ", localCommand.split(" ")[0], localCommand.split(" ")[1], String.valueOf(row), String.valueOf(col), localCommand.split(" ")[4]);
                            else // No previous local command
                                localCommand = input;

                            displayUpdater.updateDisplay();
                        } catch (RuntimeException e) {
                            // Propagate general exceptions
                            throw new IllegalArgumentException(e.getMessage());
                        }
                    }
                    case "rotate" -> {
                        // Do rotation locally
                        try {
                            int times = Integer.parseInt(commands[2]) % 4;
                            client.getGameController().rotateComponent(client.getUsername(), Integer.parseInt(commands[1]), times);

                            if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("rotate")) // Previous local command was "rotate"
                                localCommand = String.join(" ", localCommand.split(" ")[0], localCommand.split(" ")[1], String.valueOf((times + Integer.parseInt(commands[2])) % 4));
                            else if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
                                localCommand = String.join(" ", localCommand.split(" ")[0], localCommand.split(" ")[1], localCommand.split(" ")[2], localCommand.split(" ")[3], String.valueOf((Integer.parseInt(localCommand.split(" ")[4]) + times) % 4));
                            else // No previous local command
                                localCommand = input;

                            displayUpdater.updateDisplay();
                        } catch (RuntimeException e) {
                            // Propagate general exceptions
                            throw new IllegalArgumentException(e.getMessage());
                        }
                    }
                    case "look-at-cards" -> {
                        int deckIndex = Integer.parseInt(commands[1]);
                        if (deckIndex < 1 || deckIndex > 3)
                            throw new IllegalArgumentException("Deck index out of bounds");

                        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) { // Previous local command was "insert", otherwise don't change it
                            client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
                            localCommand = "";
                        }

                        client.send(MessageType.LOOK_CARD_PILE, deckIndex);
                    }
                    case "ready" -> {
                        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
                            client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
                        revertRotation();

                        localCommand = "";
                        client.send(MessageType.SET_READY);
                    }
                    default -> throw new IllegalArgumentException("Command not valid. Please try again.");
                }
            }
            case CHECK -> {
                List<Integer> ids = Arrays.stream(input.split(" "))
                        .map(Integer::parseInt)
                        .toList();
                client.send(MessageType.CHECK_SHIP, ids);
            }
            case WAIT_ALIEN -> {
                String[] commands = input.split(" ");
                Integer[] ids = IntStream.range(0, commands.length)
                        .filter(i -> i % 2 == 0)
                        .mapToObj(i -> Integer.parseInt(commands[i]))
                        .toArray(Integer[]::new);
                AlienType[] aliens = IntStream.range(0, commands.length)
                        .filter(i -> i % 2 == 1)
                        .mapToObj(i -> Objects.equals(commands[i], "cannon") ? AlienType.CANNON : AlienType.ENGINE)
                        .toArray(AlienType[]::new);

                if (ids.length != aliens.length)
                    throw new IllegalArgumentException("Command not valid. Please try again.");

                Map<Integer, AlienType> alienMap = new HashMap<>();
                for (int i = 0; i < ids.length; i++)
                    alienMap.put(ids[i], aliens[i]);
                client.send(MessageType.CHOOSE_ALIEN, alienMap);
            }
            case WAIT_SHIP_PART -> client.send(MessageType.CHOOSE_SHIP_PART, Integer.parseInt(input));
            case DRAW_CARD -> client.send(MessageType.DRAW_CARD);
            case WAIT_CANNONS -> {}
            case WAIT_ENGINES -> {}
            case WAIT_GOODS -> {}
            case WAIT_REMOVE_GOODS -> {}
            case WAIT_ROLL_DICES -> client.send(MessageType.ROLL_DICES);
            case WAIT_REMOVE_CREW -> {}
            case WAIT_SHIELD -> {}
            case WAIT_BOOLEAN -> {}
            case WAIT_INDEX -> {}
        }
    }

    private void revertRotation() {
        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("rotate")) { // Previous local command was "rotate", revert it
            try {
                int times = 4 - (Integer.parseInt(localCommand.split(" ")[2]) % 4);
                client.getGameController().rotateComponent(client.getUsername(), Integer.parseInt(localCommand.split(" ")[1]), times);
            } catch (RuntimeException e) {
                // Propagate general exceptions
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * Clears the screen.
     */
    public void clear() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            // for mac
            System.out.println("\033\143");
        }
    }

    public void displayError(String message) {
        Chroma.println(message, Chroma.RED);
        System.out.print("> ");
    }

    /**
     * Shows the title screen.
     */
    public void start() {
        clear();
        System.out.println("Welcome to");
        Chroma.println(
                """
                        
                         ██████╗  █████╗ ██╗      █████╗ ██╗  ██╗██╗   ██╗    ████████╗██████╗ ██╗   ██╗ ██████╗██╗  ██╗███████╗██████╗
                        ██╔════╝ ██╔══██╗██║     ██╔══██╗╚██╗██╔╝╚██╗ ██╔╝    ╚══██╔══╝██╔══██╗██║   ██║██╔════╝██║ ██╔╝██╔════╝██╔══██╗
                        ██║  ███╗███████║██║     ███████║ ╚███╔╝  ╚████╔╝        ██║   ██████╔╝██║   ██║██║     █████╔╝ █████╗  ██████╔╝
                        ██║   ██║██╔══██║██║     ██╔══██║ ██╔██╗   ╚██╔╝         ██║   ██╔══██╗██║   ██║██║     ██╔═██╗ ██╔══╝  ██╔══██╗
                        ╚██████╔╝██║  ██║███████╗██║  ██║██╔╝ ██╗   ██║          ██║   ██║  ██║╚██████╔╝╚██████╗██║  ██╗███████╗██║  ██║
                         ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝          ╚═╝   ╚═╝  ╚═╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝
                        """, Chroma.ORANGE
        );
        System.out.println("Press ENTER to continue...");
        scanner.nextLine();
        displayUpdater.updateDisplay();

        Thread displayThread = new Thread(displayUpdater);
        displayThread.setDaemon(true);
        displayThread.start();

        while (true) {

            try {
                this.waitLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Chroma.println("Input thread interrupted during wait", Chroma.RED);
                break;
            }

            String input = scanner.nextLine();
            processUserInput(input);
        }

        this.scanner.close();
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
