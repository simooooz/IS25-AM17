package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.network.Client;

import java.util.*;

public class DisplayUpdater implements Runnable {

    private final Client client;

    public DisplayUpdater(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                String message = client.getViewTui().getNetworkMessageQueue().poll();
                if (message != null) {
                    if (message.equals("ERROR")) {
                        displayError();
                    } else {
                        client.getViewTui().clear();
                        updateDisplay();
                    }
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Nothing to do
            }
        }
    }


    public void updateDisplay() {
        switch (client.getState()) {
            case DISCONNECT:
                client.getViewTui().handleDisconnect();
                break;
            case USERNAME:
                System.out.print("username: ");
                break;
            case LOBBY_SELECTION:
                displayLobbySelection();
                break;
            case IN_LOBBY:
                displayLobbyInfo();
                break;
            case IN_GAME:
                displayGame();
                break;
        }
    }

    private void displayLobbyInfo() {
        System.out.println("✅ Lobby ID: " + client.getLobby().getGameID());
        System.out.println((client.getLobby().isLearnerMode() ? "🔵" : "🟣").concat(" Game Mode: ".concat(client.getLobby().isLearnerMode() ? "Learner Flight" : "Standard")));
        System.out.println("👥 " + client.getLobby().getPlayers().size() + "/" + client.getLobby().getMaxPlayers() + " players:");

        for (String player : client.getLobby().getPlayers()) {
            Chroma.println("- " + player, Chroma.GREY_BOLD);
        }

        Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
        Chroma.println("waiting for players to join in...", Chroma.YELLOW);
    }

    private void displayLobbySelection() {
        Chroma.println("MENU", Chroma.GREY_BOLD);
        System.out.println("1. Create a new lobby");
        System.out.println("2. Join a lobby");
        System.out.println("3. Join in a random lobby");
        Chroma.println("press 'q' to quit the game", Chroma.GREY_BOLD);
        System.out.print("\n> ");
    }


    private String gridOfComponents(TreeMap<Integer, Component> components, int componentsPerRow) {
        StringBuilder output = new StringBuilder();

        List<Integer> keys = new ArrayList<>(components.keySet());
        // row by row
        for (int rowStart = 0; rowStart < keys.size(); rowStart += componentsPerRow) {
            int rowEnd = Math.min(rowStart + componentsPerRow, keys.size());

            // Collect all component for this row
            String[][] rowComponentLines = new String[rowEnd - rowStart][];

            for (int i = 0; i < rowEnd - rowStart; i++) {
                Integer key = keys.get(rowStart + i);
                rowComponentLines[i] = components.get(key).print(Optional.of(key)).split("\n");
            }

            int height = rowComponentLines[0].length;
            // print the row line by line
            for (int lineIndex = 0; lineIndex < height; lineIndex++) {
                for (int compIndex = 0; compIndex < rowComponentLines.length; compIndex++) {
                    output.append(rowComponentLines[compIndex][lineIndex]);

                    // add spacing between components, except after the last one
                    if (compIndex < rowComponentLines.length - 1) {
                        output.append("  ");
                    }
                }
                output.append("\n");
            }
        }

        return output.toString();
    }

    private void displayGame() {
        PlayerState state = client.getGameController().getState(client.getUsername());
        Ship ship = client.getGameController().getModel().getBoard().getPlayerEntityByUsername(client.getUsername()).getShip();
        switch (state) {
            case BUILD -> {
                // todo: how to get the map of components properly?
                Map<Integer, Component> components = client.getGameController().getModel().getBoard().getMapIdComponents();
                System.out.println(
                        gridOfComponents(
                                new TreeMap<>(components),
                                10
                        )
                );

                System.out.print("\n\n\n\n\n");
                System.out.println(ship);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println(
                        """
                                        [pick <id>]                  - pick a component
                                        [release]                    - release the picked component
                                        [reserve <id>]               - reserve the picked component
                                        [insert <x> <y>]             - inserts the component into (x,y)
                                        [move <id> <x> <y>]          - moves the component corresponding to the given id into the box with the given coordinates
                                        [rotate <times>]             - rotate picked card clockwise (0-3)
                                        [look-at-cards <id>]         - view specific card pile (1,2 or 3)
                                        [ready]                      - end building phase
                                """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }
            case CHECK -> {
                System.out.println(ship);
                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println("Oh no! Your ship is not valid :(. You have to fix it before you can continue...", Chroma.RED_BOLD);
                System.out.println(
                        """
                                ENTER a series of components <id> to remove to fix the ship
                                [<id_1> <id_2> ... <id_n>]                 - remove a 'bad' component from the ship]
                                """
                );
                System.out.print("> ");
            }
            case WAIT_ALIEN -> {
                System.out.println(ship);
                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println(
                        "You might want to put aliens in your cabins!",
                        Chroma.GREEN_BOLD
                );
                System.out.println(
                        """
                                ENTER a series of <id> cabins in which to put aliens
                                [<id_1> <id_2> ... <id_n>]                 - add alien into the given cabins]
                                """
                );
                System.out.print("> ");
            }
            case DRAW_CARD -> {
                System.out.println(ship);
                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                System.out.println("PRESS enter to draw a card...");
            }
            case WAIT_CANNONS -> {
            }
            case WAIT_ENGINES -> {
            }
            case WAIT_GOODS -> {
            }
            case WAIT_REMOVE_GOODS -> {
            }
            case WAIT_ROLL_DICES -> {
            }
            case WAIT_REMOVE_CREW -> {
            }
            case WAIT_SHIELD -> {
            }
            case WAIT_BOOLEAN -> {
            }
            case WAIT_INDEX -> {
            }
        }
    }


    public void displayError() {
        Chroma.println("Remote error :/ please try again", Chroma.RED);
    }

}
