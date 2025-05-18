package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RMIClient extends Client {

    private final String sessionCode;
    private RMIServerInterface server;
    private ClientCallback clientCallback;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RMIClient(String host, int port) {
        this.sessionCode = UUID.randomUUID().toString();
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (RMIServerInterface) registry.lookup("ServerRMI");

            clientCallback = new ClientCallback(this);
            server.registerClient(sessionCode, clientCallback);

            // Start sending ping
            scheduler.scheduleAtFixedRate(this::sendPing, Constants.HEARTBEAT_INTERVAL, Constants.HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
            this.viewTui.start();

        } catch (NotBoundException | RemoteException e) {
            System.err.println("[RMI CLIENT] Could not connect to " + host + ":" + port);
            System.exit(-1);
        }
    }

    private void sendPing() {
        try {
            server.ping(sessionCode);
        } catch (RemoteException e) {
            // Error while sending ping, do nothing
            // Server-side session will be eventually closed
        }
    }

    @Override
    public void closeConnection() {
        scheduler.shutdownNow();

        try {
            server.unregisterClient(sessionCode);
            UnicastRemoteObject.unexportObject(clientCallback, true);
        } catch (RemoteException e) {
            // Error while unregistering client
            // Do nothing
        }

    }

    @Override
    public void send(MessageType messageType, Object... args) {
        try {
            switch (messageType) {
                case SET_USERNAME -> sendSetUsername((String) args[0]);
                case CREATE_LOBBY -> server.createLobbyHandler(sessionCode, (String) args[0], (Integer) args[1], (Boolean) args[2]);
                case JOIN_LOBBY -> server.joinLobbyHandler(sessionCode, (String) args[0]);
                case JOIN_RANDOM_LOBBY -> server.joinRandomLobbyHandler(sessionCode, (Boolean) args[0]);
                case LEAVE_GAME -> server.leaveGameHandler(sessionCode);
                case PICK_COMPONENT -> server.pickComponentHandler(sessionCode, (Integer) args[0]);
                case RELEASE_COMPONENT -> server.releaseComponentHandler(sessionCode, (Integer) args[0]);
            }
        } catch (RemoteException | RuntimeException e) {
            viewTui.displayError();
        }
    }

    private void sendSetUsername(String username) throws RemoteException {
        boolean done = server.setUsernameHandler(sessionCode, username);
        if (done)
            setUsername(username);
        else
            Chroma.println("username already taken", Chroma.RED);
        viewTui.handleUIState();
    }

}
