package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.client.controller.ClientGameController;
import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.factory.ClientCardFactory;
import it.polimi.ingsw.client.model.game.ClientLobby;
import it.polimi.ingsw.common.dto.GameStateDTOFactory;
import it.polimi.ingsw.common.dto.ModelDTO;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.events.game.GameErrorEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.UserState;
import it.polimi.ingsw.network.messages.MessageType;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class ClientCallback extends UnicastRemoteObject implements ClientCallbackInterface {

    private final Client client;

    public ClientCallback(Client client) throws RemoteException {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void notifyGameEvent(MessageType eventType, Object... args) throws RemoteException {
        System.out.println("[CLIENT CALLBACK] Received call notifyGameEvent " + eventType);
        switch (eventType) {
            case ERROR -> ClientEventBus.getInstance().publish(new GameErrorEvent((String) args[0]));
            case BATCH_START -> ClientEventBus.getInstance().startBatch();
            case BATCH_END -> ClientEventBus.getInstance().endBatch();

            case USERNAME_OK_EVENT -> {
                client.setState(UserState.LOBBY_SELECTION);
                client.setUsername((String) args[0]);
            }
            case CREATED_LOBBY_EVENT -> {
                client.setState(UserState.IN_LOBBY);
                client.setLobby(new ClientLobby((String) args[0], (List<String>) args[1], (Boolean) args[2], (Integer) args[3]));
            }
            case JOINED_LOBBY_EVENT -> {
                client.getLobby().addPlayer((String) args[0]);
            }
            case LEFT_LOBBY_EVENT -> {
                if (!args[0].equals(client.getUsername()))
                    client.getLobby().removePlayer((String) args[0]);
                else {
                    client.setState(UserState.LOBBY_SELECTION);
                    client.setLobby(null);
                }
            }

            case MATCH_STARTED_EVENT -> {
                client.setState(UserState.IN_GAME);
                client.getLobby().initGame();
            }
            case SYNC_ALL_EVENT -> {
                ModelDTO dto = GameStateDTOFactory.deserializeDTO((String) args[0]);

                client.setState(UserState.IN_GAME);
                ClientLobby lobby = client.getLobby();
                lobby.setGame(new ClientGameController(lobby.isLearnerMode(), dto));
            }
            case FLIGHT_ENDED_EVENT -> client.getGameController().flightEnded((String) args[0]);
            case PLAYERS_STATE_UPDATED_EVENT -> client.getGameController().playersStateUpdated((Map<String, PlayerState>) args[0]);
            case COMPONENT_PICKED_EVENT -> client.getGameController().componentPicked((String) args[0], (Integer) args[1]);
            case COMPONENT_RELEASED_EVENT -> client.getGameController().componentReleased((String) args[0], (Integer) args[1]);
            case COMPONENT_RESERVED_EVENT -> client.getGameController().componentReserved((String) args[0], (Integer) args[1]);
            case COMPONENT_INSERTED_EVENT -> client.getGameController().componentInserted((String) args[0], (Integer) args[1], (Integer) args[2], (Integer) args[3]);
            case COMPONENT_MOVED_EVENT -> client.getGameController().componentMoved((String) args[0], (Integer) args[1], (Integer) args[2], (Integer) args[3]);
            case COMPONENT_ROTATED_EVENT -> client.getGameController().componentRotated((Integer) args[0], (Integer) args[1]);
            case COMPONENT_DESTROYED_EVENT -> client.getGameController().componentDestroyed((String) args[0], (Integer) args[1]);
            case CARD_PILE_LOOKED_EVENT -> {
                List<ClientCard> cards = ClientCardFactory.deserializeCardList((String) args[2]);
                client.getGameController().cardPileLooked((String) args[0], (Integer) args[1], cards);
            }
            case CARD_PILE_RELEASED_EVENT -> client.getGameController().cardPileReleased((String) args[0]);
            case HOURGLASS_MOVED_EVENT -> client.getGameController().hourglassMoved();
            case SHIP_BROKEN_EVENT -> client.getGameController().shipBroken((String) args[0], (List<List<Integer>>) args[1]);
            case PLAYERS_POSITION_UPDATED_EVENT -> client.getGameController().playersPositionUpdated((List<String>) args[0], (List<AbstractMap.SimpleEntry<String, Integer>>) args[1]);
            case CARD_REVEALED_EVENT -> {
                ClientCard card = ClientCardFactory.deserializeCard((String) args[0]);
                client.getGameController().cardRevealed(card);
            }
            case CARD_UPDATED_EVENT -> {
                ClientCard card = ClientCardFactory.deserializeCard((String) args[0]);
                client.getGameController().cardUpdated(card);
            }
            case BATTERIES_UPDATED_EVENT -> client.getGameController(). batteriesUpdated((Integer) args[0], (Integer) args[1]);
            case GOODS_UPDATED_EVENT -> client.getGameController().goodsUpdated((Integer) args[0], (List<ColorType>) args[1]);
            case CREW_UPDATED_EVENT -> client.getGameController().crewUpdated((Integer) args[0], (Integer) args[1], (AlienType) args[2]);
            case CREDITS_UPDATED_EVENT -> client.getGameController().creditsUpdated((String) args[0], (Integer) args[1]);
        }

    }

    @Override
    public void sendPong() throws RemoteException {
        // Do nothing, it servers only to check if client is active
    }

}
