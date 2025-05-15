package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.UserState;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.ZeroArgMessage;

import java.util.Scanner;

public class ViewTui {

    private final Client client;
    private final Scanner scanner;
    private Thread inputThread;

    /**
     * Indicates whether the program is currently waiting for a response from the server.
     * This variable is marked as volatile to ensure visibility of changes to it across threads.
     */
    private volatile boolean waitingForStateChange = false;
    private final Object stateLock = new Object();


    /**
     * Constructs a new ViewTui with the given client controller.
     *
     * @param client the client that handles communication logic
     */
    public ViewTui(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    public void handleUIState() {
        switch (client.getState()) {
            case DISCONNECT:
                handleDisconnect();
                break;
            case USERNAME:
                handleUsername();
                break;
            case LOBBY_SELECTION:
                handleLobbySelection();
                break;
            case IN_LOBBY:
                handleInLobby();
                break;
            case IN_GAME:
                handleInGame();
                break;
            default:
                break;
        }
    }

    private void waitForStateChange() {
        waitingForStateChange = true;
        synchronized (stateLock) {
            try {
                while (waitingForStateChange) {
                    stateLock.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while waiting for state change: " + e.getMessage());
            }
        }
    }

    public void unlockWaitingForStateChange() {
        synchronized (stateLock) {
            waitingForStateChange = false;
            stateLock.notifyAll();
        }
    }

    /**
     * Prompt for username.
     */
    private void handleUsername() {
        String username;

        do {
            TUIColors.printColored("username: ", TUIColors.WHITE_BOLD);
            username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                TUIColors.printlnColored("username cannot be empty", TUIColors.RED);
            }
        } while (username.isEmpty());

        client.send(MessageType.SET_USERNAME, username);
    }


    /**
     * Handles the lobby creation process.
     */
    private void handleCreateLobby() {
        System.out.print("Name of the lobby: ");
        String lobbyName = scanner.nextLine().trim();

        System.out.print("Number of players (2-4): ");
        int maxPlayers;
        do {
            maxPlayers = Integer.parseInt(scanner.nextLine().trim());
            if (maxPlayers < 2 || maxPlayers > 4) {
                TUIColors.printColored("max number of players must be between 2 and 4", TUIColors.RED);
            }
        } while (maxPlayers < 2 || maxPlayers > 4);

        System.out.print("Learner flight? (true/false): ");
        boolean learnerFlight = Boolean.parseBoolean(scanner.nextLine().trim());

        client.send(MessageType.CREATE_LOBBY, lobbyName, maxPlayers, learnerFlight);
    }

    /**
     * Handles the process of joining a specified existing lobby.
     */
    private void handleJoinLobby() {
        System.out.print("Name of the lobby: ");

        String lobbyName;
        do {
            lobbyName = scanner.nextLine().trim();
            if (lobbyName.isEmpty()) {
                TUIColors.printColored("lobby name cannot be empty", TUIColors.RED);
            }
        } while (lobbyName.isEmpty());

        client.send(MessageType.JOIN_LOBBY, lobbyName);
    }

    /**
     * Handles the process of joining a random lobby.
     */
    private void handleJoinRandomLobby() {
        System.out.print("Learner flight? (true/false): ");
        boolean learnerFlight = Boolean.parseBoolean(scanner.nextLine().trim());
        client.send(MessageType.JOIN_RANDOM_LOBBY, learnerFlight);
    }

    /**
     * Handles the lobby menu.
     * Allows you to create a new lobby or join a specific or random one.
     */
    private void handleLobbySelection() {
        clear();
        TUIColors.printlnColored("\nMENU", TUIColors.WHITE_BOLD);
        System.out.println("1. Create a new lobby");
        System.out.println("2. Join a lobby");
        System.out.println("3. Join in a random lobby");
        System.out.println("4. Quit the game");
        System.out.print("\nChoose an option (1-4): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleCreateLobby();
                break;
            case "2":
                handleJoinLobby();
                break;
            case "3":
                handleJoinRandomLobby();
                break;
            case "4":
                handleDisconnect();
                break;
            default:
                System.out.println("Option not valid. Please try again.");
                break;
        }
    }

    private void displayLobbyInfo(Lobby lobby) {
        System.out.println("✅ Lobby ID: " + lobby.getGameID());
        System.out.println("👥 Players:");

        for (String player : lobby.getPlayers()) {
            System.out.println("- " + player);
        }
    }

    /**
     * Manages the in-lobby status.
     * You stay there until the game starts, or you leave the lobby.
     */
    private void handleInLobby() {
        clear();
        displayLobbyInfo(client.getLobby());
        TUIColors.printlnColored("\n\nWaiting for players to join in...", TUIColors.WHITE_BOLD);
        System.out.println("Press 'q' to quit the lobby");
//
//        if (waitingForStateChange) return;
//
//        this.inputThread = new Thread(() -> {
//            while (client.getState() == UserState.IN_LOBBY) {
//                // use scanner.hasNextLine() for non-blocking polling
//                if (scanner.hasNextLine()) {
//                    String input = scanner.nextLine().trim();
//
//                    if (input.equals("q")) {
//                        TUIColors.printlnColored("You sure? (y/n)", TUIColors.WHITE_BOLD);
//                        if (scanner.hasNextLine()) {
//                            String confirmation = scanner.nextLine().trim();
//                            if (confirmation.equals("y")) {
//                                client.send(MessageType.LEAVE_GAME);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        });
//
//        this.inputThread.setDaemon(true);  // thread daemon will terminate when the main program ends
//        this.inputThread.start();
//
//        waitForStateChange();
//
//        // here the state has changed, then we can interrupt the input thread
//        inputThread.interrupt();
    }

    /**
     * Handles the in-game state.
     */
    private void handleInGame() {
        System.out.println("\n=== MATCH IN PROGRESS ===");
        // Game-specific implementation would go here

        // For now, we return to the main menu
        System.out.println("Game finished. Thank you for playing!");
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
        System.out.println("\nWelcome to");
        System.out.println(
                """
                        
                         ██████╗  █████╗ ██╗      █████╗ ██╗  ██╗██╗   ██╗    ████████╗██████╗ ██╗   ██╗ ██████╗██╗  ██╗███████╗██████╗ ███████╗
                        ██╔════╝ ██╔══██╗██║     ██╔══██╗╚██╗██╔╝╚██╗ ██╔╝    ╚══██╔══╝██╔══██╗██║   ██║██╔════╝██║ ██╔╝██╔════╝██╔══██╗██╔════╝
                        ██║  ███╗███████║██║     ███████║ ╚███╔╝  ╚████╔╝        ██║   ██████╔╝██║   ██║██║     █████╔╝ █████╗  ██████╔╝███████╗
                        ██║   ██║██╔══██║██║     ██╔══██║ ██╔██╗   ╚██╔╝         ██║   ██╔══██╗██║   ██║██║     ██╔═██╗ ██╔══╝  ██╔══██╗╚════██║
                        ╚██████╔╝██║  ██║███████╗██║  ██║██╔╝ ██╗   ██║          ██║   ██║  ██║╚██████╔╝╚██████╗██║  ██╗███████╗██║  ██║███████║
                         ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝          ╚═╝   ╚═╝  ╚═╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚══════╝
                        """
        );
        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();

        handleUIState();
    }

    public void handleDisconnect() {
        // TODO send message disconnect ?
        TUIColors.printColored("\nBye!", TUIColors.YELLOW_BOLD);
        client.closeConnection();
        System.exit(0);
    }

}
