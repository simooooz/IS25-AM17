package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.UserState;
import it.polimi.ingsw.network.messages.MessageType;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

public class ClientCallback extends UnicastRemoteObject implements ClientCallbackInterface {

    private final Client client;

    public ClientCallback(Client client) throws RemoteException {
        this.client = client;
    }

    @Override
    public void updateLobbyStatus(MessageType lobbyEvent, Lobby lobby) throws RemoteException {
        client.setLobby(lobby);
        switch (lobbyEvent) {
            case CREATE_LOBBY_OK, JOIN_LOBBY_OK, JOIN_RANDOM_LOBBY_OK -> client.setState(UserState.IN_LOBBY);
            case LEAVE_GAME_OK -> {
                String leftPlayer = client.getLobby().getPlayers().stream().filter(u -> !lobby.getPlayers().contains(u)).findFirst().orElseThrow(() -> new RuntimeException("Unknown left player"));
                if (!leftPlayer.equals(client.getUsername())) {
                    client.getLobby().setGame(client.getGameController());
                    client.getLobby().removePlayer(leftPlayer);
                }
                else {
                    client.setLobby(null);
                    client.setGameController(null);
                    client.setState(UserState.LOBBY_SELECTION);
                }
            }
            case GAME_STARTED_OK -> {
                client.setState(UserState.IN_GAME);
                client.setGameController(new GameController(lobby.getPlayers(), lobby.isLearnerMode()));
                client.getGameController().startMatch();

                // Test only
                switch (client.getLobby().getGameID()) {
                    case "test-1" -> client.getGameController().startTest(1);
                    case "test-2" -> client.getGameController().startTest(2);
                }
            }
        }

        // Send update to Display Updater thread
        try {
            client.getViewTui().getNetworkMessageQueue().put(lobbyEvent.name());
        } catch (InterruptedException e) {
            // Just ignore it
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void notifyGameEvent(MessageType eventType, String username, Object... args) throws RemoteException {
        switch (eventType) {
            case SET_SHUFFLED_DECK -> client.getGameController().setShuffledCardPile((List<Integer>) args[0]);
            case PICK_COMPONENT -> client.getGameController().pickComponent(username, (Integer) args[0]);
            case RELEASE_COMPONENT -> client.getGameController().releaseComponent(username, (Integer) args[0]);
            case RESERVE_COMPONENT -> client.getGameController().reserveComponent(username, (Integer) args[0]);
            case INSERT_COMPONENT -> client.getGameController().insertComponent(username, (Integer) args[0], (Integer) args[1], (Integer) args[2], (Integer) args[3], true);
            case MOVE_COMPONENT -> client.getGameController().moveComponent(username, (Integer) args[0], (Integer) args[1], (Integer) args[2], (Integer) args[3]);
            case ROTATE_COMPONENT -> client.getGameController().rotateComponent(username, (Integer) args[0], (Integer) args[1]);
            case LOOK_CARD_PILE -> client.getGameController().lookCardPile(username, (Integer) args[0]);
            case MOVE_HOURGLASS -> client.getGameController().moveHourglass(username);
            case SET_READY -> client.getGameController().setReady(username);
            case CHECK_SHIP -> client.getGameController().checkShip(username, (List<Integer>) args[0]);
            case CHOOSE_ALIEN -> client.getGameController().chooseAlien(username, (Map<Integer, AlienType>) args[0]);
            case CHOOSE_SHIP_PART -> client.getGameController().chooseShipPart(username, (Integer) args[0]);
            case DRAW_CARD -> client.getGameController().drawCard(username);
            case ACTIVATE_CANNONS -> client.getGameController().activateCannons(username, (List<Integer>) args[0], (List<Integer>) args[1]);
            case ACTIVATE_ENGINES -> client.getGameController().activateEngines(username, (List<Integer>) args[0], (List<Integer>) args[1]);
            case ACTIVATE_SHIELD -> client.getGameController().activateShield(username, (Integer) args[0]);
            case UPDATE_GOODS -> client.getGameController().updateGoods(username, (Map<Integer, List<ColorType>>) args[0], (List<Integer>) args[1]);
            case REMOVE_CREW -> client.getGameController().removeCrew(username, (List<Integer>) args[0]);
            case ROLL_DICES -> client.getGameController().rollDices(username, (Integer) args[0]);
            case GET_BOOLEAN -> client.getGameController().getBoolean(username, (Boolean) args[0]);
            case GET_INDEX -> client.getGameController().getIndex(username, (Integer) args[0]);
            case END_FLIGHT -> client.getGameController().endFlight(username);
        }

        // Send update to Display Updater thread
        try {
            client.getViewTui().getNetworkMessageQueue().put(eventType.name());
        } catch (InterruptedException e) {
            // Just ignore it
        }
    }

    @Override
    public void sendPong() throws RemoteException {
        // Do nothing, it servers only to check if client is active
    }

}
