package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

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

    void reserveComponentHandler(String sessionCode, Integer id) throws RemoteException;

    void insertComponentHandler(String sessionCode, Integer id, Integer row, Integer col, Integer rotations) throws RemoteException;

    void moveComponentHandler(String sessionCode, Integer id, Integer row, Integer col, Integer rotations) throws RemoteException;

    void rotateComponentHandler(String sessionCode, Integer id, Integer num) throws RemoteException;

    void moveHourglassHandler(String sessionCode) throws RemoteException;

    void setReadyHandler(String sessionCode) throws RemoteException;

    void checkShipHandler(String sessionCode, List<Integer> toRemove) throws RemoteException;

    void chooseAlienHandler(String sessionCode, Map<Integer, AlienType> aliensIds) throws RemoteException;

    void chooseShipPartHandler(String sessionCode, Integer partIndex) throws RemoteException;

    void drawCardHandler(String sessionCode) throws RemoteException;

    void activateCannonsHandler(String sessionCode, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) throws RemoteException;

    void activateEnginesHandler(String sessionCode, List<Integer> batteriesIds, List<Integer> engineComponentsIds) throws RemoteException;

    void activateShieldHandler(String sessionCode, Integer batteryId) throws RemoteException;

    void updateGoodsHandler(String sessionCode, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) throws RemoteException;

    void removeCrewHandler(String sessionCode, List<Integer> cabinsIds) throws RemoteException;

    void rollDicesHandler(String sessionCode) throws RemoteException;

    void getBooleanHandler(String sessionCode, Boolean value) throws RemoteException;

    void getIndexHandler(String sessionCode, Integer value) throws RemoteException;

    void endFlightHandler(String sessionCode) throws RemoteException;

}