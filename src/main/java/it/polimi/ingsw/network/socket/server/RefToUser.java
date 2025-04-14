package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.exceptions.UserNotFoundException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Server's point of view of the client. This class handles connected users
 */
public class RefToUser {

    private final static List<RefToUser> users = new ArrayList<>();

    private final String connectionCode;
    private String username;
    private GameController gameController;

    public RefToUser(String connectionCode) {
        this.connectionCode = connectionCode;
        this.username = null;
        this.gameController = null;

        synchronized (users) {
            users.add(this);
        }
    }

    public void send(Message message, CompletableFuture<Void> completion) {
        try {
            Server.getInstance().send(connectionCode, message, completion);
        } catch (ServerException e) {
            System.err.println("[USER] Error while sending message: " + e.getMessage());
            // Everything should be closed
        }
    }

    public void receive(Message message) {
        message.execute(this);
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public String getUsername() {
        return username;
    }

    public boolean setUsername(String username) {
        synchronized (users) {
            boolean taken = users.stream().anyMatch(user -> user.getUsername() != null && user.getUsername().equals(username));
            if (!taken)
                this.username = username;
            return !taken;
        }
    }

    public static RefToUser getUser(String username) throws UserNotFoundException {
        List<RefToUser> temp;
        synchronized (users) {
            temp = new ArrayList<>(users);
        }
        return temp.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

    public static boolean isUsernameTaken(String username) {
        synchronized (users) {
            return users.stream().anyMatch(user -> user.getUsername() != null && user.getUsername().equals(username));
        }
    }

    // todo è sicuro/devo fare una get del connectionCode?
    public String getConnectionCode() {
        return connectionCode;
    }
}
