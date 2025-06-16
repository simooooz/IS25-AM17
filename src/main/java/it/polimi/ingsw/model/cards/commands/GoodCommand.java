package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.SpecialCargoHoldsComponent;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GoodCommand implements Command {

    private final String username;
    private final Board board;
    private final ModelFacade model;
    Map<ColorType, Integer> deltaGood;
    private final Map<SpecialCargoHoldsComponent, List<ColorType>> newDisposition;
    private final List<BatteryComponent> batteries;

    public GoodCommand(ModelFacade model, Board board, String username, Map<SpecialCargoHoldsComponent, List<ColorType>> newDisposition, List<BatteryComponent> batteries) {
        this.username = username;
        this.board = board;
        this.model = model;
        this.deltaGood = new HashMap<>();
        for(ColorType c : ColorType.values()) {
            this.deltaGood.put(c, 0);
        }
        this.newDisposition = newDisposition;
        this.batteries = batteries;
    }

    @Override
    public boolean execute(Card card) {
        Ship ship = this.board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);

        // increases goods value for each good which is present after the call
        for (SpecialCargoHoldsComponent c : newDisposition.keySet())
            for (int i = 0; i < newDisposition.get(c).size(); i++)
                deltaGood.put(newDisposition.get(c).get(i), deltaGood.get(newDisposition.get(c).get(i)) + 1);

        // decreases goods value for each good which is present before the call
        for (ColorType good : ColorType.values())
            deltaGood.put(good, deltaGood.get(good) - ship.getGoods().get(good));

        if (model.getPlayerState(username) == PlayerState.WAIT_GOODS)
            card.doSpecificCheck(PlayerState.WAIT_GOODS, null, deltaGood, batteries, username, board);
        else
            card.doSpecificCheck(PlayerState.WAIT_REMOVE_GOODS, 0, deltaGood, batteries, username, board);

        List<SpecialCargoHoldsComponent> componentsInShip = ship.getComponentByType(SpecialCargoHoldsComponent.class);
        for (SpecialCargoHoldsComponent component : componentsInShip) {
            List<ColorType> currentGoods = new ArrayList<>(component.getGoods());
            for (ColorType good : currentGoods) {
                component.unloadGood(good, ship);
            }
            if (newDisposition.containsKey(component))
                for (ColorType good : newDisposition.get(component))
                    component.loadGood(good, ship);
        }

        batteries.forEach(batteryComponent -> batteryComponent.useBattery(ship));
        return card.doCommandEffects(model.getPlayerState(username), model, board, username);
    }

    private void checkInput(Ship ship) {
        for (Component component : newDisposition.keySet())
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Cargo hold component not valid");

        for (SpecialCargoHoldsComponent component : newDisposition.keySet())
            if (newDisposition.get(component).size() > component.getNumber())
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
    }

}
