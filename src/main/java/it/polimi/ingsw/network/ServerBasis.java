package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.controller.exceptions.LobbyNotFoundException;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.common.model.events.lobby.UsernameOkEvent;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.common.model.enums.LobbyState;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.network.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ServerBasis {

    public static void setUsername(User user, String username) {
        if (user.getState() != UserState.USERNAME) throw new IllegalStateException("User is not in state USERNAME");

        user.setUsername(username);
        user.setState(UserState.LOBBY_SELECTION);
        List<GameEvent> events = new ArrayList<>();
        events.add(new UsernameOkEvent(username));

        // Check previous sessions
        User oldUser = User.popInactiveUser(username);
        if (oldUser != null && oldUser.getLobby() != null && oldUser.getLobby().getState() == LobbyState.IN_GAME) { // Rejoin
            events.addAll(MatchController.getInstance().rejoinGame(username, oldUser.getLobby().getGameID()));
            user.setLobby(oldUser.getLobby());
            user.setState(UserState.IN_GAME);

            System.out.println("Riconnesso al gioco " + user.getUsername());
        }

        user.notifyEvents(events);
    }

    public static void createLobby(User user, String name, Integer maxPlayers, Boolean learnerMode) throws PlayerAlreadyInException {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");

        List<GameEvent> events = MatchController.getInstance().createNewGame(user.getUsername(), maxPlayers, name, learnerMode);
        user.setLobby(MatchController.getInstance().getLobby(user.getUsername()));
        user.setState(UserState.IN_LOBBY);
        user.notifyEvents(events);
    }

    public static void joinLobby(User user, String lobbyName) throws LobbyNotFoundException, PlayerAlreadyInException {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");

        List<GameEvent> events = MatchController.getInstance().joinGame(user.getUsername(), lobbyName);
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

        user.notifyEvents(events);
    }

    public static void joinRandomLobby(User user, Boolean learnerMode) throws LobbyNotFoundException, PlayerAlreadyInException {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");

        List<GameEvent> events = MatchController.getInstance().joinRandomGame(user.getUsername(), learnerMode);
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

        user.notifyEvents(events);
    }

    public static void leaveGame(User user) {
        if (user.getState() != UserState.IN_GAME || user.getState() != UserState.IN_LOBBY) throw new IllegalStateException("User is not in lobby or game");

        List<GameEvent> events = MatchController.getInstance().leaveGame(user.getUsername());
        user.setState(UserState.LOBBY_SELECTION);
        user.setLobby(null);

        user.notifyEvents(events);
    }

    public static void pickComponent(User user, Integer id) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().pickComponent(user.getUsername(), id);
        user.notifyEvents(events);
    }

    public static void releaseComponent(User user, Integer id) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().releaseComponent(user.getUsername(), id);
        user.notifyEvents(events);
    }

    public static void reserveComponent(User user, Integer id) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().reserveComponent(user.getUsername(), id);
        user.notifyEvents(events);
    }

    public static void insertComponent(User user, Integer id, Integer row, Integer col, Integer rotations) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().insertComponent(user.getUsername(), id, row, col, rotations, true);
        user.notifyEvents(events);
    }

    public static void moveComponent(User user, Integer id, Integer row, Integer col, Integer rotations) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().moveComponent(user.getUsername(), id, row, col, rotations);
        user.notifyEvents(events);
    }

    public static void rotateComponent(User user, Integer id, Integer num) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().rotateComponent(user.getUsername(), id, num);
        user.notifyEvents(events);
    }

    public static void lookCardPile(User user, Integer pileIndex) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().lookCardPile(user.getUsername(), pileIndex);
        user.notifyEvents(events);
    }

    public static void moveHourglass(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().moveHourglass(user.getUsername());
        user.notifyEvents(events);
    }

    public static void setReady(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().setReady(user.getUsername());
        user.notifyEvents(events);
    }

    public static void checkShip(User user, List<Integer> toRemove) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().checkShip(user.getUsername(), toRemove);
        user.notifyEvents(events);
    }

    public static void chooseAlien(User user, Map<Integer, AlienType> aliensIds) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().chooseAlien(user.getUsername(), aliensIds);
        user.notifyEvents(events);
    }

    public static void chooseShipPart(User user, Integer partIndex) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().chooseShipPart(user.getUsername(), partIndex);
        user.notifyEvents(events);
    }

    public static void drawCard(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().drawCard(user.getUsername());
        user.notifyEvents(events);
    }

    public static void activateCannons(User user, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().activateCannons(user.getUsername(), batteriesIds, cannonComponentsIds);
        user.notifyEvents(events);
    }

    public static void activateEngines(User user, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().activateEngines(user.getUsername(), batteriesIds, engineComponentsIds);
        user.notifyEvents(events);
    }

    public static void activateShield(User user, Integer batteryId) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().activateShield(user.getUsername(), batteryId);
        user.notifyEvents(events);
    }

    public static void updateGoods(User user, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().updateGoods(user.getUsername(), cargoHoldsIds, batteriesIds);
        user.notifyEvents(events);
    }

    public static void removeCrew(User user, List<Integer> cabinsIds) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().removeCrew(user.getUsername(), cabinsIds);
        user.notifyEvents(events);
    }

    public static void rollDices(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        int value = ((int)(Math.random() * 6) + 1) + ((int)(Math.random() * 6) + 1); // TODO spostare nel server model
        List<GameEvent> events = user.getGameController().rollDices(user.getUsername(), value);
        user.notifyEvents(events);
    }

    public static void getBoolean(User user, Boolean value) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().getBoolean(user.getUsername(), value);
        user.notifyEvents(events);
    }

    public static void getIndex(User user, Integer value) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().getIndex(user.getUsername(), value);
        user.notifyEvents(events);
    }

    public static void endFlight(User user) {
        if (user.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        List<GameEvent> events = user.getGameController().endFlight(user.getUsername());
        user.notifyEvents(events);
    }

}
