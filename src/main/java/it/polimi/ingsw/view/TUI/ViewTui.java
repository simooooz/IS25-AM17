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
import it.polimi.ingsw.view.TUI.graphics.ShipBoardTUI;

import java.util.Scanner;
import java.util.*;
import java.util.List;

public class ViewTui {

    private final Client client;
    private final Scanner scanner;
    private Thread inputThread;
    private ShipBoardTUI shipBoard;

    /**
     * Constructs a new ViewTui with the given client controller.
     *
     * @param client the client that handles communication logic
     */
    public ViewTui(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.shipBoard = new ShipBoardTUI(null, 5, 7);
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
        String username = InputUtility.requestString("Username: ", false, 3, 18);
        client.send(MessageType.SET_USERNAME, username);
    }

    /**
     * Handles the lobby creation process.
     */
    private void handleCreateLobby() {
        clear();
        Chroma.println("CREATE LOBBY (press q to go back to main menù)", Chroma.WHITE_BOLD);

        String lobbyName = InputUtility.requestString("Name of the lobby: ", true, 3, 18);
        if (lobbyName == null) handleUIState();
        Integer maxPlayers = InputUtility.requestInt("Number of players (2-4): ", true, 2, 4);
        if (maxPlayers == null) handleUIState();
        Boolean learnerFlight = InputUtility.requestBoolean("Learner flight? (true/false): ", true);
        if (learnerFlight == null) handleUIState();

        client.send(MessageType.CREATE_LOBBY, lobbyName, maxPlayers, learnerFlight);
    }

    /**
     * Handles the process of joining a specified existing lobby.
     */
    private void handleJoinLobby() {
        clear();
        Chroma.println("JOIN LOBBY (press q to go back to main menù)", Chroma.WHITE_BOLD);

        String lobbyName = InputUtility.requestString("Name of the lobby: ", true, 3, 18);
        if (lobbyName == null) handleUIState();

        client.send(MessageType.JOIN_LOBBY, lobbyName);
    }

    /**
     * Handles the process of joining a random lobby.
     */
    private void handleJoinRandomLobby() {
        clear();
        Chroma.println("JOIN RANDOM LOBBY (press q to go back to main menù)", Chroma.WHITE_BOLD);

        Boolean learnerFlight = InputUtility.requestBoolean("Learner flight? (true/false): ", true);
        if (learnerFlight == null) handleUIState();

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
     * Handles the lobby menu.
     * Allows you to create a new lobby or join a specific or random one.
     */
    private void handleLobbySelection() {
        clear();
        Chroma.println("MENU", Chroma.WHITE_BOLD);
        System.out.println("1. Create a new lobby");
        System.out.println("2. Join a lobby");
        System.out.println("3. Join in a random lobby");
        System.out.println("4. Quit the game");
        int choice = InputUtility.requestInt("Choose an option (1-4): ", false,1, 4);

        switch (choice) {
            case 1:
                handleCreateLobby();
                break;
            case 2:
                handleJoinLobby();
                break;
            case 3:
                handleJoinRandomLobby();
                break;
            case 4:
                handleDisconnect();
                break;
            default:
                Chroma.println("Option not valid. Please try again.", Chroma.RED);
                break;
        }
    }

    private void displayLobbyInfo(Lobby lobby) {
        System.out.println("✅ Lobby ID: " + lobby.getGameID());
        System.out.println("👥 " + lobby.getPlayers().size() + "/" + lobby.getMaxPlayers() + " players:");

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
        Chroma.println("\n\nWaiting for players to join in...", Chroma.WHITE_BOLD);
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
        // todo: get components for testing (then how?)
        Map<Integer, Component> components = client.getGameController().getModel().getBoard().getMapIdComponents();

            String id;
            do {
                clear();
                System.out.println(gridOfComponents(components.values().stream().toList(), 6));
                System.out.println("\nYour current ship:");
                shipBoard.printBoard();

                Chroma.println("- Type 'r' when ready to finish building", Chroma.GREEN);
                Chroma.println("- Type 'q' to quit the game", Chroma.ORANGE);
                System.out.print("\n- Enter component ID to pick it: ");
                id = scanner.nextLine().trim();

                if (id.equals("q")) {
                    client.send(MessageType.LEAVE_GAME);
                    return;
                }

                if (id.equals("r")) {
                    // player has finished building the ship
                    // building = false;
                    break;
                }

                if (!components.containsKey(Integer.parseInt(id)) || id.isEmpty()) {
                    Chroma.println("ID not valid", Chroma.RED);
                } else {
                    client.send(MessageType.PICK_COMPONENT, Integer.parseInt(id));
                }
            } while (id.isEmpty() || !components.containsKey(Integer.parseInt(id)));


//            // Placement phase - place selected component on ship
//            if (selectedComponent != null && building) {
//                clear();
//                System.out.println("Selected component: " + selectedComponent.getId());
//                System.out.println(selectedComponent);
//                shipBoard.printBoard();
//
//                if (shipBoard.promptForPlacement(components, scanner)) {
//                    // Component placed successfully
//                    components.remove(selectedComponent.getId());
//                    componentsView.remove(selectedComponent);
//                }
//            }

        //Chroma.print("READY! ", Chroma.GREEN_BOLD);
        //System.out.println("Waiting for other players to get ready...");

        // todo: gestire il time

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
        System.out.println("Press ENTER to continue...");
        scanner.nextLine();

        handleUIState();
    }

    public void handleDisconnect() {
        // TODO send message disconnect ?
        Chroma.println("Bye!", Chroma.YELLOW_BOLD);
        client.closeConnection();
        System.exit(0);
    }

    public void displayError() {
        // TODO sistema che fa clear ma non si vede
        Chroma.println("Remote error :/ please try again", Chroma.RED);
        handleUIState();
    }

}
