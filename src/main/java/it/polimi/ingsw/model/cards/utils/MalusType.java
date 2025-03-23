package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.CardState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.SpecialCargoHoldsComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;
import java.util.Optional;

public enum MalusType {
    DAYS {
        @Override
        public CardState resolve(int penaltyNumber, Board board, PlayerData player) throws Exception {
            board.movePlayer(player, -1*penaltyNumber);
            return CardState.DONE;
        }
    },

    GOODS {
        @Override
        public CardState resolve(int penaltyNumber, Board board, PlayerData player) throws Exception {
            return CardState.WAIT_REMOVE_GOOD;
            int penalties = penaltyNumber;
            ColorType[] goodTypes = ColorType.values(); // Goods in order of value

            for (ColorType goodType : goodTypes) {
                int nOfGoods = player.getShip().getGoods().get(goodType);
                if (nOfGoods > 1) { // The user has to choose from where withdrawal
                    while (penalties > 0 || nOfGoods > 1) {
                        Optional<SpecialCargoHoldsComponent> chosenComponentOpt = Optional.empty(); // View => Ask the user
                        SpecialCargoHoldsComponent chosenComponent = chosenComponentOpt.orElseThrow();
                        chosenComponent.unloadGood(goodType, player.getShip());
                        penalties--;
                        nOfGoods--;
                    }
                }
                if (nOfGoods == 1 && penalties > 0) { // Only one, withdraw automatically
                    List<SpecialCargoHoldsComponent> list = player.getShip().getComponentByType(SpecialCargoHoldsComponent.class);
                    SpecialCargoHoldsComponent component = list.stream()
                        .filter(cargo -> cargo.getGoods().contains(goodType))
                        .findFirst()
                        .orElseThrow();
                    component.unloadGood(goodType, player.getShip());
                    penalties--;
                }
            }

            if (penalties > 0) { // Not enough goods, remove batteries
                if (player.getShip().getBatteries() > penalties) { // Ask the user where
                    while (penalties > 0) {
                        Optional<BatteryComponent> chosenComponentOpt = Optional.empty(); // View
                        BatteryComponent chosenComponent = chosenComponentOpt.orElseThrow();
                        chosenComponent.useBattery(player.getShip());
                        penalties--;
                    }
                }
                else { // Remove all batteries
                    List<BatteryComponent> batteryComponents = player.getShip().getComponentByType(BatteryComponent.class);
                    for (BatteryComponent batteryComponent : batteryComponents)
                        while (batteryComponent.getBatteries() > 0)
                            batteryComponent.useBattery(player.getShip());

                }
            }

        }
    },

    CREW {
        @Override
        public CardState resolve(int penaltyNumber, Board board, PlayerData player) throws Exception {
            return CardState.WAIT_REMOVE_CREW;
        }
    };

    public abstract CardState resolve(int penaltyNumber, Board board, PlayerData player) throws Exception;
}
