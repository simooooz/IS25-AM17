package it.polimi.ingsw.network;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.messages.*;
import it.polimi.ingsw.network.rmi.ClientCallbackInterface;
import it.polimi.ingsw.network.exceptions.UserNotFoundException;
import it.polimi.ingsw.network.socket.server.ClientHandler;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Server's point of view of the client. This class handles connected users
 */
public class User {

    private final static List<User> users = new ArrayList<>();

    protected final String connectionCode;
    private String username;
    private GameController gameController;
    private UserState state;
    private Lobby lobby;

    private final boolean isRMI;
    private final ClientCallbackInterface callback;
    private long lastPing; // RMI only

    public User(String connectionCode, boolean isRMI, ClientCallbackInterface callback) {
        this.connectionCode = connectionCode;
        this.username = null;
        this.gameController = null;
        this.state = UserState.USERNAME;
        this.lobby = null;

        this.callback = callback;
        this.isRMI = isRMI;
        if (isRMI) {
            lastPing = System.currentTimeMillis();
        }

        synchronized (users) {
            users.add(this);
        }
    }

    // RMI -> args is list of parameters
    // Socket -> args parameter has length 1 and is a message
    public void notifyGameEvent(MessageType gameEvent, Object... args) {
        for (String playerToNotify : lobby.getPlayers()) {
            User userToNotify = User.getUser(playerToNotify);
            if (userToNotify.isRMI) {
                try {
                    userToNotify.getCallback().notifyGameEvent(gameEvent, username, args);
                } catch (RemoteException e) {
                    // Error while notifying an update to a client
                    // Just ignore it
                }
            }
            else {
                Object[] newArgs = new Object[args.length + 1];
                newArgs[0] = username;
                System.arraycopy(args, 0, newArgs, 1, args.length);
                Message message = Constants.createMessage(gameEvent, newArgs);
                ((ClientHandler) userToNotify).send(message);
            }
        }
    }

    // RMI -> callback for lobby
    // Socket -> message with lobby
    public void notifyLobbyEvent(MessageType lobbyEvent, List<String> playersToNotify) {
        for (String username : playersToNotify) {
            User player = User.getUser(username);
            if (player.isRMI) {
                try {
                    player.getCallback().updateLobbyStatus(lobbyEvent, this.lobby);
                } catch (RemoteException e) {
                    // Error while notifying an update to a client
                    // Just ignore it
                }
            }
            else
                ((ClientHandler) player).send(new SingleArgMessage<>(lobbyEvent, this.lobby));
        }
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public String getUsername() {
        return username != null ? username : "";
    }

    public boolean setUsername(String username) {
        synchronized (users) {
            boolean taken = users.stream().anyMatch(user -> user.getUsername() != null && user.getUsername().equals(username));
            if (!taken) {
                this.username = username;
                setState(UserState.LOBBY_SELECTION);
            }
            return !taken;
        }
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    // RMI only
    public ClientCallbackInterface getCallback() {
        return callback;
    }

    // RMI only
    public long getLastPing() {
        return lastPing;
    }

    // RMI only
    public void setLastPing(long lastPing) {
        this.lastPing = lastPing;
    }

    public static User getUser(String username) {
        List<User> temp;
        synchronized (users) {
            temp = new ArrayList<>(users);
        }
        return temp.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

    public static void removeUser(User user) {
        if (user.lobby != null) {
            MatchController.getInstance().leaveGame(user.getUsername());
            user.lobby = null;
        }

        synchronized (users) {
            users.remove(user);
        }
    }

    public static boolean isUsernameTaken(String username) {
        synchronized (users) {
            return users.stream().anyMatch(user -> user.getUsername() != null && user.getUsername().equals(username));
        }
    }

}
