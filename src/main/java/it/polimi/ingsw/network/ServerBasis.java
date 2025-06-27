package it.polimi.ingsw.network;

import it.polimi.ingsw.common.model.events.game.ErrorEvent;
import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.common.model.events.lobby.UsernameOkEvent;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.common.model.enums.LobbyState;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.network.exceptions.UserNotFoundException;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract class providing core server functionality.
 * Contains all static methods for handling player operations, from lobby management
 * to specific game actions.
 */
public abstract class ServerBasis {

    /**
     * Sets the username for a user and handles rejoining to previous active session
     * if any (resilience to disconnections).
     *
     * @param user     the user for whom to set the username
     * @param username the username to set
     * @throws IllegalStateException if the user is not in USERNAME state
     */
    public static void setUsername(User user, String username) {
        if (user.getState() != UserState.USERNAME) throw new IllegalStateException("User is not in state USERNAME");
        List<Event> events = new ArrayList<>();

        try {
            user.setUsername(username);
            user.setState(UserState.LOBBY_SELECTION);
            events.add(new UsernameOkEvent(username));
        } catch (IllegalArgumentException e) {
           user.sendEvent(new ErrorEvent(e.getMessage()));
           return;
        }

        try {
            // Check previous sessions
            User oldUser = User.popInactiveUser(username);
            if (oldUser != null && oldUser.getLobby() != null && oldUser.getLobby().getState() == LobbyState.IN_GAME) { // Rejoin
                List<Event> rejoinEvents = MatchController.getInstance().rejoinGame(username, oldUser.getLobby().getGameID());
                if (!rejoinEvents.isEmpty()) {
                    events.addAll(rejoinEvents);
                    user.setLobby(oldUser.getLobby());
                    user.setState(UserState.IN_GAME);
                }
            }
        } catch (RuntimeException e) {
            events.add(new ErrorEvent(e.getMessage()));
        }

        user.notifyEvents(events);
    }

    /**
     * Creates a new lobby with the specified parameters.
     *
     * @param user        the user who wants to create the lobby
     * @param name        the name of the lobby to create
     * @param maxPlayers  the maximum number of players in the lobby
     * @param learnerMode true if the lobby is in learner mode
     * @throws IllegalStateException    if the user is not in LOBBY_SELECTION state
     */
    public static void createLobby(User user, String name, Integer maxPlayers, Boolean learnerMode) {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");

        List<Event> events = MatchController.getInstance().createNewGame(user.getUsername(), maxPlayers, name, learnerMode);
        if (notContainsError(events)) {
            user.setLobby(MatchController.getInstance().getLobby(user.getUsername()));
            user.setState(UserState.IN_LOBBY);
        }
        user.notifyEvents(events);
    }

    /**
     * Allows a user to join a specific lobby by name.
     *
     * @param user      the user who wants to join the lobby
     * @param lobbyName the name of the lobby to join
     * @throws IllegalStateException    if the user is not in LOBBY_SELECTION state
     */
    public static void joinLobby(User user, String lobbyName) {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");

        List<Event> events = MatchController.getInstance().joinGame(user.getUsername(), lobbyName);
        if (notContainsError(events))
            joinCommon(user, events);

        user.notifyEvents(events);
    }

    /**
     * Allows a user to join a random lobby with the specified mode.
     *
     * @param user        the user who wants to join a random lobby
     * @param learnerMode true to join lobbies in learner mode
     * @throws IllegalStateException    if the user is not in LOBBY_SELECTION state
     */
    public static void joinRandomLobby(User user, Boolean learnerMode) {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");

        List<Event> events = MatchController.getInstance().joinRandomGame(user.getUsername(), learnerMode);
        if (notContainsError(events))
            joinCommon(user, events);

        user.notifyEvents(events);
    }

    private static void joinCommon(User user, List<Event> events) {
        Lobby lobby = MatchController.getInstance().getLobby(user.getUsername());
        user.setLobby(lobby);
        user.setState(UserState.IN_LOBBY);

        if (lobby.getState() == LobbyState.IN_GAME) {
            List<String> players = new ArrayList<>(lobby.getPlayers());
            try {
                for (String username : players) { // Set GameController for each user and update state
                    User userP = User.getUser(username);
                    userP.setState(UserState.IN_GAME);
                }
            } catch (UserNotFoundException e) {
                throw new RuntimeException("Error while creating game");
            }

            switch (user.getLobby().getGameID()) { // Test only
                case "test-1" -> events.addAll(user.getGameController().startTest(1));
                case "test-2" -> events.addAll(user.getGameController().startTest(2));
            }
        }
    }

    /**
     * Allows a user to leave the current game or lobby.
     *
     * @param user the user who wants to leave
     * @throws IllegalStateException if the user is not in a lobby or game
     */
    public static void leaveGame(User user) {
        if (user.getState() != UserState.IN_GAME && user.getState() != UserState.IN_LOBBY) throw new IllegalStateException("User is not in lobby or game");

        List<Event> events = MatchController.getInstance().leaveGame(user.getUsername());
        events.stream().filter(e -> e.eventType().equals(MessageType.LEFT_LOBBY_EVENT)).findFirst().ifPresent(e -> e.getTargetPlayers().add(user.getUsername()));

        user.notifyEvents(events);
        if (notContainsError(events)) {
            user.setState(UserState.LOBBY_SELECTION);
            user.setLobby(null);
        }
    }

    /**
     * Allows a user to pick a component during the building phase.
     *
     * @param user the user who wants to pick the component
     * @param id   the ID of the component to pick
     * @throws IllegalStateException if the user is not in game
     */
    public static void pickComponent(User user, Integer id) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().pickComponent(user.getUsername(), id);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to release a previously picked component.
     *
     * @param user the user who wants to release the component
     * @param id   the ID of the component to release
     * @throws IllegalStateException if the user is not in game
     */
    public static void releaseComponent(User user, Integer id) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().releaseComponent(user.getUsername(), id);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to reserve a component for future use.
     *
     * @param user the user who wants to reserve the component
     * @param id   the ID of the component to reserve
     * @throws IllegalStateException if the user is not in game
     */
    public static void reserveComponent(User user, Integer id) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().reserveComponent(user.getUsername(), id);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to insert a component into their spaceship.
     *
     * @param user      the user who wants to insert the component
     * @param id        the ID of the component to insert
     * @param row       the target row in the spaceship
     * @param col       the target column in the spaceship
     * @param rotations the number of rotations to apply to the component
     * @throws IllegalStateException if the user is not in game
     */
    public static void insertComponent(User user, Integer id, Integer row, Integer col, Integer rotations) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().insertComponent(user.getUsername(), id, row, col, rotations, true);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to move a component already placed but not welded in their spaceship.
     *
     * @param user      the user who wants to move the component
     * @param id        the ID of the component to move
     * @param row       the new target row
     * @param col       the new target column
     * @param rotations the number of rotations to apply
     * @throws IllegalStateException if the user is not in game
     */
    public static void moveComponent(User user, Integer id, Integer row, Integer col, Integer rotations) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().moveComponent(user.getUsername(), id, row, col, rotations);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to rotate a component not already welded in their spaceship.
     *
     * @param user the user who wants to rotate the component
     * @param id   the ID of the component to rotate
     * @param num  the number of rotations to apply (90° per rotation)
     * @throws IllegalStateException if the user is not in game
     */
    public static void rotateComponent(User user, Integer id, Integer num) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().rotateComponent(user.getUsername(), id, num);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to look at a card pile during the game.
     *
     * @param user      the user who wants to look at the pile
     * @param pileIndex the index of the card pile to look at
     * @throws IllegalStateException if the user is not in game
     */
    public static void lookCardPile(User user, Integer pileIndex) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().lookCardPile(user.getUsername(), pileIndex);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to release the card pile they were looking at.
     *
     * @param user the user who wants to release the pile
     * @throws IllegalStateException if the user is not in game
     */
    public static void releaseCardPile(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().releaseCardPile(user.getUsername());
        user.notifyEvents(events);
    }

    /**
     * Allows a user to move the hourglass during the building phase.
     *
     * @param user the user who wants to move the hourglass
     * @throws IllegalStateException if the user is not in game
     */
    public static void moveHourglass(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().moveHourglass(user.getUsername(), user::notifyEvents);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to signal they are ready for the next phase.
     *
     * @param user the user declaring readiness
     * @throws IllegalStateException if the user is not in game
     */
    public static void setReady(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().setReady(user.getUsername());
        user.notifyEvents(events);
    }

    /**
     * Allows a user to check their spaceship's validity and remove components.
     *
     * @param user     the user who wants to check the spaceship
     * @param toRemove list of component IDs to remove
     * @throws IllegalStateException if the user is not in game
     */
    public static void checkShip(User user, List<Integer> toRemove) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().checkShip(user.getUsername(), toRemove);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to choose aliens if they want to.
     *
     * @param user      the user choosing the aliens
     * @param aliensIds map associating alien IDs to their types
     * @throws IllegalStateException if the user is not in game
     */
    public static void chooseAlien(User user, Map<Integer, AlienType> aliensIds) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().chooseAlien(user.getUsername(), aliensIds);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to choose a spaceship part to keep
     * in case the ship breaks down into separate parts.
     *
     * @param user      the user making the choice
     * @param partIndex the index of the spaceship part to choose
     * @throws IllegalStateException if the user is not in game
     */
    public static void chooseShipPart(User user, Integer partIndex) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().chooseShipPart(user.getUsername(), partIndex);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to draw a card during the game.
     *
     * @param user the user who wants to draw a card
     * @throws IllegalStateException if the user is not in game
     */
    public static void drawCard(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().drawCard(user.getUsername());
        user.notifyEvents(events);
    }

    /**
     * Allows a user to activate cannons using specified batteries and components.
     *
     * @param user                the user who wants to activate cannons
     * @param batteriesIds        list of battery IDs to use
     * @param cannonComponentsIds list of cannon component IDs
     * @throws IllegalStateException if the user is not in game
     */
    public static void activateCannons(User user, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().activateCannons(user.getUsername(), batteriesIds, cannonComponentsIds);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to activate engines using specified batteries and components.
     *
     * @param user                the user who wants to activate engines
     * @param batteriesIds        list of battery IDs to use
     * @param engineComponentsIds list of engine component IDs
     * @throws IllegalStateException if the user is not in game
     */
    public static void activateEngines(User user, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().activateEngines(user.getUsername(), batteriesIds, engineComponentsIds);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to activate shield using a battery.
     *
     * @param user      the user who wants to activate the shield
     * @param batteryId the ID of the battery to use for the shield
     * @throws IllegalStateException if the user is not in game
     */
    public static void activateShield(User user, Integer batteryId) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().activateShield(user.getUsername(), batteryId);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to update goods distribution in cargo holds.
     *
     * @param user          the user who wants to update goods
     * @param cargoHoldsIds map associating cargo hold IDs to goods colors
     * @param batteriesIds  list of battery IDs used
     * @throws IllegalStateException if the user is not in game
     */
    public static void updateGoods(User user, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().updateGoods(user.getUsername(), cargoHoldsIds, batteriesIds);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to remove crew members from cabins.
     *
     * @param user      the user who wants to remove crew
     * @param cabinsIds list of cabin IDs to remove crew from
     * @throws IllegalStateException if the user is not in game
     */
    public static void removeCrew(User user, List<Integer> cabinsIds) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().removeCrew(user.getUsername(), cabinsIds);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to roll dice during the game.
     *
     * @param user the user who wants to roll dice
     * @throws IllegalStateException if the user is not in game
     */
    public static void rollDices(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().rollDices(user.getUsername());
        user.notifyEvents(events);
    }

    /**
     * Allows a user to provide a boolean response when requested by the game.
     *
     * @param user  the user providing the response
     * @param value the boolean value of the response
     * @throws IllegalStateException if the user is not in game
     */
    public static void getBoolean(User user, Boolean value) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().getBoolean(user.getUsername(), value);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to provide an index when requested by the game.
     *
     * @param user  the user providing the index
     * @param value the index value
     * @throws IllegalStateException if the user is not in game
     */
    public static void getIndex(User user, Integer value) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().getIndex(user.getUsername(), value);
        user.notifyEvents(events);
    }

    /**
     * Allows a user to end their flight early.
     *
     * @param user the user who wants to end the flight
     * @throws IllegalStateException if the user is not in game
     */
    public static void endFlight(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<Event> events = user.getGameController().endFlight(user.getUsername());
        user.notifyEvents(events);
    }

    private static boolean notContainsError(List<Event> events) {
        for (Event event : events)
            if (event.eventType() == MessageType.ERROR)
                return false;
        return true;
    }

}
