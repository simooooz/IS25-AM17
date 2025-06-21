package it.polimi.ingsw.network;

import it.polimi.ingsw.common.model.events.BatchEndedEvent;
import it.polimi.ingsw.common.model.events.BatchStartedEvent;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.common.model.enums.LobbyState;
import it.polimi.ingsw.network.rmi.ClientCallbackInterface;
import it.polimi.ingsw.network.exceptions.UserNotFoundException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Server's point of view of the client. This class handles connected users
 */
public class User {

    private final static List<User> activeUsers = new ArrayList<>();
    private final static List<User> inactiveUsers = new ArrayList<>();

    protected final String connectionCode;
    protected String username;
    private UserState state;
    private Lobby lobby;

    private final ClientCallbackInterface callback;
    private long lastPing; // RMI only

    public User(String connectionCode, boolean isRMI, ClientCallbackInterface callback) {
        this.connectionCode = connectionCode;
        this.username = null;
        this.state = UserState.USERNAME;
        this.lobby = null;

        this.callback = callback;
        if (isRMI) {
            lastPing = System.currentTimeMillis();
        }

        synchronized (activeUsers) {
            activeUsers.add(this);
        }
    }

    public void notifyEvents(List<GameEvent> events) {
        if (events.size() == 1) notifyGameEvent(events.getFirst());
        else {
            notifyGameEvent(new BatchStartedEvent());
            for (GameEvent event : events)
                notifyGameEvent(event);
            notifyGameEvent(new BatchEndedEvent());
        }
    }

    public void notifyGameEvent(GameEvent gameEvent) {
        List<String> playersToNotify = new ArrayList<>();
        if ((gameEvent.getVisibility() == EventVisibility.ALL_PLAYERS || gameEvent.getVisibility() == EventVisibility.OTHER_PLAYERS) && lobby != null)
            playersToNotify.addAll(lobby.getPlayers());
        else if (gameEvent.getVisibility() == EventVisibility.PLAYER_ONLY)
            playersToNotify.add(username);
        else if (gameEvent.getVisibility() == EventVisibility.SPECIFIC_PLAYERS)
            playersToNotify.addAll(gameEvent.getTargetPlayers());

        if (gameEvent.getVisibility() == EventVisibility.OTHER_PLAYERS)
            playersToNotify.remove(username);

        for (String playerToNotify : playersToNotify) {
            User userToNotify = User.getUser(playerToNotify);
            userToNotify.sendGameEvent(gameEvent);
        }
    }

    // RMI -> args is list of parameters
    // Socket -> args parameter has length 1 and is a message
    public void sendGameEvent(GameEvent gameEvent) {
        try {
            this.getCallback().notifyGameEvent(gameEvent.eventType(), gameEvent.getArgs());
        } catch (RemoteException e) {
            System.out.println("[USER] Rmi callback remote exception: " + e.getMessage());
            e.printStackTrace();
            // Error while notifying an update to a client
            // Just ignore it
        }
    }

    public GameController getGameController() {
        return lobby.getGame();
    }

    public String getUsername() {
        return username != null ? username : "";
    }

    public void setUsername(String username) {
        synchronized (activeUsers) {
            boolean taken = activeUsers.stream().anyMatch(user -> user.getUsername() != null && user.getUsername().equals(username));
            if (!taken) {
                this.username = username;
            }
            else
                throw new IllegalArgumentException("Username already taken");
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
        synchronized (activeUsers) {
            temp = new ArrayList<>(activeUsers);
        }
        return temp.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

    public static User popInactiveUser(String username) {
        synchronized (inactiveUsers) {
            for (User user : inactiveUsers) {
                if (user.getUsername().equals(username)) {
                    inactiveUsers.remove(user);
                    return user;
                }
            }
        }
        return null;
    }

    public static void removeUser(User user) {
        synchronized (activeUsers) {
            if (!activeUsers.contains(user)) return;
        }

        if (user.lobby != null) {
            List<GameEvent> events = MatchController.getInstance().leaveGame(user.getUsername());
            if (user.lobby.getState() == LobbyState.IN_GAME) { // Player was gaming, add to inactive users
                synchronized (inactiveUsers) {
                    inactiveUsers.add(user);
                }
                user.notifyEvents(events);
            }
        }

        synchronized (activeUsers) {
            activeUsers.remove(user);
        }
    }

}
