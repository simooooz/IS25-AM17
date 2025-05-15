package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.messages.MessageType;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallbackInterface extends Remote {

    void updateLobbyStatus(MessageType lobbyEvent, Lobby lobby) throws RemoteException;

    void notifyGameEvent(MessageType eventType, String username, Object... params) throws RemoteException;

    void sendPong() throws RemoteException;

}
