package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
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
            try {
                this.viewTui.start();
            } catch (IOException e) {
                // TODO change
                e.printStackTrace();
            }

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

    @SuppressWarnings("unchecked")
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
                case RESERVE_COMPONENT -> server.reserveComponentHandler(sessionCode, (Integer) args[0]);
                case INSERT_COMPONENT -> server.insertComponentHandler(sessionCode, (Integer) args[0], (Integer) args[1], (Integer) args[2], (Integer) args[3]);
                case MOVE_COMPONENT -> server.moveComponentHandler(sessionCode, (Integer) args[0], (Integer) args[1], (Integer) args[2], (Integer) args[3]);
                case ROTATE_COMPONENT -> server.rotateComponentHandler(sessionCode, (Integer) args[0], (Integer) args[1]);
                case LOOK_CARD_PILE -> server.lookCardPileHandler(sessionCode, (Integer) args[0]);
                case MOVE_HOURGLASS -> server.moveHourglassHandler(sessionCode);
                case SET_READY -> server.setReadyHandler(sessionCode);
                case CHECK_SHIP -> server.checkShipHandler(sessionCode, (List<Integer>) args[0]);
                case CHOOSE_ALIEN -> server.chooseAlienHandler(sessionCode, (Map<Integer, AlienType>) args[0]);
                case CHOOSE_SHIP_PART -> server.chooseShipPartHandler(sessionCode, (Integer) args[0]);
                case DRAW_CARD -> server.drawCardHandler(sessionCode);
                case ACTIVATE_CANNONS -> server.activateCannonsHandler(sessionCode, (List<Integer>) args[0], (List<Integer>) args[1]);
                case ACTIVATE_ENGINES -> server.activateEnginesHandler(sessionCode, (List<Integer>) args[0], (List<Integer>) args[1]);
                case ACTIVATE_SHIELD -> server.activateShieldHandler(sessionCode, (Integer) args[0]);
                case UPDATE_GOODS -> server.updateGoodsHandler(sessionCode, (Map<Integer, List<ColorType>>) args[0], (List<Integer>) args[1]);
                case REMOVE_CREW -> server.removeCrewHandler(sessionCode, (List<Integer>) args[0]);
                case ROLL_DICES -> server.rollDicesHandler(sessionCode);
                case GET_BOOLEAN -> server.getBooleanHandler(sessionCode, (Boolean) args[0]);
                case GET_INDEX -> server.getIndexHandler(sessionCode, (Integer) args[0]);
                case END_FLIGHT -> server.endFlightHandler(sessionCode);
            }
        } catch (RemoteException | RuntimeException e) {
            viewTui.displayError(e.getMessage());
        }
    }

    private void sendSetUsername(String username) throws RemoteException {
        boolean done = server.setUsernameHandler(sessionCode, username);
        if (done)
            setUsername(username);
        else {
            Chroma.println("Username already taken", Chroma.RED);
            System.out.print("> ");
        }

        // Send update to Display Updater thread
        try {
            viewTui.getNetworkMessageQueue().put(MessageType.USERNAME_OK.name());
        } catch (InterruptedException e) {
            // Just ignore it
        }

    }

}
