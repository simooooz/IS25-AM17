package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.network.Client;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.CountDownLatch;

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


    private String displayComponents(List<Component> components, int componentsPerRow) {
        StringBuilder output = new StringBuilder();

        Collections.shuffle(components);

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

        // Handle wait state
        if (state == PlayerState.WAIT)
            client.getViewTui().waitLatch = new CountDownLatch(1);
        else
            client.getViewTui().waitLatch.countDown();

        switch (state) {

            case BUILD -> {
                // is player ready?
                boolean isReady = board.getStartingDeck().contains(board.getPlayerEntityByUsername(client.getUsername()));
                if (!isReady) {
                    List<Component> commonComponents = board.getCommonComponents();
                    System.out.println(displayComponents(commonComponents, 10));

                    Chroma.println("\nreserves: " + (
                            ship.getReserves().isEmpty()
                                    ? "none"
                                    : displayComponents(ship.getReserves(), 2)
                    ), Chroma.GREY_BOLD);
                }

                Chroma.println("\nYOUR ship:\n", Chroma.YELLOW_BOLD);
                System.out.println(ship);

                if (!isReady) {
                    System.out.println("\nyour hand: " + (
                            ship.getHandComponent().isEmpty()
                                    ? "empty"
                                    : ship.getHandComponent()
                    ));
                }

                // todo: hourglass
                System.out.println("\nHourglass position: ");
                System.out.println("Time left: ");

                for (PlayerData player : board.getStartingDeck())
                    System.out.println("- " + player.getUsername() + Chroma.color(" not ready", Chroma.RED));
                for (SimpleEntry<PlayerData, Integer> entry : board.getPlayers())
                    System.out.println("- " + entry.getKey().getUsername() + Chroma.color(" READY", Chroma.GREEN));

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);

                if (!isReady) {
                    Chroma.println(
                            "[pick <id>]                  - pick a component\n" +
                                    "[insert <id> <x> <y>]        - inserts the component into (x,y)\n" +
                                    "[release]                    - release the picked component\n" +
                                    (client.getGameController().getModel().isLearnerMode() ? "" : "[reserve <id>]               - reserve the picked component\n") +

                                    "[move <id> <x> <y>]          - moves the component <id> into the position (x,y) of the ship\n" +
                                    "[rotate <id> <times>]        - rotate the selected component clockwise n - times\n" +
                                    "[look-at-cards <id>]         - view specific card pile (1, 2 or 3)\n" +
                                    "[ready]                      - end building phase",
                            Chroma.BLUE
                    );
                    System.out.print("> ");
                }
            }

            case CHECK -> {
                System.out.println(ship);
                Chroma.println("Oh no! Your ship is not valid :(. You have to fix it before you can continue...", Chroma.RED_BOLD);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                System.out.println(
                        """
                                ENTER a series of components <id> to remove to fix the ship
                                [<id> <id> ... <id>]               - remove a 'bad' component from the ship]
                                """
                );
                System.out.print("> ");
            }

            case WAIT_ALIEN -> {
                System.out.println(ship);
                Chroma.println("You might want to put aliens in your cabins!", Chroma.YELLOW_BOLD);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                System.out.println(
                        """
                                ENTER a series of <id> cabins in which to put aliens
                                [<id> <id> ... <id>]               - add alien into the given cabins]
                                """
                );
                System.out.print("> ");
            }

            case DRAW_CARD -> {
                gameInfo(board, ship);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                System.out.println("PRESS enter to draw a card...");
            }

            case WAIT_CANNONS -> {
                gameInfo(board, ship);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println("You might want to activate DOUBLE cannons!", Chroma.YELLOW_BOLD);
                System.out.println(
                        """
                                ENTER a series of battery components from which to use batteries
                                (a component put in multiple times uses multiple batteries)
                                in order to activate the DOUBLE cannons you wish to activate
                                (a single battery activates a cannon!)
                                [<battery1_id> ... <batteryN_id> | <cannon1_id> ... <cannonN_id>]
                                """
                );
                System.out.print("> ");
            }

            case WAIT_ENGINES -> {
                gameInfo(board, ship);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println("You might want to activate DOUBLE engines!", Chroma.YELLOW_BOLD);
                System.out.println(
                        """
                                ENTER a series of battery components from which to use batteries
                                (a component put in multiple times uses multiple batteries)
                                in order to activate the DOUBLE engines you wish to activate
                                (a single battery activates a engine!)
                                [<battery1_id> ... <batteryN_id> | <engine1_id> ... <engineN_id>]
                                """
                );
                System.out.print("> ");
            }

            case WAIT_GOODS -> {
                // todo
            }

            case WAIT_REMOVE_GOODS -> {
                // todo
            }

            case WAIT_ROLL_DICES -> {
                gameInfo(board, ship);

                System.out.println("PRESS enter to roll the dices...");
            }

            case WAIT_REMOVE_CREW -> {
                gameInfo(board, ship);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                System.out.println(
                        """
                                ENTER a series of cabin components in order to remove crew/alien
                                [<cabin1_id> <cabin2_id> ... <cabinN_id>]
                                """
                );
                System.out.print("> ");
            }

            case WAIT_SHIELD -> {
                gameInfo(board, ship);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println("You might want to activate shields!", Chroma.YELLOW_BOLD);
                System.out.println(
                        """
                                ENTER a battery component from which to use a battery
                                in order to activate the shield you wish to activate
                                [<battery_id> <shield_id>]
                                """
                );
                System.out.print("> ");
            }

            case WAIT_BOOLEAN -> {
                gameInfo(board, ship);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println("You might want to take the reward!", Chroma.YELLOW_BOLD);
                System.out.println("true/fase?");
                System.out.print("> ");
            }

            case WAIT_INDEX -> {
                gameInfo(board, ship);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println("You might want to land on a planet!", Chroma.YELLOW_BOLD);
                System.out.println(
                        """
                                ENTER the index of the planet you wish to land on
                                (indexes from top to bottom. For example: 0,1,...,4)
                                """
                );
                System.out.print("> ");
            }

            case WAIT -> Chroma.println("\nNOT your turn. Waiting for other players' actions...", Chroma.YELLOW_BOLD);

        }
    }

    private void gameInfo(Board board, Ship ship) {
        board.getPlayers().stream()
                .filter(e -> !e.getKey().getUsername().equals(client.getUsername()))
                .forEach(e -> {
                    Chroma.println(e.getKey().getUsername() + "'s ship:\n", Chroma.YELLOW);
                    System.out.println(e.getKey().getShip());
                });

        Chroma.println("\nYOUR ship:\n", Chroma.YELLOW_BOLD);
        System.out.println(ship);

        System.out.println(board);

        if (board.getPlayers().stream()
                .noneMatch(e -> client.getGameController().getModel().getPlayerState(e.getKey().getUsername()) == PlayerState.DRAW_CARD)) {
            System.out.println(board.getCardPile().get(board.getCardPilePos()));
        }
    }

}
