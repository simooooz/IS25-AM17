package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.SpecialCargoHoldsComponent;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.Ship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GoodCommand implements Command {

    private final String username;
    private final Board board;
    Map<ColorType, Integer> deltaGood;
    private final Map<SpecialCargoHoldsComponent, List<ColorType>> cargoHolds;
    private final List<BatteryComponent> batteries;

    public GoodCommand(String username, Board board, Map<SpecialCargoHoldsComponent, List<ColorType>> cargoHolds, List<BatteryComponent> batteries) {
        this.username = username;
        this.board = board;
        this.deltaGood = new HashMap<>();
        this.cargoHolds = cargoHolds;
        this.batteries = batteries;
    }

    @Override
    public void execute(Card card) {
        Ship ship = this.board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);

        // increases goods value for each good which is present after the call
        for (SpecialCargoHoldsComponent c : cargoHolds.keySet())
            for (int i = 0; i < cargoHolds.get(c).size(); i++)
                deltaGood.put(cargoHolds.get(c).get(i), deltaGood.get(cargoHolds.get(c).get(i)) + 1);

        // decreases goods value for each good which is present before the call
        for (ColorType good : ColorType.values())
            deltaGood.put(good, deltaGood.get(good) - ship.getGoods().get(good));

        if (card.getPlayersState().get(username) == PlayerState.WAIT_GOODS)
            card.doSpecificCheck(PlayerState.WAIT_GOODS, null, deltaGood, batteries, username, board);
        else
            card.doSpecificCheck(PlayerState.WAIT_REMOVE_GOODS, 0, deltaGood, batteries, username, board);

        for (SpecialCargoHoldsComponent component : cargoHolds.keySet()) {
            for (ColorType good : component.getGoods()) {
                component.unloadGood(good, ship);
            }
            for (ColorType good : cargoHolds.get(component)) {
                component.loadGood(good, ship);
            }
        }

        batteries.forEach(batteryComponent -> batteryComponent.useBattery(ship));

    }

    private void checkInput(Ship ship) {
        for (Component component : cargoHolds.keySet())
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Cargo hold component not valid");

        for (SpecialCargoHoldsComponent component : cargoHolds.keySet())
            if (cargoHolds.get(component).size() > component.getNumber())
                throw new GoodNotValidException("Too many goods in cargo hold");

        for (Component component : batteries)
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Battery component not valid");

        boolean enoughBatteries = batteries.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .allMatch(entry -> entry.getValue() <= entry.getKey().getBatteries());
        if (!enoughBatteries)
            throw new CabinComponentNotValidException("Not enough batteries in a single component");

        // TODO ulteriore controllo se la merce è rossa e il cargo special?
    }

}
