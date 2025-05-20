package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.Scanner;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

public class ViewTui {

    private final Client client;
    private final Scanner scanner;

    private final DisplayUpdater displayUpdater;
    private final BlockingQueue<String> networkMessageQueue = new LinkedBlockingQueue<>();

    private int offlineRotations = 0;

    /**
     * Constructs a new ViewTui with the given client controller.
     *
     * @param client the client that handles communication logic
     */
    public ViewTui(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.displayUpdater = new DisplayUpdater(this.client);

        this.offlineRotations = 0;
    }

    private void processUserInput(String input) {
        try {
            switch (client.getState()) {
                case USERNAME:
                    if (input.length() < 3 || input.length() > 18) throw new IllegalArgumentException();
                    client.send(MessageType.SET_USERNAME, input);
                    break;

                case LOBBY_SELECTION:
                    switch (input) {
                        case "1" -> handleCreateLobby();
                        case "2" -> handleJoinLobby();
                        case "3" -> handleJoinRandomLobby();
                        case "q" -> handleDisconnect();
                        default -> throw new IllegalArgumentException();
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
            Chroma.println("Command not valid. Please try again.", Chroma.RED);
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
            boolean sure = InputUtility.requestBoolean("You sure? (y/n)", false);
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
                String[] commands = input.split(" ");
                switch (commands[0]) {
                    case "pick" -> {
                        client.send(MessageType.PICK_COMPONENT, Integer.parseInt(commands[1]));
                        this.offlineRotations = 0;
                    }
                    case "release" -> client.send(MessageType.RELEASE_COMPONENT, Integer.parseInt(commands[1]));
                    case "reserve" -> client.send(MessageType.RESERVE_COMPONENT, Integer.parseInt(commands[1]));
                    case "insert" -> {
                        int row = Integer.parseInt(commands[2]) - 5;
                        int col = Integer.parseInt(commands[3]) - 4;
                        client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(commands[1]), row, col, offlineRotations);
                        this.offlineRotations = 0;
                    }
                    case "move" -> {
                        int row = Integer.parseInt(commands[2]) - 5;
                        int col = Integer.parseInt(commands[3]) - 4;
                        client.send(MessageType.MOVE_COMPONENT, Integer.parseInt(commands[1]), row, col, offlineRotations);
                    }
                    case "rotate" -> {
                        int id = Integer.parseInt(commands[1]);
                        Component component = client.getGameController().getModel().getBoard().getMapIdComponents().get(id);
                        Optional<Component> handComponent = client.getGameController().getModel().getBoard().getPlayerEntityByUsername(client.getUsername()).getShip().getHandComponent();

                        if (component == null) throw new IllegalArgumentException();
                        else if (handComponent.isPresent() && handComponent.get().equals(component)) // If component is already in the dashboard send rotare, otherwise increase counter
                            offlineRotations += Integer.parseInt(commands[2]);
                        else
                            client.send(MessageType.ROTATE_COMPONENT, Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
                    }
                    case "look-at-cards" -> {
                        int deckIndex = Integer.parseInt(commands[1]);
                        if (deckIndex < 1 || deckIndex > 3)
                            throw new IllegalArgumentException("Deck index out of bounds");
                        client.send(MessageType.LOOK_CARD_PILE, deckIndex);
                    }
                    case "ready" -> client.send(MessageType.SET_READY);
                    default -> throw new IllegalArgumentException();
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
                    throw new IllegalArgumentException();

                Map<Integer, AlienType> alienMap = new HashMap<>();
                for (int i = 0; i < ids.length; i++)
                    alienMap.put(ids[i], aliens[i]);
                client.send(MessageType.CHOOSE_ALIEN, alienMap);
            }
            case WAIT_SHIP_PART -> client.send(MessageType.CHOOSE_SHIP_PART, Integer.parseInt(input));
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