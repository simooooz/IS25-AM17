package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.SpecialCargoHoldsComponent;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SmugglersCard extends Card {
    private final int firePower;            // firePower of the smugglers' ship
    private final int lostGoods;            // number of goods to lose if player doesn't win the battle with smugglers
    private final List<ColorType> goods;    // reward
    private final int days;                 // number of days to lose if player win the battle and decides to pick the reward (goods)

    public SmugglersCard(
            int level,
            boolean isLearner,

            int firePower,
            int lostGoods,
            List<ColorType> goods,
            int days
    ) {
        super(level, isLearner);
        this.firePower = firePower;
        this.lostGoods = lostGoods;
        this.goods = goods;
        this.days = days;
    }

    public int getFirePower() {
        return firePower;
    }

    public int getLostGoods() {
        return lostGoods;
    }

    public List<ColorType> getGoods() {
        return goods;
    }

    public int getDays() {
        return days;
    }

    @Override
    public void resolve(Board board) throws Exception {
        List<AbstractMap.SimpleEntry<PlayerData, Integer>> players = board.getPlayers()
                .stream()
                .sorted(Comparator.comparing((AbstractMap.SimpleEntry<PlayerData, Integer> entry) -> entry.getValue()).reversed())
                .toList();
        players.forEach(p -> {
            Ship ship = p.getKey().getShip();
            double fP = ship.calcFirePower(ship.getComponentByType(CannonComponent.class));
            if (fP > firePower) {
//                The player here has the right to claim the reward if he wants to: in case, he will lose #days flight days.
//                In addition, the player has the right to discard some goods already in his possession. Since
//                he has free choice, as long as the holds match the color of the goods, of where to
//                add/remove goods, for the moment it is assumed in the code below that there is an interaction
//                with the view, and we receive the reference component (hold) on which to load/unload goods
//                Optional<Component> chosenComponent;
//                if (chosenComponent.isPresent()) {
//                    if (!(chosenComponent.get() instanceof SpecialCargoHoldsComponent)) throw new Exception("il componente selezionato non è un cargo");
//                }

                return;     // smugglers have been defeated, the remaining players (if any) are not affected
            } else if (fP < firePower) {
//                The player loses the number of goods listed in lostGoods in order of value
            }
        });
    }
}
