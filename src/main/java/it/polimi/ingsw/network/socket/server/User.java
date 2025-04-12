package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.exceptions.UserNotFoundException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class User {

    private final static List<User> users = new ArrayList<>();

    private final String connectionCode;
    private String username;
    private GameController gameController;

    public User(String connectionCode) {
        this.connectionCode = connectionCode;
        this.username = null;
        this.gameController = null;

        synchronized (users) {
            users.add(this);
        }

    }

    public void send(Message message) {
        try {
            Server.getInstance().sendObject(connectionCode, message);
        } catch (ServerException e) {
            System.err.println("[USER] Error while sending message: " + e.getMessage());
            // Everything should be closed
        }
    }

    public void receive(Message message) {
        try {
            message.execute(this);
        } catch (RuntimeException e) {
            System.err.println("[USER] Receive method has caught a RuntimeException: " + e.getMessage());
            this.send(new ErrorMessage());
        }
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

    public static User getUser(String username) throws UserNotFoundException {
        List <User> temp;
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

}
