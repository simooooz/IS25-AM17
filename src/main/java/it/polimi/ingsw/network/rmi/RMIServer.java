package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.network.ServerBasis;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.exceptions.UserNotFoundException;
import it.polimi.ingsw.network.User;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class RMIServer extends ServerBasis implements RMIServerInterface {

    private static RMIServer instance;
    private final Registry registry;
    private final Map<String, User> sessions;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RMIServer(int port) throws ServerException {
        this.sessions = new HashMap<>();

        try {
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", String.valueOf(Constants.NETWORK_TIMEOUT));
            System.setProperty("sun.rmi.transport.tcp.handshakeTimeout", "10000");
            System.setProperty("sun.rmi.dgc.server.gcInterval", "3600000");
            System.setProperty("sun.rmi.dgc.client.gcInterval", "3600000");

            registry = LocateRegistry.createRegistry(port);
            RMIServerInterface stub = (RMIServerInterface) UnicastRemoteObject.exportObject(this, port);
            registry.rebind("ServerRMI", stub);
        } catch (RemoteException e) {
            throw new ServerException("[RMI SERVER] Server cannot be started: " + e.getMessage());
        }

        scheduler.scheduleAtFixedRate(this::checkActiveClients, Constants.SERVER_CHECK_INTERVAL, Constants.SERVER_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
        System.out.println("[RMI SERVER] Server started on port " + port);
    }

    public static RMIServer getInstance() throws ServerException {
        if (instance != null) {
            return instance;
        }
        throw new ServerException("Server is not instantiated");
    }

    public static RMIServer getInstance(int port) throws ServerException {
        if (instance == null) {
            instance = new RMIServer(port);
        }
        return instance;
    }

    public void stop() {
        scheduler.shutdownNow();

        synchronized (sessions) {
            for (String sessionCode : new HashSet<>(sessions.keySet())) {
                unregisterClient(sessionCode);
            }
            sessions.clear();
        }

        if (registry != null) {
            try {
                registry.unbind("ServerRMI");
                UnicastRemoteObject.unexportObject(this, true);
                UnicastRemoteObject.unexportObject(registry, true);
            } catch (NotBoundException | RemoteException e) {
                // Do nothing
            }
            System.out.println("[RMI SERVER] Server closed...");
        }
    }

    private User getUserInRmiSessions(String sessionCode) throws UserNotFoundException {
        User user;
        synchronized (sessions) {
            user = sessions.get(sessionCode);
        }
        if (user == null)
            throw new UserNotFoundException("User not found");
        return user;
    }

    private void checkActiveClients() {
        long now = System.currentTimeMillis();
        Map<String, User> sessionsCopy;

        synchronized (sessions) {
            sessionsCopy = new HashMap<>(sessions);
        }

        for (Map.Entry<String, User> entry : sessionsCopy.entrySet()) {
            String sessionCode = entry.getKey();
            User user = entry.getValue();

            if (now - user.getLastPing() > Constants.NETWORK_TIMEOUT)
                unregisterClient(sessionCode);
        }

    }

    @Override
    public void registerClient(String sessionCode, ClientCallbackInterface callback) throws RemoteException {
        synchronized (sessions) {
            sessions.put(sessionCode, new User(sessionCode, callback));
        }
    }

    @Override
    public void unregisterClient(String sessionCode) {
        User.removeUser(getUserInRmiSessions(sessionCode));
        synchronized (sessions) {
            sessions.remove(sessionCode);
        }
        System.out.println("[RMI SERVER] Session: " + sessionCode + " closed");
    }

    @Override
    public void ping(String sessionCode) throws RemoteException {
        try {
            User user = getUserInRmiSessions(sessionCode);
            user.setLastPing(System.currentTimeMillis());
        } catch (UserNotFoundException e) {
            throw new RemoteException("Session expired: " + sessionCode);
        }
    }

    @Override
    public void setUsernameHandler(String sessionCode, String username) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        setUsername(user, username);
    }

    @Override
    public void createLobbyHandler(String sessionCode, String name, Integer maxPlayers, Boolean learnerMode) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        createLobby(user, name, maxPlayers, learnerMode);
    }

    @Override
    public void joinLobbyHandler(String sessionCode, String lobbyName) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        joinLobby(user, lobbyName);
    }

    @Override
    public void joinRandomLobbyHandler(String sessionCode, Boolean learnerMode) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        joinRandomLobby(user, learnerMode);
    }

    @Override
    public void leaveGameHandler(String sessionCode) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        leaveGame(user);
    }

    @Override
    public void pickComponentHandler(String sessionCode, Integer id) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        pickComponent(user, id);
    }

    @Override
    public void releaseComponentHandler(String sessionCode, Integer id) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        releaseComponent(user, id);
    }

    @Override
    public void reserveComponentHandler(String sessionCode, Integer id) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        reserveComponent(user, id);
    }

    @Override
    public void insertComponentHandler(String sessionCode, Integer id, Integer row, Integer col, Integer rotations) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        insertComponent(user, id, row, col, rotations);
    }

    @Override
    public void moveComponentHandler(String sessionCode, Integer id, Integer row, Integer col, Integer rotations) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        moveComponent(user, id, row, col, rotations);
    }

    @Override
    public void rotateComponentHandler(String sessionCode, Integer id, Integer num) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        rotateComponent(user, id, num);
    }

    @Override
    public void lookCardPileHandler(String sessionCode, Integer pileIndex) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        lookCardPile(user, pileIndex);
    }

    @Override
    public void releaseCardPileHandler(String sessionCode) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        releaseCardPile(user);
    }

    @Override
    public void moveHourglassHandler(String sessionCode) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        moveHourglass(user);
    }

    @Override
    public void setReadyHandler(String sessionCode) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        setReady(user);
    }

    @Override
    public void checkShipHandler(String sessionCode, List<Integer> toRemove) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        checkShip(user, toRemove);
    }

    @Override
    public void chooseAlienHandler(String sessionCode, Map<Integer, AlienType> aliensIds) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        chooseAlien(user, aliensIds);
    }

    @Override
    public void chooseShipPartHandler(String sessionCode, Integer partIndex) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        chooseShipPart(user, partIndex);
    }

    @Override
    public void drawCardHandler(String sessionCode) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        drawCard(user);
    }

    @Override
    public void activateCannonsHandler(String sessionCode, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        activateCannons(user, batteriesIds, cannonComponentsIds);
    }

    @Override
    public void activateEnginesHandler(String sessionCode, List<Integer> batteriesIds, List<Integer> engineComponentsIds) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        activateEngines(user, batteriesIds, engineComponentsIds);
    }

    @Override
    public void activateShieldHandler(String sessionCode, Integer batteryId) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        activateShield(user, batteryId);
    }

    @Override
    public void updateGoodsHandler(String sessionCode, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        updateGoods(user, cargoHoldsIds, batteriesIds);
    }

    @Override
    public void removeCrewHandler(String sessionCode, List<Integer> cabinsIds) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        removeCrew(user, cabinsIds);
    }

    @Override
    public void rollDicesHandler(String sessionCode) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        rollDices(user);
    }

    @Override
    public void getBooleanHandler(String sessionCode, Boolean value) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        getBoolean(user, value);
    }

    @Override
    public void getIndexHandler(String sessionCode, Integer value) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        getIndex(user, value);
    }

    @Override
    public void endFlightHandler(String sessionCode) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        endFlight(user);
    }

}