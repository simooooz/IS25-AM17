package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class AbandonedStationCard extends Card{
    private final int crew;
    private final int days;
    private final Map<ColorType, Integer> goods;;

    public AbandonedStationCard(int level, boolean isLearner, int crew, int days, Map<ColorType, Integer> goods) {
        super(level, isLearner);
        this.crew = crew;
        this.days = days;
        this.goods = goods;
    }

    public int getCrew() {
        return crew;
    }

    public int getDays() {
        return days;
    }

    public Map<ColorType, Integer> getGoods() {
        return goods;
    }

    @Override
    public void resolve(Board board){
        List<AbstractMap.SimpleEntry<PlayerData, Integer>> players = board.getPlayers();
        // iterate on the list of player -> for now I've not implemented if a player does not want to
        // play this card, it's only to iterate on the list of player
        for (AbstractMap.SimpleEntry<PlayerData, Integer> entry : players) {
            PlayerData player = entry.getKey();

            int crew = player.getShip().getCrew();
            // check if the player can really play the card
            if (crew > getCrew()) {
                getGoods().keySet().forEach(goodType -> {
                    int rewardTypeG = getGoods().get(goodType);
                    int availableG = board.getAvailableGoods().get(goodType);
                    int playerG = player.getShip().getGoods().get(goodType);
                    int toDecrease = Math.min(availableG, rewardTypeG);
                    while (toDecrease > 0) {
                        //chiedo alla view il componente sul quale inserire la merce
                        // CargoComponent c1 = Funzionecherichiedeilcomponenteallview()
                        // c1.loadGood(goodType);
                        toDecrease--;
                    }
                    board.getAvailableGoods().put(goodType, availableG - toDecrease);
                    player.getShip().getGoods().put(goodType, playerG + toDecrease);
                });
                board.movePlayer(player, getDays()* -1);
            }
        }
    }
}
