package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.Optional;

public class ShieldCommand implements Command {

    private final String username;
    private final Board board;
    private final Optional<BatteryComponent> battery;

    public ShieldCommand(String username, Board board, BatteryComponent battery) {
        this.username = username;
        this.board = board;
        this.battery = Optional.ofNullable(battery);
    }

    @Override
    public void execute(Card card) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);

        if (battery.isPresent()) {
            battery.get().useBattery(ship);
            card.doCommandEffects(PlayerState.WAIT_SHIELD, true, username, board);
        }
        else
            card.doCommandEffects(PlayerState.WAIT_SHIELD, false, username, board);
    }

    private void checkInput(Ship ship) {
        if (battery.isEmpty()) return;
        BatteryComponent component = battery.get();

        if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
            throw new ComponentNotValidException("Battery component not valid");

        if (component.getBatteries() == 0)
            throw new BatteryComponentNotValidException("Not enough batteries");
    }

}
