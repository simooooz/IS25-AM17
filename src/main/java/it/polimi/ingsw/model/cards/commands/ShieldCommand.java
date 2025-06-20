package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;


public class ShieldCommand implements Command {

    private final ModelFacade model;
    private final String username;
    private final Board board;
    private final BatteryComponent battery;

    public ShieldCommand(ModelFacade model, Board board, String username, BatteryComponent battery) {
        this.model = model;
        this.username = username;
        this.board = board;
        this.battery = battery;
    }

    @Override
    public boolean execute(Card card) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);

        if (battery != null)
            battery.useBattery(ship);

        return card.doCommandEffects(PlayerState.WAIT_SHIELD, battery != null, model, board, username);
    }

    private void checkInput(Ship ship) {
        if (battery == null) return;

        if (ship.getDashboard(battery.getY(), battery.getX()).isEmpty() || !ship.getDashboard(battery.getY(), battery.getX()).get().equals(battery))
            throw new ComponentNotValidException("Battery component not valid");

        if (battery.getBatteries() == 0)
            throw new BatteryComponentNotValidException("Not enough batteries");
    }

}