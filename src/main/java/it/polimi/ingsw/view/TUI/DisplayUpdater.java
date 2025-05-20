package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.network.Client;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

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
                if (message != null && !message.equals("ERROR")) {
                    client.getViewTui().clear();
                    updateDisplay();
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

    private String gridOfComponents(List<Component> components, int componentsPerRow) {
        StringBuilder output = new StringBuilder();

        for (int rowStart = 0; rowStart < components.size(); rowStart += componentsPerRow) {
            int rowEnd = Math.min(rowStart + componentsPerRow, components.size());

            // Collect all component for this row
            String[][] rowComponentLines = new String[rowEnd - rowStart][];

            for (int i = 0; i < rowEnd - rowStart; i++)
                rowComponentLines[i] = components.get(rowStart + i).toString().split("\n");

            // Print the row line by line
            int height = rowComponentLines[0].length;
            for (int lineIndex = 0; lineIndex < height; lineIndex++) {
                for (int compIndex = 0; compIndex < rowComponentLines.length; compIndex++) {
                    output.append(rowComponentLines[compIndex][lineIndex]);

                    // Add spacing between components, except after the last one
                    if (compIndex < rowComponentLines.length - 1)
                        output.append("  ");
                }
                output.append("\n");
            }
        }
        return output.toString();
    }

    private void displayGame() {
        PlayerState state = client.getGameController().getState(client.getUsername());
        Board board = client.getGameController().getModel().getBoard();
        Ship ship = board.getPlayerEntityByUsername(client.getUsername()).getShip();

        switch (state) {
            case BUILD -> {

                // Not ready
                if (board.getStartingDeck().contains(board.getPlayerEntityByUsername(client.getUsername()))) {
                    List<Component> commonComponents = board.getCommonComponents();
                    System.out.println("Common components:\n");
                    System.out.println(gridOfComponents(commonComponents, 10));

                    if (ship.getReserves().isEmpty())
                        System.out.println("\nReserves: none");
                    else {
                        System.out.println("\nReserves:\n");
                        System.out.println(gridOfComponents(ship.getReserves(), 2));
                    }

                    if (ship.getHandComponent().isEmpty())
                        System.out.println("\nHand: empty");
                    else {
                        System.out.println("\nHand:\n");
                        System.out.println(ship.getHandComponent().get());
                    }
                }

                System.out.println("\nYour ship:\n");
                System.out.println(ship);

                System.out.println("\nHourglass position: ");
                System.out.println("Time left: ");
                for (PlayerData player : board.getStartingDeck())
                    System.out.println("- " + player.getUsername() + " not ready");
                for (SimpleEntry<PlayerData, Integer> entry : board.getPlayers())
                    System.out.println("- " + entry.getKey().getUsername() + " READY");

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);

                // Not ready
                if (board.getStartingDeck().contains(board.getPlayerEntityByUsername(client.getUsername()))) {
                    Chroma.println(
                            """
                                            [pick <id>]                  - pick a component
                                            [release]                    - release the picked component
                                            [reserve <id>]               - reserve the picked component
                                            [insert <x> <y>]             - inserts the component into (x,y)
                                            [move <id> <x> <y>]          - moves the component corresponding to the given id into the box with the given coordinates
                                            [rotate <id> <times>]        - rotate the selected component clockwise n-times
                                            [look-at-cards <id>]         - view specific card pile (1,2 or 3)
                                            [ready]                      - end building phase
                                    """,
                            Chroma.BLUE
                    );
                    System.out.print("> ");
                }
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

                System.out.print("\n\n\n\n\n");
                System.out.println("Cards resolved: " + (board.getCardPilePos() + 1) + "/" + board.getCardPile().size());

                System.out.print("\n\n\n\n\n");
                System.out.println("Positions: " + (board.getCardPilePos() + 1) + "/" + board.getCardPile().size());
                for (SimpleEntry<PlayerData, Integer> entry : board.getPlayers())
                    System.out.println("- " + entry.getKey().getUsername() + "at position " + entry.getValue() + " with " + entry.getKey().getCredits() + " credits");
                for (PlayerData player : board.getStartingDeck())
                    System.out.println("- " + player.getUsername() + "at starting deck with" + player.getCredits() + " credits");

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

}
