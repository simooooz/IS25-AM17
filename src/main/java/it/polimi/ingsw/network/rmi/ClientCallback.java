package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.UserState;
import it.polimi.ingsw.network.messages.MessageType;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
                client.setState(UserState.LOBBY_SELECTION);
                client.setGameController(null);
            }
            case GAME_STARTED_OK -> {
                client.setState(UserState.IN_GAME);
                client.setGameController(new GameController(lobby.getPlayers(), lobby.isLearnerMode()));
            }
        }

        // Send update to Display Updater thread
        try {
            client.getViewTui().getNetworkMessageQueue().put(lobbyEvent.name());
        } catch (InterruptedException e) {
            // Just ignore it
        }
    }

    @Override
    public void notifyGameEvent(MessageType eventType, String username, Object... params) throws RemoteException {
        switch (eventType) {
            case PICK_COMPONENT -> client.getGameController().pickComponent(username, (Integer) params[0]);
            case RELEASE_COMPONENT -> client.getGameController().releaseComponent(username, (Integer) params[0]);
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
