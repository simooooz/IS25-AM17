package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.ServerBasis;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.exceptions.UserNotFoundException;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.User;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
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
            registry = LocateRegistry.createRegistry(port);
            RMIServerInterface stub = (RMIServerInterface) UnicastRemoteObject.exportObject(this, port);
            registry.rebind("ServerRMI", stub);
        } catch (RemoteException e) {
            throw new ServerException("RMI Server cannot be started");
        }

        // Check active clients every SOCKET_TIMEOUT * 2 (timeout is SOCKET_TIMEOUT and ping is sent every HEARTBEAT_INTERVAL)
        scheduler.scheduleAtFixedRate(this::checkActiveClients, 10000, Constants.SOCKET_TIMEOUT * 2, TimeUnit.MILLISECONDS);
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

    public void unregisterServer() {
        scheduler.shutdownNow();
        if (registry != null) {
            try {
                UnicastRemoteObject.unexportObject(registry, true);
            } catch (NoSuchObjectException e) {
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

            if (now - user.getLastPing() > Constants.SOCKET_TIMEOUT) {
                try {
                    user.getCallback().sendPong(); // Check if client is still active,
                    user.setLastPing(System.currentTimeMillis());
                } catch (RemoteException e) {
                    unregisterClient(sessionCode); // Client not reachable
                }
            }
        }

    }

    @Override
    public void registerClient(String sessionCode, ClientCallbackInterface callback) throws RemoteException {
        synchronized (sessions) {
            sessions.put(sessionCode, new User(sessionCode,true, callback));
        }
    }

    @Override
    public void unregisterClient(String sessionCode) {
        // TODO non rimuovi ma set inattivo
        User.removeUser(getUserInRmiSessions(sessionCode));
        synchronized (sessions) {
            sessions.remove(sessionCode);
        }
        System.out.println("[RMI SERVER] Session: " + sessionCode + " closed");
    }

    @Override
    public void ping(String sessionCode) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        user.setLastPing(System.currentTimeMillis());
    }

    // Returns a boolean value
    // true if username has been set, otherwise false
    @Override
    public boolean setUsernameHandler(String sessionCode, String username) throws RemoteException {
        return setUsername(getUserInRmiSessions(sessionCode), username);
    }

    // Returns GameID
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
        user.notifyGameEvent(MessageType.PICK_COMPONENT, id);
    }

    @Override
    public void releaseComponentHandler(String sessionCode, Integer id) throws RemoteException {
        User user = getUserInRmiSessions(sessionCode);
        releaseComponent(user, id);
        user.notifyGameEvent(MessageType.RELEASE_COMPONENT, id);
    }

}
