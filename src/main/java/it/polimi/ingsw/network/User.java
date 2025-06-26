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

    private long lastPing;
    protected final String connectionCode;
    protected String username;
    private UserState state;
    private Lobby lobby;

    private final ClientCallbackInterface callback;

    public User(String connectionCode, ClientCallbackInterface callback) {
        this.connectionCode = connectionCode;
        this.username = null;
        this.state = UserState.USERNAME;
        this.lobby = null;

        this.callback = callback;
        lastPing = System.currentTimeMillis();

        synchronized (activeUsers) {
            activeUsers.add(this);
        }
    }

    public void notifyEvents(List<GameEvent> events) {
        if (events.size() == 1) notifyEvent(events.getFirst());
        else {
            notifyEvent(new BatchStartedEvent());
            for (GameEvent event : events)
                notifyEvent(event);
            notifyEvent(new BatchEndedEvent());
        }
    }

    public void notifyEvent(GameEvent event) {
        List<String> playersToNotify = new ArrayList<>();
        if ((event.getVisibility() == EventVisibility.ALL_PLAYERS || event.getVisibility() == EventVisibility.OTHER_PLAYERS) && lobby != null)
            playersToNotify.addAll(lobby.getPlayers());
        else if (event.getVisibility() == EventVisibility.PLAYER_ONLY)
            playersToNotify.add(username);
        else if (event.getVisibility() == EventVisibility.SPECIFIC_PLAYERS)
            playersToNotify.addAll(event.getTargetPlayers());

        if (event.getVisibility() == EventVisibility.OTHER_PLAYERS)
            playersToNotify.remove(username);

        for (String playerToNotify : playersToNotify) {
            User userToNotify = User.getUser(playerToNotify);
            userToNotify.sendEvent(event);
        }
    }

    public void sendEvent(GameEvent event) {
        try {
            this.getCallback().notifyGameEvent(event.eventType(), event.getArgs());
        } catch (RemoteException e) {
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

    public ClientCallbackInterface getCallback() {
        return callback;
    }

    public long getLastPing() {
        return lastPing;
    }

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
