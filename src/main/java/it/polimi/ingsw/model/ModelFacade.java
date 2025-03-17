package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;

public class ModelFacade {

    private final Board board;

    public ModelFacade(List<String> usernames) {
        this.board = new Board(usernames);
    }

    public List<PlayerData> getPlayersByPos() {
        return board.getPlayersByPos();
    }

    public void rotateHourglass() {
        board.getTimeManagment().rotateHourglass();
    }

    public int getTimeLeft() {
        return board.getTimeManagment().getTimeLeft();
    }

    public void decrementTimeLeft() {
        board.getTimeManagment().decrementTimeLeft();
    }

    public Card drawCard() throws Exception {
        return board.drawCard();
    }

    public void insertComponent(String username,  Component component, int row, int col) throws Exception {
        Ship ship = board.getPlayer(username).getShip();
        component.insertComponent(ship, row, col);
    }

    public void rotateComponent(Component component, boolean clockwise) throws Exception {
        component.rotateComponent(clockwise);
    }

    public void useBatteries(String username, List<BatteryComponent> batteryComponents) throws Exception {
        for
    }

    // UPDATE BATTERIES, CABIN
    // ACTIVATE CANNONS AND ENGINES

}