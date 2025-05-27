package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.controller.exceptions.LobbyNotFoundException;
import it.polimi.ingsw.controller.exceptions.PlayerAlreadyInException;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.model.game.LobbyState;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ServerBasis {

    public static boolean setUsername(User user, String username) {
        if (user.getState() != UserState.USERNAME) throw new IllegalStateException("User is not in state USERNAME");

        if (User.isUsernameTaken(username))
            return false;
        return user.setUsername(username);
    }

    public static void createLobby(User user, String name, Integer maxPlayers, Boolean learnerMode) throws PlayerAlreadyInException {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");
        Lobby lobby = MatchController.getInstance().createNewGame(user.getUsername(), maxPlayers, name, learnerMode);
        user.setLobby(lobby);
        user.notifyLobbyEvent(MessageType.CREATE_LOBBY_OK, lobby.getPlayers());
    }

    public static void joinLobby(User user, String lobbyName) throws LobbyNotFoundException, PlayerAlreadyInException {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");
        Lobby lobby = MatchController.getInstance().joinGame(user.getUsername(), lobbyName);
        user.setLobby(lobby);
        user.notifyLobbyEvent(lobby.getState() == LobbyState.IN_GAME ? MessageType.GAME_STARTED_OK : MessageType.JOIN_LOBBY_OK, lobby.getPlayers());
    }

    public static void joinRandomLobby(User user, Boolean learnerMode) throws LobbyNotFoundException, PlayerAlreadyInException {
        if (user.getState() != UserState.LOBBY_SELECTION) throw new IllegalStateException("User is not in state LOBBY");
        Lobby lobby = MatchController.getInstance().joinRandomGame(user.getUsername(), learnerMode);
        user.setLobby(lobby);
        user.notifyLobbyEvent(lobby.getState() == LobbyState.IN_GAME ? MessageType.GAME_STARTED_OK : MessageType.JOIN_RANDOM_LOBBY_OK, lobby.getPlayers());
    }

    public static void leaveGame(User user) {
        // TODO check state
        // TODO gestire situazione lobby si elimina perché ci sono < 2 giocatori
        MatchController.getInstance().leaveGame(user.getUsername());

        List<String> playersToNotify = new ArrayList<>(user.getLobby().getPlayers());
        playersToNotify.add(user.getUsername());
        user.notifyLobbyEvent(MessageType.LEAVE_GAME_OK, playersToNotify);

        user.setLobby(null);
        user.setGameController(null); // Giusto?
    }

    public static void pickComponent(User client, Integer id) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().pickComponent(client.getUsername(), id);
        client.notifyGameEvent(MessageType.PICK_COMPONENT, id);
    }

    public static void releaseComponent(User client, Integer id) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().releaseComponent(client.getUsername(), id);
        client.notifyGameEvent(MessageType.RELEASE_COMPONENT, id);
    }

    public static void reserveComponent(User client, Integer id) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().reserveComponent(client.getUsername(), id);
        client.notifyGameEvent(MessageType.RESERVE_COMPONENT, id);
    }

    public static void insertComponent(User client, Integer id, Integer row, Integer col, Integer rotations) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().insertComponent(client.getUsername(), id, row, col, rotations, true);
        client.notifyGameEvent(MessageType.INSERT_COMPONENT, id, row, col, rotations);
    }

    public static void moveComponent(User client, Integer id, Integer row, Integer col, Integer rotations) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().moveComponent(client.getUsername(), id, row, col, rotations);
        client.notifyGameEvent(MessageType.MOVE_COMPONENT, id, row, col, rotations);
    }

    public static void rotateComponent(User client, Integer id, Integer num) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().rotateComponent(client.getUsername(), id, num);
        client.notifyGameEvent(MessageType.ROTATE_COMPONENT, id, num);
    }

    public static void lookCardPile(User client, Integer pileIndex) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().lookCardPile(client.getUsername(), pileIndex);
        client.notifyGameEvent(MessageType.LOOK_CARD_PILE);
    }

    public static void moveHourglass(User client) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().moveHourglass(client.getUsername());
        client.notifyGameEvent(MessageType.MOVE_HOURGLASS);
    }

    public static void setReady(User client) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().setReady(client.getUsername());
        client.notifyGameEvent(MessageType.SET_READY);
    }

    public static void checkShip(User client, List<Integer> toRemove) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().checkShip(client.getUsername(), toRemove);
        client.notifyGameEvent(MessageType.CHECK_SHIP, toRemove);
    }

    public static void chooseAlien(User client, Map<Integer, AlienType> aliensIds) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().chooseAlien(client.getUsername(), aliensIds);
        client.notifyGameEvent(MessageType.CHOOSE_ALIEN, aliensIds);
    }

    public static void chooseShipPart(User client, Integer partIndex) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().chooseShipPart(client.getUsername(), partIndex);
        client.notifyGameEvent(MessageType.CHOOSE_SHIP_PART, partIndex);
    }

    public static void drawCard(User client) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().drawCard(client.getUsername());
        client.notifyGameEvent(MessageType.DRAW_CARD);
    }

    public static void activateCannons(User client, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().activateCannons(client.getUsername(), batteriesIds, cannonComponentsIds);
        client.notifyGameEvent(MessageType.ACTIVATE_CANNONS, batteriesIds, cannonComponentsIds);
    }

    public static void activateEngines(User client, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().activateEngines(client.getUsername(), batteriesIds, engineComponentsIds);
        client.notifyGameEvent(MessageType.ACTIVATE_ENGINES, batteriesIds, engineComponentsIds);
    }

    public static void activateShield(User client, Integer batteryId) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().activateShield(client.getUsername(), batteryId);
        client.notifyGameEvent(MessageType.ACTIVATE_SHIELD, batteryId);
    }

    public static void updateGoods(User client, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().updateGoods(client.getUsername(), cargoHoldsIds, batteriesIds);
        client.notifyGameEvent(MessageType.UPDATE_GOODS, cargoHoldsIds, batteriesIds);
    }

    public static void removeCrew(User client, List<Integer> cabinsIds) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().removeCrew(client.getUsername(), cabinsIds);
        client.notifyGameEvent(MessageType.REMOVE_CREW, cabinsIds);
    }

    public static void rollDices(User client) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().rollDices(client.getUsername());
        client.notifyGameEvent(MessageType.ROLL_DICES);
    }

    public static void getBoolean(User client, Boolean value) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().getBoolean(client.getUsername(), value);
        client.notifyGameEvent(MessageType.GET_BOOLEAN, value);
    }

    public static void getIndex(User client, Integer value) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().getIndex(client.getUsername(), value);
        client.notifyGameEvent(MessageType.GET_INDEX, value);
    }

    public static void endFlight(User client) {
        if (client.getState() != UserState.IN_GAME) throw new IllegalStateException("User is not in state MATCH");
        client.getGameController().endFlight(client.getUsername());
        client.notifyGameEvent(MessageType.END_FLIGHT);
    }

}
