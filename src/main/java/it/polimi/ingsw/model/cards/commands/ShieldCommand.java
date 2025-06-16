package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.Optional;

public class ShieldCommand implements Command {

    private final ModelFacade model;
    private final String username;
    private final Board board;
    private final Optional<BatteryComponent> battery;

    public ShieldCommand(ModelFacade model, Board board, String username, BatteryComponent battery) {
        this.model = model;
        this.username = username;
        this.board = board;
        this.battery = Optional.ofNullable(battery);
    }

    @Override
    public boolean execute(Card card) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);

        battery.ifPresent(batteryComponent -> batteryComponent.useBattery(ship));
        return card.doCommandEffects(PlayerState.WAIT_SHIELD, battery.isPresent(), model, board, username);
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
