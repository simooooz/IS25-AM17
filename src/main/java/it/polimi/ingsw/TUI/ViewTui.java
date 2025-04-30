package it.polimi.ingsw.TUI;

import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.SingleArgMessage;
import it.polimi.ingsw.network.messages.lobby.CreateLobbyMessage;
import it.polimi.ingsw.network.messages.lobby.JoinLobbyMessage;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.client.NetworkEventListener;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ViewTui implements NetworkEventListener {

    private final ClientSocket client;
    private final Scanner scanner;
    private UIState currentState;

    // For synchronizing with network responses
    private CountDownLatch responseLatch;
    private Message lastReceivedMessage;
    private boolean waitingForResponse;

    private Lobby lobby;

    /**
     * Constructs a new ViewTui with the given client socket.
     *
     * @param client The client socket used for communication with the server
     */
    public ViewTui(ClientSocket client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.currentState = UIState.USERNAME;

        // Register as network listener
        this.client.getUser().addNetworkEventListener(this);
    }

    @Override
    public void onMessageReceived(Message message) {
        this.lastReceivedMessage = message;

        // Automatic state transitions based on message type
        MessageType type = message.getMessageType();

        if (type == MessageType.USERNAME_OK) {
            currentState = UIState.LOBBY_SELECTION;
        } else if (type == MessageType.CREATE_LOBBY_OK || type == MessageType.JOIN_LOBBY_OK) {
            currentState = UIState.IN_LOBBY;
        } else if (type == MessageType.USERNAME_ALREADY_TAKEN) {
            System.out.println("Username already taken. Please try again.");
            currentState = UIState.USERNAME;
        }

        // Unblock waiting methods if needed
        if (waitingForResponse && responseLatch != null) {
            responseLatch.countDown();
        }
    }

    @Override
    public void onConnectionClosed(String reason) {
        System.out.println("Connection closed: " + reason);
        System.out.println("Exiting application...");
        System.exit(1);
    }

    @Override
    public void onConnectionEstablished() {
        System.out.println("Connected to server successfully!");
    }

    /**
     * Wait for a response from the server, with timeout
     * @param timeoutMs Time to wait in milliseconds
     * @return true if a response was received, false if timeout
     */
    private boolean waitForResponse(int timeoutMs) {
        try {
            waitingForResponse = true;
            responseLatch = new CountDownLatch(1);
            return responseLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return false;
        } finally {
            waitingForResponse = false;
            responseLatch = null;
        }
    }

    /**
     * Starts the TUI and enters the main interface loop.
     */
    public void start() {
        // Show the title screen
        showTitleScreen();

        // Main interface loop
        while (true) {
            switch (currentState) {
                case USERNAME:
                    handleUsernameState();
                    break;
                case LOBBY_SELECTION:
                    handleLobbySelectionState();
                    break;
                case IN_LOBBY:
                    handleInLobbyState();
                    break;
                case IN_GAME:
                    handleInGameState();
                    break;
                default:
                    System.out.println("UNKNOWN STATE!");
                    return;
            }
        }
    }

    /**
     * Handles the username input state.
     */
    private void handleUsernameState() {
        System.out.println("\n=== WELCOME ===");
        System.out.println("To get started, set up your username.");
        System.out.print("Username: ");

        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty. Please try again.");
            return;
        }

        // Send the message to set the username
        Message message = new SingleArgMessage<>(MessageType.SET_USERNAME, username);
        client.getUser().send(message);

        System.out.println("Checking username availability...");

        // Wait for server response
        if (waitForResponse(5000)) {
            // The state will be automatically updated by onMessageReceived
            if (lastReceivedMessage.getMessageType() == MessageType.USERNAME_OK) {
                System.out.println("Username set successfully: " + username);
            }
        } else {
            System.out.println("Server did not respond. Please try again.");
        }
    }

    /**
     * Handles the lobby creation process.
     */
    private void handleCreateLobby() {
        System.out.println("\n=== CREATE LOBBY ===");
        System.out.print("Name of lobby: ");
        String lobbyName = scanner.nextLine().trim();

        System.out.print("Max Player (2-4): ");
        int maxPlayers;
        try {
            maxPlayers = Integer.parseInt(scanner.nextLine().trim());
            if (maxPlayers < 2 || maxPlayers > 4) {
                System.out.println("The number of players must be between 2 and 4. Try again.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number. Try again.");
            return;
        }

        System.out.print("Set timeout? (true/false): ");
        boolean timeout;
        try {
            timeout = Boolean.parseBoolean(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid value. Defaults to false.");
            timeout = false;
        }

        // Send message to create the lobby
        Message message = new CreateLobbyMessage(lobbyName, maxPlayers, timeout);
        client.getUser().send(message);

        System.out.println("Lobby creation request sent. Waiting for server confirmation...");

        // Wait for server response
        if (waitForResponse(5000)) {
            if (lastReceivedMessage.getMessageType() == MessageType.CREATE_LOBBY_OK) {
                System.out.println("Lobby '" + lobbyName + "' successfully created!");
            } else {
                System.out.println("Failed to create lobby. Please try again.");
                currentState = UIState.LOBBY_SELECTION;
            }
        } else {
            System.out.println("Server did not respond. Please try again.");
            currentState = UIState.LOBBY_SELECTION;
        }
    }

    /**
     * Handles the process of joining an existing lobby.
     */
    private void handleJoinLobby() {
        System.out.println("\n=== JOIN A LOBBY ===");
        System.out.print("Name of lobby: ");
        String lobbyName = scanner.nextLine().trim();

        if (lobbyName.isEmpty()) {
            System.out.println("Name of lobby cannot be empty. Please try again.");
            return;
        }

        // Send message to join the lobby
        Message message = new JoinLobbyMessage(lobbyName);
        client.getUser().send(message);

        System.out.println("Request to join lobby sent. Waiting for server confirmation...");

        // Wait for server response
        if (waitForResponse(5000)) {
            if (lastReceivedMessage.getMessageType() == MessageType.JOIN_LOBBY_OK) {
                System.out.println("You have joined '" + lobbyName + "' successfully!");
            } else {
                System.out.println("Failed to join lobby. Please try again.");
                currentState = UIState.LOBBY_SELECTION;
            }
        } else {
            System.out.println("Server did not respond. Please try again.");
            currentState = UIState.LOBBY_SELECTION;
        }
    }
    /**
     * Handles the lobby selection state.
     */
    private void handleLobbySelectionState() {
        clear();
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Create a new lobby");
        System.out.println("2. Join a lobby");
        System.out.println("3. Quit the game");
        System.out.print("\nChoose an option (1-3): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleCreateLobby();
                break;
            case "2":
                handleJoinLobby();
                break;
            case "3":
                System.out.println("Thank you for playing!");
                System.exit(0);
                break;
            default:
                System.out.println("Option not valid. Please try again.");
                break;
        }
    }

    /**
     * Handles the in-lobby state, waiting for all players to join and game to start.
     */
    private void handleInLobbyState() {
        System.out.println("\n=== WAITING IN THE LOBBY ===");
        System.out.println("Players");

        System.out.println("\n1. Start the game");
        System.out.println("2. Quit the lobby");
        System.out.print("\nChoose an option (1-2): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("Starting the game...");
                currentState = UIState.IN_GAME;
                break;
            case "2":
                System.out.println("Quitting the lobby...");
                currentState = UIState.LOBBY_SELECTION;
                break;
            default:
                System.out.println("Option not valid. Please try again.");
                break;
        }
    }

    /**
     * Handles the in-game state.
     */
    private void handleInGameState() {
        System.out.println("\n=== MATCH IN PROGRESS ===");
        // Game-specific implementation would go here

        // For now, we return to the main menu
        System.out.println("Game finished. Thank you for playing!");
        currentState = UIState.LOBBY_SELECTION;
    }

    /**
     * Shows the title screen.
     */
    public void showTitleScreen() {
        clear();
        System.out.println("\nWelcome to");
        System.out.println(
                "\n" +
                        " ██████╗  █████╗ ██╗      █████╗ ██╗  ██╗██╗   ██╗    ████████╗██████╗ ██╗   ██╗ ██████╗██╗  ██╗███████╗██████╗ ███████╗\n" +
                        "██╔════╝ ██╔══██╗██║     ██╔══██╗╚██╗██╔╝╚██╗ ██╔╝    ╚══██╔══╝██╔══██╗██║   ██║██╔════╝██║ ██╔╝██╔════╝██╔══██╗██╔════╝\n" +
                        "██║  ███╗███████║██║     ███████║ ╚███╔╝  ╚████╔╝        ██║   ██████╔╝██║   ██║██║     █████╔╝ █████╗  ██████╔╝███████╗\n" +
                        "██║   ██║██╔══██║██║     ██╔══██║ ██╔██╗   ╚██╔╝         ██║   ██╔══██╗██║   ██║██║     ██╔═██╗ ██╔══╝  ██╔══██╗╚════██║\n" +
                        "╚██████╔╝██║  ██║███████╗██║  ██║██╔╝ ██╗   ██║          ██║   ██║  ██║╚██████╔╝╚██████╗██║  ██╗███████╗██║  ██║███████║\n" +
                        " ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝          ╚═╝   ╚═╝  ╚═╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚══════╝\n"
        );
        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    /**
     * Clears the screen by printing multiple newlines.
     */
    public void clear() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }
}
