package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.client.model.events.CardPileLookedEvent;
import it.polimi.ingsw.client.model.events.CardRevealedEvent;
import it.polimi.ingsw.client.model.events.CardUpdatedEvent;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.common.model.events.game.*;
import it.polimi.ingsw.common.model.events.lobby.CreatedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.JoinedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.UsernameOkEvent;
import it.polimi.ingsw.model.exceptions.PlayerNotFoundException;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.rmi.RMIClient;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.view.UserInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.IntStream;

public class ViewTui implements UserInterface {

    private static Client client;
    private final BufferedReader reader;

    private final DisplayUpdater displayUpdater;
    private String localCommand = "";

    public ViewTui() {
        ClientEventBus.getInstance().subscribe(this);
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.displayUpdater = new DisplayUpdater();
    }

    @Override
    public void onEvent(List<GameEvent> events) {
        boolean toUpdate = false;
        for (GameEvent event : events) {
            if (toUpdate) break;
            switch (event) {
                case ComponentPickedEvent _, ComponentReleasedEvent _, SyncAllEvent _, MatchStartedEvent _, HourglassMovedEvent _, CardRevealedEvent _,
                     CardUpdatedEvent _, FlightEndedEvent _, CreatedLobbyEvent _, JoinedLobbyEvent _, LeftLobbyEvent _,
                     UsernameOkEvent _, GameErrorEvent _, PlayersStateUpdatedEvent _, PlayersPositionUpdatedEvent _, CreditsUpdatedEvent _ -> toUpdate = true;
                case ComponentRotatedEvent e -> toUpdate = isToUpdateByComponentId(e.id());
                case BatteriesUpdatedEvent e -> toUpdate = isToUpdateByComponentId(e.id());
                case CrewUpdatedEvent e -> toUpdate = isToUpdateByComponentId(e.id());
                case GoodsUpdatedEvent e -> toUpdate = isToUpdateByComponentId(e.id());
                case ComponentInsertedEvent e -> toUpdate = e.username().equals(client.getUsername());
                case ComponentMovedEvent e -> toUpdate = e.username().equals(client.getUsername());
                case ComponentReservedEvent e -> toUpdate = e.username().equals(client.getUsername());
                case ComponentDestroyedEvent e -> toUpdate = e.username().equals(client.getUsername());
                case ShipBrokenEvent e -> toUpdate = e.username().equals(client.getUsername());
                case CardPileLookedEvent e -> {
                    if (e.username().equals(client.getUsername())) {
                        toUpdate = true;
                        displayUpdater.getLookedCards().clear();
                        displayUpdater.getLookedCards().addAll(e.cards());
                    }
                }
                case CardPileReleasedEvent e -> {
                    if (e.username().equals(client.getUsername())) {
                        toUpdate = true;
                        displayUpdater.getLookedCards().clear();
                    }
                }
                default -> {}
            }
        }

        if (toUpdate) {
            scheduleUpdate();
            events.stream().filter(e -> e.eventType() == MessageType.ERROR).findFirst().ifPresent(e -> displayError((String) e.getArgs()[0]));
        }
    }

    private boolean isToUpdateByComponentId(Integer id) {
        boolean toUpdate;
        ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(id);
        ClientShip ship = client.getGameController().getModel().getBoard().getPlayerEntityByUsername(client.getUsername()).getShip();
        Optional<ClientComponent> shipC = ship.getDashboard(component.getY(), component.getX());
        toUpdate = (shipC.isPresent() && shipC.get().equals(component)) || (ship.getComponentInHand().isPresent() && ship.getComponentInHand().get().equals(component)) || ship.getReserves().contains(component);
        return toUpdate;
    }

    private void scheduleUpdate() {
        clear();
        displayUpdater.updateDisplay();
        System.out.flush();
    }

    private void processUserInput(String input) {
        try {
            switch (client.getState()) {
                case USERNAME:
                    if (input.length() < 3 || input.length() > 18)
                        throw new IllegalArgumentException("Username length must be between 3 and 18.");
                    client.send(MessageType.SET_USERNAME, input);
                    break;

                case LOBBY_SELECTION:
                    switch (input) {
                        case "1" -> handleCreateLobby();
                        case "2" -> handleJoinLobby();
                        case "3" -> handleJoinRandomLobby();
                        case "q" -> handleDisconnect();
                        default -> throw new IllegalArgumentException("Command not valid. Please try again.");
                    }
                    break;

                case IN_LOBBY:
                    handleInLobby(input);
                    break;

                case IN_GAME:
                    handleInGame(input);
                    break;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            Chroma.println("Command not valid. Please try again.", Chroma.RED);
            System.out.print("> ");
        } catch (PlayerNotFoundException e) {
            Chroma.println("Player not found. Please try again.", Chroma.RED);
            System.out.print("> ");
        } catch (IllegalArgumentException e) {
            Chroma.println(e.getMessage(), Chroma.RED);
            System.out.print("> ");
        }
    }

    /**
     * Handles the lobby creation process.
     */
    private void handleCreateLobby() {
        clear();
        Chroma.println("press 'q' to go back to the menu", Chroma.GREY_BOLD);

        String lobbyName = InputUtility.requestString("Insert lobby name\n> ", true, 3, 18);
        if (lobbyName == null) {
            scheduleUpdate();
            return;
        }
        Integer maxPlayers = InputUtility.requestInt("Insert the number of players (2-4)\n> ", true, 2, 4);
        if (maxPlayers == null) {
            scheduleUpdate();
            return;
        }
        Boolean learnerFlight = InputUtility.requestBoolean("Learner flight? (true/false)\n> ", true);
        if (learnerFlight == null) {
            scheduleUpdate();
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

        String lobbyName = InputUtility.requestString("Insert lobby name\n> ", true, 3, 18);
        if (lobbyName == null) {
            scheduleUpdate();
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

        Boolean learnerFlight = InputUtility.requestBoolean("Learner flight? (true/false)\n> ", true);
        if (learnerFlight == null) {
            scheduleUpdate();
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
            boolean sure = InputUtility.requestBoolean("You sure? (y/n)\n> ", false);
            if (sure)
                client.send(MessageType.LEAVE_GAME);
        }
    }

    /**
     * Handles various in-game actions based on the player's current state.
     *
     * @param input provided by the user to influence the game logic
     */
    @SuppressWarnings("Duplicates")
    private void handleInGame(String input) {
        PlayerState state = client.getGameController().getModel().getPlayerState(client.getUsername());

        if (input.equals("q")) {
            boolean sure = InputUtility.requestBoolean("You sure? (y/n)\n> ", false);
            if (sure)
                client.send(MessageType.LEAVE_GAME);
            else
                System.out.print("> ");
            return;
        }
        else if (input.split(" ").length > 1 && input.split(" ")[0].equals("ship")) {
            String username = input.split(" ")[1];
            ClientPlayer player = client.getGameController().getModel().getBoard().getPlayerEntityByUsername(username);
            clear();
            if (!player.getUsername().equals(client.getUsername())) {
                StringBuilder sb = new StringBuilder();
                sb.append(Chroma.color(username + "'s ship:\n", Chroma.YELLOW_BOLD));
                player.getShip().printShip(sb);
                sb.append("\n\n");
                System.out.println(sb);
            }
            displayUpdater.updateDisplay();
            return;
        }
        else if (input.equals("end-flight")) {
            client.send(MessageType.END_FLIGHT);
            return;
        }

        switch (state) {
            case BUILD, LOOK_CARD_PILE -> {
                String[] commands = input.trim().split(" ");
                switch (commands[0]) {
                    case "pick" -> {
                        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
                            client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
                        revertRotation();

                        localCommand = "";
                        client.send(MessageType.PICK_COMPONENT, Integer.parseInt(commands[1]));
                    }
                    case "release" -> {
                        revertRotation();
                        localCommand = "";
                        client.send(MessageType.RELEASE_COMPONENT, Integer.parseInt(commands[1]));
                    }
                    case "reserve" -> {
                        revertRotation();
                        localCommand = "";
                        client.send(MessageType.RESERVE_COMPONENT, Integer.parseInt(commands[1]));
                    }
                    case "insert" -> {
                        // Do insert locally
                        try {
                            int row = Integer.parseInt(commands[2]) - 5;
                            int col = Integer.parseInt(commands[3]) - 4;
                            input = String.join(" ", "insert", commands[1], String.valueOf(row), String.valueOf(col));
                            client.getGameController().insertComponent(client.getUsername(), Integer.parseInt(commands[1]), row, col, 0);

                            if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("rotate")) // Previous local command was "rotate"
                                localCommand = String.join(" ", "insert", commands[1], String.valueOf(row), String.valueOf(col), localCommand.split(" ")[2]);
                            else if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) { // Previous local command was "insert" (ex. of a reserve)
                                client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
                                localCommand = input + " 0";
                            } else // No previous local command
                                localCommand = input + " 0";

                        } catch (RuntimeException e) {
                            // Propagate general exceptions
                            throw new IllegalArgumentException(e.getMessage());
                        }
                    }
                    case "move" -> {
                        // Do move locally
                        try {
                            int row = Integer.parseInt(commands[2]) - 5;
                            int col = Integer.parseInt(commands[3]) - 4;
                            client.getGameController().moveComponent(client.getUsername(), Integer.parseInt(commands[1]), row, col, 0);

                            if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
                                localCommand = String.join(" ", localCommand.split(" ")[0], localCommand.split(" ")[1], String.valueOf(row), String.valueOf(col), localCommand.split(" ")[4]);
                            else // No previous local command
                                localCommand = input + " 0";

                        } catch (RuntimeException e) {
                            // Propagate general exceptions
                            throw new IllegalArgumentException(e.getMessage());
                        }
                    }
                    case "rotate" -> {
                        // Do rotation locally
                        try {
                            int times = Integer.parseInt(commands[2]) % 4;
                            client.getGameController().rotateComponent(client.getUsername(), Integer.parseInt(commands[1]), times);

                            if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("rotate")) // Previous local command was "rotate"
                                localCommand = String.join(" ", localCommand.split(" ")[0], localCommand.split(" ")[1], String.valueOf((times + Integer.parseInt(localCommand.split(" ")[2])) % 4));
                            else if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
                                localCommand = String.join(" ", localCommand.split(" ")[0], localCommand.split(" ")[1], localCommand.split(" ")[2], localCommand.split(" ")[3], String.valueOf((Integer.parseInt(localCommand.split(" ")[4]) + times) % 4));
                            else // No previous local command
                                localCommand = input;

                        } catch (RuntimeException e) {
                            // Propagate general exceptions
                            throw new IllegalArgumentException(e.getMessage());
                        }
                    }
                    case "look-cards" -> {
                        int deckIndex = Integer.parseInt(commands[1]);
                        if (deckIndex < 0 || deckIndex > 2)
                            throw new IllegalArgumentException("Deck index not valid");

                        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) { // Previous local command was "insert", otherwise don't change it
                            client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
                            localCommand = "";
                        }

                        client.send(MessageType.LOOK_CARD_PILE, deckIndex);
                    }
                    case "rotate-hourglass" -> client.send(MessageType.MOVE_HOURGLASS);
                    case "ready" -> {
                        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
                            client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
                        revertRotation();

                        localCommand = "";
                        client.send(MessageType.SET_READY);
                    }
                    default -> throw new IllegalArgumentException("Command not valid. Please try again.");
                }
            }
            case CHECK, WAIT_REMOVE_CREW -> {
                List<Integer> ids = Arrays.stream(input.split(" "))
                        .map(Integer::parseInt)
                        .toList();

                if (state == PlayerState.CHECK)
                    client.send(MessageType.CHECK_SHIP, ids);
                else
                    client.send(MessageType.REMOVE_CREW, ids);
            }
            case WAIT_ALIEN -> {
                Map<Integer, AlienType> alienMap = new HashMap<>();
                if (!input.isBlank()) {
                    String[] commands = input.trim().split(" ");
                    Integer[] ids = IntStream.range(0, commands.length)
                            .filter(i -> i % 2 == 0)
                            .mapToObj(i -> Integer.parseInt(commands[i]))
                            .toArray(Integer[]::new);
                    AlienType[] aliens = IntStream.range(0, commands.length)
                            .filter(i -> i % 2 == 1)
                            .mapToObj(i -> {
                                if (commands[i].equalsIgnoreCase("cannon"))
                                    return AlienType.CANNON;
                                else if (commands[i].equalsIgnoreCase("engine"))
                                    return AlienType.ENGINE;
                                throw new IllegalArgumentException("Invalid alien type. Correct types are cannon and engine.");
                            })
                            .toArray(AlienType[]::new);

                    if (ids.length != aliens.length)
                        throw new IllegalArgumentException("Command not valid. Please try again.");

                    for (int i = 0; i < ids.length; i++)
                        alienMap.put(ids[i], aliens[i]);
                }
                client.send(MessageType.CHOOSE_ALIEN, alienMap);
            }
            case WAIT_SHIP_PART -> client.send(MessageType.CHOOSE_SHIP_PART, Integer.parseInt(input));
            case DRAW_CARD -> client.send(MessageType.DRAW_CARD);
            case WAIT_CANNONS, WAIT_ENGINES -> {
                List<Integer> cannonComponentsIds = new ArrayList<>();
                List<Integer> batteriesIds = new ArrayList<>();
                if (!input.isBlank()) {
                    String[] parts = input.trim().split("-");
                    String[] ids;

                    if (parts.length > 0 && !parts[0].isBlank()) {
                        ids = parts[0].trim().split(" ");
                        cannonComponentsIds = Arrays.stream(ids)
                                .map(Integer::parseInt)
                                .toList();
                    }
                    if (parts.length > 1 && !parts[1].isBlank()) {
                        ids = parts[1].trim().split(" ");
                        batteriesIds = Arrays.stream(ids)
                                .map(Integer::parseInt)
                                .toList();
                    }
                }

                if (cannonComponentsIds.size() != batteriesIds.size())
                    throw new IllegalArgumentException("Inconsistent number of batteries");

                if (state == PlayerState.WAIT_CANNONS)
                    client.send(MessageType.ACTIVATE_CANNONS, batteriesIds, cannonComponentsIds);
                else
                    client.send(MessageType.ACTIVATE_ENGINES, batteriesIds, cannonComponentsIds);
            }
            case WAIT_GOODS, WAIT_REMOVE_GOODS -> {
                Map<Integer, List<ColorType>> newDisposition = new HashMap<>();
                List<Integer> batteriesIds = new ArrayList<>();
                if (!input.isBlank()) {
                    String[] parts = input.trim().split("-");

                    if (parts.length > 0 && !parts[0].isBlank()) {
                        String[] firstCommandList = parts[0].trim().split(" ");
                        List<String> colors = Arrays.stream(ColorType.values()).map(c -> c.name().toUpperCase()).toList();
                        Integer currentId = null;
                        for (String value : firstCommandList) {
                            if (colors.contains(value.toUpperCase()) && newDisposition.containsKey(currentId))
                                newDisposition.get(currentId).add(ColorType.valueOf(value.toUpperCase()));
                            else {
                                currentId = Integer.parseInt(value);
                                newDisposition.put(currentId, new ArrayList<>());
                            }
                        }
                    }

                    if (state == PlayerState.WAIT_REMOVE_GOODS && parts.length > 1 && !parts[1].isBlank()) {
                        batteriesIds = Arrays.stream(parts[1].trim().split(" "))
                                .map(Integer::parseInt)
                                .toList();
                    }
                }
                client.send(MessageType.UPDATE_GOODS, newDisposition, batteriesIds);
            }
            case WAIT_ROLL_DICES -> client.send(MessageType.ROLL_DICES);
            case WAIT_SHIELD -> {
                Integer id = null;
                if (!input.isBlank())
                    id = Integer.parseInt(input);
                client.send(MessageType.ACTIVATE_SHIELD, id);
            }
            case WAIT_BOOLEAN -> {
                boolean value;
                if (input.trim().equalsIgnoreCase("true"))
                    value = true;
                else if (input.trim().equalsIgnoreCase("false"))
                    value = false;
                else
                    throw new IllegalArgumentException("Invalid boolean value. Correct values are true or false.");
                client.send(MessageType.GET_BOOLEAN, value);
            }
            case WAIT_INDEX -> {
                Integer index = null;
                if (!input.isBlank())
                    index = Integer.parseInt(input);
                client.send(MessageType.GET_INDEX, index);
            }
            case WAIT, DONE -> {
                String[] commands = input.trim().split(" ");
                switch (commands[0]) {
                    case "rotate-hourglass" -> client.send(MessageType.MOVE_HOURGLASS);
                    case "ship" -> {}
                    default -> throw new IllegalArgumentException("Command not valid. Please try again.");
                }
            }
        }

    }

    @SuppressWarnings("Duplicates")
    private void revertRotation() {
        if (localCommand.split(" ").length > 0 && (localCommand.split(" ")[0].equals("rotate") || localCommand.split(" ")[0].equals("insert"))) { // Previous local command was "rotate" or "insert", revert it
            try {
                int times = 4 - (localCommand.split(" ")[0].equals("rotate") ? (Integer.parseInt(localCommand.split(" ")[2]) % 4) : (Integer.parseInt(localCommand.split(" ")[4]) % 4));
                client.getGameController().componentRotated(Integer.parseInt(localCommand.split(" ")[1]), times);
            } catch (RuntimeException e) {
                // Propagate general exceptions
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    @Override
    public void clear() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            // for mac
            System.out.println("\033\143");
        }
    }

    @Override
    public void displayError(String message) {
        Chroma.println(message, Chroma.RED);
        System.out.print("> ");
        System.out.flush();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
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

        try {
            System.out.print("Press ENTER to continue...");
            reader.readLine();

            int clientType = InputUtility.requestInt("Press 1 to choose socket client or 2 for RMI: ", false, 1, 2);

            if (clientType == 1)
                client = new ClientSocket(this);
            else if (clientType == 2)
                client = new RMIClient(this);
            else
                System.exit(-1);

            displayUpdater.updateDisplay();

            while (true) {
                System.out.flush();
                String input = reader.readLine();
                processUserInput(input);
            }

        } catch (IOException e) {
            handleDisconnect();
        }

    }

    public void handleDisconnect() {
        Chroma.println("Bye!", Chroma.YELLOW_BOLD);

        if (reader != null) {
            try {
                this.reader.close();
            } catch (IOException e) {
                // Do nothing
            }
        }
        client.closeConnection();
        System.exit(0);
    }

    public static Client getClientInstance() {
        return client;
    }

}
