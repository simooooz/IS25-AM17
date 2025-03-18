package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.game.Board;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private GameState state;
//    private final String id;
    private final List<String> players;
    private Board board;

    public Game(List<String> usernames) {
//        this.id = UUID.randomUUID().toString();
        this.players = new ArrayList<>(usernames);
        this.board = new Board(usernames);
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Board getBoard() {
        return board;
    }

}
