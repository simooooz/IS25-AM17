package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CannonCommand implements Command {

    private final String username;
    private final Board board;
    private final List<BatteryComponent> batteries;
    private final List<CannonComponent> cannons;

    public CannonCommand(String username, Board board, List<BatteryComponent> batteries, List<CannonComponent> cannons) {
        this.username = username;
        this.board = board;
        this.batteries = batteries;
        this.cannons = cannons;
    }

    @Override
    public void execute(Card card) {
        Ship ship = this.board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);
        card.doSpecificCheck(PlayerState.WAIT_CANNONS, cannons, username, board);

        double singleCannonPower = ship.getComponentByType(CannonComponent.class).stream()
                .filter(cannon -> !cannon.getIsDouble())
                .mapToDouble(CannonComponent::calcPower)
                .sum();
        double doubleCannonPower = cannons.stream().mapToDouble(CannonComponent::calcPower).sum();
        double userCannonPower = singleCannonPower + doubleCannonPower + (ship.getCannonAlien() ? 2 : 0);

        batteries.forEach(batteryComponent -> batteryComponent.useBattery(ship));
        card.doCommandEffects(PlayerState.WAIT_CANNONS, userCannonPower, username, board);
    }

    private void checkInput(Ship ship) {
        for (Component component : batteries)
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Battery component not valid");

        for (Component component : cannons)
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Cannon component not valid");

        if (batteries.size() != cannons.size())
            throw new RuntimeException("Inconsistent number of batteries components");

        boolean enoughBatteries = batteries.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .allMatch(entry -> entry.getValue() <= entry.getKey().getBatteries());
        if (!enoughBatteries)
            throw new BatteryComponentNotValidException("Not enough batteries in a single component");

        if (cannons.size() != cannons.stream().distinct().count())
            throw new ComponentNotValidException("Duplicate cannons");
    }

}
