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
                boolean hasMessages = false;
                String message;

                while ((message = client.getViewTui().getNetworkMessageQueue().poll()) != null) {
                    if (!message.equals("ERROR"))
                        hasMessages = true;
                }

                if (hasMessages) {
                    client.getViewTui().clear();
                    updateDisplay();
                    System.out.flush();
                }

                Thread.sleep(500);
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

    private void displayGame() {
        PlayerState state = client.getGameController().getState(client.getUsername());

        Board board = client.getGameController().getModel().getBoard();
        Ship ship = board.getPlayerEntityByUsername(client.getUsername()).getShip();

        // Handle wait state
        // todo also end, done?
        if (state == PlayerState.WAIT)
            client.getViewTui().waitLatch = new CountDownLatch(1);
        else
            client.getViewTui().waitLatch.countDown();

        switch (state) {

            case BUILD, LOOK_CARD_PILE -> {
                System.out.println(board.toString(client.getUsername(), state));
                System.out.println(ship.toString(client.getUsername(), state));

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println(
                        "[pick <id>]                  - pick a component\n" +
                                "[insert <id> <x> <y>]        - inserts the component into (x,y)\n" +
                                "[release <id>]               - release the picked component\n" +
                                (client.getGameController().getModel().isLearnerMode() ? "" : "[reserve <id>]               - reserve the picked component\n") +
                                "[move <id> <x> <y>]          - moves the component <id> into the position (x,y) of the ship\n" +
                                "[rotate <id> <times>]        - rotate the selected component clockwise n - times\n" +
                                "[look-cards <id>]            - view specific card pile (0, 1 or 2)\n" +
                                "[ready]                      - end building phase",
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case CHECK -> {
                System.out.println(ship.toString(client.getUsername(), state));
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
                System.out.println(ship.toString(client.getUsername(), state));
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
                this.printGameInfo(board, ship, state);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                System.out.println("PRESS enter to draw a card...");
            }

            case WAIT_CANNONS -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println("You might want to activate DOUBLE cannons!", Chroma.YELLOW_BOLD);
                System.out.println(
                        """
                                ENTER a series of battery components from which to use batteries
                                (a component put in multiple times uses multiple batteries)
                                in order to activate the DOUBLE cannons you wish to activate
                                (a single battery activates a cannon!)
                                [<cannon1_id> ... <cannonN_id> - <battery1_id> ... <batteryN_id>]
                                """
                );
                System.out.print("> ");
            }

            case WAIT_ENGINES -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println("You might want to activate DOUBLE engines!", Chroma.YELLOW_BOLD);
                System.out.println(
                        """
                            ENTER a series of battery components from which to use batteries
                            (a component put in multiple times uses multiple batteries)
                            in order to activate the DOUBLE engines you wish to activate
                            (a single battery activates a engine!)
                            [<engine1_id> ... <engineN_id> - <battery1_id> ... <batteryN_id>]
                            """
                );
                System.out.print("> ");
            }

            case WAIT_GOODS -> {
                this.printGameInfo(board, ship, state);

                System.out.println("Invia la nuova configurazione di merci");
                System.out.println("<cargo id> <RED BLUE YELLOW> ...");
                System.out.print("> ");
            }

            case WAIT_REMOVE_GOODS -> {
                this.printGameInfo(board, ship, state);

                System.out.println("Devi rimuovere delle merci, invia la nuova configurazione");
                System.out.println("<cargo id> <RED BLUE YELLOW> - <battery id>");
                System.out.print("> ");
            }

            case WAIT_ROLL_DICES -> {
                this.printGameInfo(board, ship, state);
                System.out.println("PRESS enter to roll the dices...");
            }

            case WAIT_REMOVE_CREW -> {
                this.printGameInfo(board, ship, state);

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
                this.printGameInfo(board, ship, state);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println("You might want to activate shields!", Chroma.YELLOW_BOLD);
                System.out.println(
                        """
                            ENTER a battery component from which to use a battery
                            in order to activate the shield or press enter to not activate it
                            [<battery_id>]
                        """
                );
                System.out.print("> ");
            }

            case WAIT_BOOLEAN -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("\n\npress 'q' to go back to the menu", Chroma.GREY_BOLD);
                Chroma.println("You might want to take the reward!", Chroma.YELLOW_BOLD);
                System.out.println("true/false?");
                System.out.print("> ");
            }

            case WAIT_INDEX -> {
                this.printGameInfo(board, ship, state);

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

            case WAIT, DONE -> {
                boolean inGame = board.getStartingDeck().stream()
                        .noneMatch(p -> client.getGameController().getModel().getPlayerState(p.getUsername()) != PlayerState.END)
                        && board.getPlayersByPos().stream()
                        .noneMatch(p -> client.getGameController().getModel().getPlayerState(p.getUsername()) == PlayerState.WAIT_ALIEN);

                if (inGame)
                    printGameInfo(board, ship, state);

                Chroma.println("\nNOT your turn. Waiting for other players' actions...", Chroma.YELLOW_BOLD);
            }

            case END -> {
                Chroma.println("GAME FINISHED", Chroma.GREEN_BOLD);

                if (board.getCardPilePos() > 0) {
                    Chroma.println("Previous card", Chroma.GREY_BOLD);
                    System.out.println(board.getCardPile().get(board.getCardPilePos()-1));
                }

                System.out.println(board.toString(client.getUsername(), state));
            }

        }
    }

    private void printGameInfo(Board board, Ship ship, PlayerState state) {
        if (board.getCardPilePos() > 0) {
            Chroma.println("Previous card", Chroma.GREY_BOLD);
            System.out.println(board.getCardPile().get(board.getCardPilePos()-1));
        }

        if (board.getPlayers().stream()
                .noneMatch(e -> client.getGameController().getModel().getPlayerState(e.getKey().getUsername()) == PlayerState.DRAW_CARD)) {
            Chroma.println("\nActual card", Chroma.GREY_BOLD);
            System.out.println(board.getCardPile().get(board.getCardPilePos()));
            board.getCardPile().get(board.getCardPilePos()).printCardInfo(client.getGameController().getModel(), board);
        }

        System.out.println(board.toString(client.getUsername(), state));
        System.out.println(ship.toString(client.getUsername(), state));
    }

}
