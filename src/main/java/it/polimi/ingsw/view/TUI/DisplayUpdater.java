package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.model.enums.PlayerState;

import java.util.List;

public class DisplayUpdater {
    

    public DisplayUpdater() {}

    public void updateDisplay() {
        switch (ViewTui.getClientInstance().getState()) {
            case USERNAME:
                System.out.print("Insert a username\n> ");
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
        System.out.println("✅ Lobby ID: " + ViewTui.getClientInstance().getLobby().getGameID());
        System.out.println((ViewTui.getClientInstance().getLobby().isLearnerMode() ? "🔵" : "🟣").concat(" Game Mode: ".concat(ViewTui.getClientInstance().getLobby().isLearnerMode() ? "Learner Flight" : "Standard")));
        System.out.println("👥 " + ViewTui.getClientInstance().getLobby().getPlayers().size() + "/" + ViewTui.getClientInstance().getLobby().getMaxPlayers() + " players:");

        for (String player : ViewTui.getClientInstance().getLobby().getPlayers()) {
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
        PlayerState state = ViewTui.getClientInstance().getGameController().getModel().getPlayerState(ViewTui.getClientInstance().getUsername());

        ClientBoard board = ViewTui.getClientInstance().getGameController().getModel().getBoard();
        ClientShip ship = board.getPlayerEntityByUsername(ViewTui.getClientInstance().getUsername()).getShip();

        switch (state) {

            case BUILD, LOOK_CARD_PILE -> {
                System.out.println(board.toString(ViewTui.getClientInstance().getUsername(), state));
                System.out.println(ship.toString(ViewTui.getClientInstance().getUsername(), state));
                if (state == PlayerState.LOOK_CARD_PILE)
                    System.out.println(Constants.displayCards(board.getLookedCards(), 3));

                Chroma.println(
                       "[ship <username>]            - view <username>'s ship\n" +
                            "[pick <id>]                  - pick a component\n" +
                            "[insert <id> <x> <y>]        - inserts the component into (x,y)\n" +
                            "[release <id>]               - release the picked component\n" +
                            (ViewTui.getClientInstance().getLobby().isLearnerMode() ? "" : "[reserve <id>]               - reserve the picked component\n") +
                            (ViewTui.getClientInstance().getLobby().isLearnerMode() ? "" : "[rotate-hourglass]           - rotate the hourglass\n") +
                            "[move <id> <x> <y>]          - moves the component <id> into the position (x,y) of the ship\n" +
                            "[rotate <id> <times>]        - rotate the selected component clockwise n - times\n" +
                            "[look-cards <id>]            - view specific card pile (0, 1 or 2)\n" +
                            "[ready]                      - end building phase\n" +
                            "[q]                          - go back to the main menù",
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case CHECK -> {
                System.out.println(ship.toString(ViewTui.getClientInstance().getUsername(), state));

                Chroma.println("Oh no! Your ship is not valid :(. You have to fix it before you can continue...", Chroma.RED_BOLD);
                Chroma.println(
                        """
                            ENTER a series of components <id> to remove to fix the ship
                            [<id_1> <id_2> ... <id_N>]         - remove a 'bad' component from the ship
                            [ship <username>]                  - view <username>'s ship
                            [q]                                - go back to the main menù
                        """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case WAIT_ALIEN -> {
                System.out.println(board.toString(ViewTui.getClientInstance().getUsername(), state));
                System.out.println(ship.toString(ViewTui.getClientInstance().getUsername(), state));

                Chroma.println("You might want to put aliens in your cabins!", Chroma.YELLOW_BOLD);
                Chroma.println(
                        """
                            ENTER a series of <id> cabins and alien type in which to put aliens
                            [<cabin_id_1> <ENGINE|CANNON> ... <cabin_id_N> <ENGINE|CANNON>] - add alien into the given cabins
                            [ship <username>]                                               - view <username>'s ship
                            [q]                                                             - go back to the main menù
                        """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case WAIT_SHIP_PART -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("Oh no! Your ship has broken in two or more parts :(. You have to fix it before you can continue...", Chroma.RED_BOLD);
                Chroma.println(
                        """
                            ENTER the index part you want to keep from the ship
                            [<id>]                              - index part of the ship (ex. 0, 1, ...)
                            [ship <username>]                   - view <username>'s ship
                            [q]                                 - go back to the main menù
                        """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case DRAW_CARD -> {
                this.printGameInfo(board, ship, state);

                Chroma.print("PRESS enter to draw a card... ", Chroma.BLUE);
            }

            case WAIT_CANNONS -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("You might want to activate DOUBLE cannons!", Chroma.YELLOW_BOLD);
                Chroma.println(
                        """
                                ENTER a series of cannons and battery components from which to use batteries
                                in order to activate the DOUBLE cannons you wish to activate
                                [<cannon1_id> ... <cannonN_id> - <battery1_id> ... <batteryN_id>]
                                [ship <username>]                                                   - view <username>'s ship
                                [q]                                                                 - go back to the main menù
                                """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case WAIT_ENGINES -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("You might want to activate DOUBLE engines!", Chroma.YELLOW_BOLD);
                Chroma.println(
                        """
                            ENTER a series of engines battery components from which to use batteries
                            in order to activate the DOUBLE engines you wish to activate
                            [<engine1_id> ... <engineN_id> - <battery1_id> ... <batteryN_id>]
                            [ship <username>]                                                       - view <username>'s ship
                            [q]                                                                     - go back to the main menù
                            """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case WAIT_GOODS -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("You have to add goods!", Chroma.YELLOW_BOLD);
                Chroma.println(
                        """
                            Send the new configurations of goods
                            [<cargo_id> <RED BLUE YELLOW> ...]
                            [ship <username>]                               - view <username>'s ship
                            [q]                                             - go back to the main menù
                            """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case WAIT_REMOVE_GOODS -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("You have to remove some goods...", Chroma.YELLOW_BOLD);
                Chroma.println(
                        """
                            Send the new configurations of goods in your ship and add batteries if you
                            haven't enough goods to remove
                            [<cargo_id> <RED BLUE YELLOW> ... - <battery_id> ... <batteryN_id>]
                            [ship <username>]                                                       - view <username>'s ship
                            [q]                                                                     - go back to the main menù
                            """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case WAIT_ROLL_DICES -> {
                this.printGameInfo(board, ship, state);

                System.out.print("PRESS enter to roll the dices... ");
            }

            case WAIT_REMOVE_CREW -> {
                this.printGameInfo(board, ship, state);

                Chroma.println(
                        """
                            ENTER a series of cabin components in order to remove crew/alien
                            [<cabin1_id> <cabin2_id> ... <cabinN_id>]
                            [ship <username>]                  - view <username>'s ship
                            [q]                                - go back to the main menù
                        """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case WAIT_SHIELD -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("You might want to activate shields!", Chroma.YELLOW_BOLD);
                Chroma.println(
                        """
                            ENTER a battery component from which to use a battery
                            in order to activate the shield or press enter to not activate it
                            [<battery_id>]
                            [ship <username>]                  - view <username>'s ship
                            [q]                                - go back to the main menù
                        """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case WAIT_BOOLEAN -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("You might want to take the reward!", Chroma.YELLOW_BOLD);
                Chroma.println(
                    """
                            [<true/false>]                     - take reward or not
                            [ship <username>]                  - view <username>'s ship
                            [q]                                - go back to the main menù
                        """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case WAIT_INDEX -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("You might want to land on a planet!", Chroma.YELLOW_BOLD);
                Chroma.println(
                        """
                                ENTER the index of the planet you wish to land on
                                [<id>]                             - indexes from top to bottom (ex. 0,1,...)
                                [ship <username>]                  - view <username>'s ship
                                [q]                                - go back to the main menù
                            """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case WAIT, DONE -> {
                boolean inGame = board.getStartingDeck().stream()
                        .noneMatch(p -> ViewTui.getClientInstance().getGameController().getModel().getPlayerState(p.getUsername()) != PlayerState.END)
                        && board.getPlayersByPos().stream()
                        .noneMatch(p -> ViewTui.getClientInstance().getGameController().getModel().getPlayerState(p.getUsername()) == PlayerState.WAIT_ALIEN);

                if (inGame)
                    printGameInfo(board, ship, state);

                Chroma.println("\nNOT your turn. Waiting for other players' actions...", Chroma.YELLOW_BOLD);
                Chroma.println(
                        (!inGame && !ViewTui.getClientInstance().getLobby().isLearnerMode() ? "[rotate-hourglass]                 - rotate the hourglass\n" : "") +
                            "[ship <username>]                  - view <username>'s ship\n" +
                            "[q]                                - go back to the main menù",
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

            case END -> {
                this.printGameInfo(board, ship, state);

                Chroma.println("\nGAME FINISHED", Chroma.GREEN_BOLD);
                Chroma.println(
                        """
                            [ship <username>]                  - view <username>'s ship
                            [q]                               - go back to the main menù
                            """,
                        Chroma.BLUE
                );
                System.out.print("> ");
            }

        }
    }

    private void printGameInfo(ClientBoard board, ClientShip ship, PlayerState state) {
        System.out.println(board.toString(ViewTui.getClientInstance().getUsername(), state));
        System.out.println(ship.toString(ViewTui.getClientInstance().getUsername(), state));

        // TODO forse getAllPlayers()?
        if (board.getPlayers().stream()
                .noneMatch(e -> ViewTui.getClientInstance().getGameController().getModel().getPlayerState(e.getKey().getUsername()) == PlayerState.DRAW_CARD || ViewTui.getClientInstance().getGameController().getModel().getPlayerState(e.getKey().getUsername()) == PlayerState.END)) {
            if (board.getCardPile().size() > 1) {
                Chroma.println("\nPrevious card\tActual card", Chroma.GREY_BOLD);
                System.out.println(Constants.displayCards(List.of(board.getCardPile().get(board.getCardPile().size()-2), board.getCardPile().getLast()), 2));
            }
            else {
                Chroma.println("\nActual card", Chroma.GREY_BOLD);
                System.out.println(Constants.displayCards(List.of(board.getCardPile().getLast()), 1));
            }
            board.getCardPile().getLast().printCardInfo(ViewTui.getClientInstance().getGameController().getModel(), board);
        }
        else if (!board.getCardPile().isEmpty()) {
            Chroma.println("Previous card", Chroma.GREY_BOLD);
            System.out.println(board.getCardPile().getLast());
        }

    }

}
