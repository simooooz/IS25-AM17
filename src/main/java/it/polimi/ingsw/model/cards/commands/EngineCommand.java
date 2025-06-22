package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EngineCommand implements Command {

    private final ModelFacade model;
    private final String username;
    private final Board board;
    private final List<BatteryComponent> batteries;
    private final List<EngineComponent> engines;

    public EngineCommand(ModelFacade model, Board board, String username, List<BatteryComponent> batteries, List<EngineComponent> engines) {
        this.model = model;
        this.username = username;
        this.board = board;
        this.batteries = batteries;
        this.engines = engines;
    }

    @Override
    public boolean execute(Card card) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);

        int singleEnginePower = ship.getComponentByType(EngineComponent.class).stream()
                .filter(engine -> !engine.getIsDouble())
                .toList().size();
        int doubleEnginePower = engines.stream().mapToInt(EngineComponent::calcPower).sum();
        int userEnginePower = singleEnginePower + doubleEnginePower;
        if (userEnginePower > 0 && ship.getEngineAlien())
            userEnginePower += 2;

        batteries.forEach(batteryComponent -> batteryComponent.useBattery(ship));
        return card.doCommandEffects(PlayerState.WAIT_ENGINES, userEnginePower, model, board, username);
    }

    private void checkInput(Ship ship) {
        for (Component component : batteries)
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Battery component not valid");

        for (EngineComponent component : engines) {
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Engine component not valid");
            else if (!component.getIsDouble())
                throw new ComponentNotValidException("Engine component " + component.getId() + "is not double");
        }

        if (batteries.size() != engines.size())
            throw new RuntimeException("Inconsistent number of batteries");

        boolean enoughBatteries = batteries.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .allMatch(entry -> entry.getValue() <= entry.getKey().getBatteries());
        if (!enoughBatteries)
            throw new BatteryComponentNotValidException("Not enough batteries");

        if (engines.size() != engines.stream().distinct().count())
            throw new ComponentNotValidException("Duplicate engines");
    }

}
