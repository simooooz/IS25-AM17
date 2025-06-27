package it.polimi.ingsw.network;

import it.polimi.ingsw.common.model.events.BatchEndedEvent;
import it.polimi.ingsw.common.model.events.BatchStartedEvent;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.MatchController;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.common.model.enums.LobbyState;
import it.polimi.ingsw.network.rmi.ClientCallbackInterface;
import it.polimi.ingsw.network.exceptions.UserNotFoundException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Server-side user representation supporting both RMI and Socket connections.
 * Manages connected users, event notifications, and connection monitoring.
 */
public class User {

    /** Thread-safe list of active users */
    private final static List<User> activeUsers = new ArrayList<>();

    /** Thread-safe list of inactive users with ongoing games */
    private final static List<User> inactiveUsers = new ArrayList<>();

    /** Last ping timestamp for connection monitoring */
    private long lastPing;

    /** Unique connection identifier */
    protected final String connectionCode;

    /** User's username */
    protected String username;

    /** Current user state */
    private UserState state;

    /** User's current lobby */
    private Lobby lobby;

    /** RMI callback interface for client communication */
    private final ClientCallbackInterface callback;

    /**
     * @param connectionCode unique connection identifier
     * @param callback RMI callback interface for client communication, null for Socket
     */
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

    /**
     * Notifies multiple events, wrapping in batch markers if needed.
     *
     * @param events list of events to notify
     */
    public void notifyEvents(List<Event> events) {
        if (events.size() == 1) notifyEvent(events.getFirst());
        else {
            notifyEvent(new BatchStartedEvent());
            for (Event event : events)
                notifyEvent(event);
            notifyEvent(new BatchEndedEvent());
        }
    }

    /**
     * Notifies event to appropriate players based on visibility.
     *
     * @param event event to notify
     */
    public void notifyEvent(Event event) {
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

    /**
     * Sends event to this user's client.
     *
     * @param event event to send
     */
    public void sendEvent(Event event) {
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

    /**
     * Updates last ping timestamp.
     *
     * @param lastPing new ping timestamp
     */
    public void setLastPing(long lastPing) {
        this.lastPing = lastPing;
    }

    /**
     * Finds active user by username.
     *
     * @param username username to find
     * @return user with matching username
     * @throws UserNotFoundException if user not found
     */
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

    /**
     * Removes and returns inactive user by username.
     *
     * @param username username to find
     * @return inactive user or null if not found
     */
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

    /**
     * Removes user from system, handling lobby cleanup and game state.
     *
     * @param user user to remove
     */
    public static void removeUser(User user) {
        synchronized (activeUsers) {
            if (!activeUsers.contains(user)) return;
        }

        if (user.lobby != null) {
            List<Event> events = MatchController.getInstance().leaveGame(user.getUsername());
            if (user.lobby.getState() == LobbyState.IN_GAME) { // Player was gaming, add to inactive users
                synchronized (inactiveUsers) {
                    inactiveUsers.add(user);
                }
                user.notifyEvents(events);
            }
            else if (user.lobby.getState() == LobbyState.WAITING && !events.isEmpty()) {
                user.notifyEvents(events);
            }
        }

        synchronized (activeUsers) {
            activeUsers.remove(user);
        }
    }

}
