package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.UserState;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.TUI.graphics.ComponentsTUI;

import java.util.Scanner;
import java.util.*;
import java.util.List;

public class ViewTui {

    private final Client client;
    private final Scanner scanner;
    private Thread inputThread;

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
        System.out.println("Press 'q' to go back to the menu.");

        // todo: capire perché blocca aggiornamento e risolvere
//        if (scanner.hasNextLine()) {
//            String input = scanner.nextLine().trim();
//
//            if (input.equals("q")) {
//                TUIColors.printlnColored("You sure? (y/n)", TUIColors.WHITE_BOLD);
//                if (scanner.hasNextLine()) {
//                    String confirmation = scanner.nextLine().trim();
//                    if (confirmation.equals("y")) {
//                        client.send(MessageType.LEAVE_GAME);
//                    } else {
//                        // todo: è utile?
//                        handleInLobby();
//                    }
//                }
//            }
//        }
    }

    /**
     * Handles the in-game state.
     */
    private void handleInGame() {
        clear();

        // todo: only-for test the graphics of components
        Map<Integer, Component> components = new Board(new ArrayList<>(), false).getMapIdComponents();

        List<ComponentsTUI.ComponentUI> componentsView = new ArrayList<>();
        for (Map.Entry<Integer, Component> entry : components.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Component component = entry.getValue();

            ComponentsTUI.ComponentUI componentUI = switch (component) {
                case BatteryComponent ignored -> new ComponentsTUI.BatteryComponent(key, false);
                case CabinComponent ignored -> new ComponentsTUI.CabinComponent(key);
                case CannonComponent ignored -> new ComponentsTUI.CannonComponent(key, false);
                case CargoHoldsComponent ignored -> new ComponentsTUI.CargoHoldsComponent(key);
                case EngineComponent ignored -> new ComponentsTUI.EngineComponent(key, false);
                case OddComponent ignored -> new ComponentsTUI.OddComponent(key, AlienType.ENGINE);
                case ShieldComponent ignored -> new ComponentsTUI.ShieldComponent(key);
                case SpecialCargoHoldsComponent ignored -> new ComponentsTUI.SpecialCargoHoldsComponent(key);
                case null, default -> new ComponentsTUI.Component(key);
            };

            componentsView.add(componentUI);
        }

        // quick lookup of the components
        Map<String, ComponentsTUI.ComponentUI> idToComponent = new HashMap<>();
        for (ComponentsTUI.ComponentUI component : componentsView) {
            idToComponent.put(component.getId(), component);
        }

        boolean ready = false;
        while (!ready) {
            String id;
            do {
                clear();
                System.out.println(ComponentsTUI.gridOfComponents(componentsView, 13));
                TUIColors.printColored("Press 'q' to quit the game.", TUIColors.WHITE_BOLD);
                TUIColors.printColored("\nInsert the ID of the component you want to pick: ", TUIColors.YELLOW_BOLD);

                id = scanner.nextLine().trim();

                if (id.equals("q")) {
                    client.send(MessageType.LEAVE_GAME);
                    return;
                }

                if (id.equals("r")) {
                    // todo: ready message
                    ready = true;
                    break;
                }

                if (!idToComponent.containsKey(id) || id.isEmpty()) {
                    TUIColors.printlnColored("ID not valid", TUIColors.RED);
                }

            } while (id.isEmpty() || !idToComponent.containsKey(id));

            // todo: message to uncover
            if (!id.equals("r")) {
                ComponentsTUI.ComponentUI component = idToComponent.get(id);
                component.uncover();
            }
        }

        TUIColors.printColored("READY! ", TUIColors.GREEN_BOLD);
        System.out.println("Waiting for other players to get ready...");
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
