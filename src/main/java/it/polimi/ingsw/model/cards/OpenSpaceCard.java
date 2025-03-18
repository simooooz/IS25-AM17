package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;

public class OpenSpaceCard extends Card {

    public OpenSpaceCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    @Override
    protected boolean requiresPlayerInteraction(PlayerData player) {
        return player.getShip().hasDoubleEngines() && player.getShip().getBatteries()!=0;
    }

    @Override
    protected void doResolve(Game game, PlayerData player, Object data) throws Exception {
        Ship ship = player.getShip();

        if (data != null && data instanceof List) {
            // the list may contain duplicates (e.g. in case more batteries are activated by the same component)
            List<BatteryComponent> selectedBatteries = (List<BatteryComponent>) data;

            if (selectedBatteries.size()>ship.getBatteries()) throw new  Exception("batteries exhausted");
            if (
                    selectedBatteries.size() > ship.getComponentByType(EngineComponent.class).stream()
                                                    .filter(e -> e.getIsDouble())
                                                    .toList()
                                                    .size()
            ) throw new  Exception("the number of batteries used exceeds the number of dual motors available");

            selectedBatteries.forEach(batteryComponent -> {
                try {
                    batteryComponent.useBattery(ship);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            game.getBoard().movePlayer(player, ship.calcEnginePower(selectedBatteries.size()));
        } else {
            game.getBoard().movePlayer(player, ship.calcEnginePower(0));
        }
    }

}
