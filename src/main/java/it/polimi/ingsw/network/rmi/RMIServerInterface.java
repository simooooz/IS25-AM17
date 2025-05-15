package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.model.game.Lobby;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInterface extends Remote {

    void registerClient(String sessionCode, ClientCallbackInterface callback) throws RemoteException;

    void unregisterClient(String sessionCode) throws RemoteException;

    void ping(String sessionCode) throws RemoteException;

    boolean setUsernameHandler(String sessionCode, String username) throws RemoteException;

    void createLobbyHandler(String sessionCode, String name, Integer maxPlayers, Boolean learnerMode) throws RemoteException;

    void joinLobbyHandler(String sessionCode, String lobbyName) throws RemoteException;

    void joinRandomLobbyHandler(String sessionCode, Boolean learnerMode) throws RemoteException;

    void leaveGameHandler(String sessionCode) throws RemoteException;

    void pickComponentHandler(String sessionCode, Integer id) throws RemoteException;

    void releaseComponentHandler(String sessionCode, Integer id) throws RemoteException;

}