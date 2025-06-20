package it.polimi.ingsw.model.game;

import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.model.factory.CardFactoryLearnerMode;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.factory.ComponentFactory;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.player.ShipLearnerMode;

import java.util.*;
import java.util.function.Consumer;

public class BoardLearnerMode extends Board {

    public BoardLearnerMode(List<String> usernames) {
        super();

        List<ColorType> colors = Arrays.stream(ColorType.values()).toList();
        for (int i = 0; i < usernames.size(); i++) {
            PlayerData player = new PlayerData(usernames.get(i));

            Ship ship = new ShipLearnerMode();
            player.setShip(ship);

            ComponentFactory componentFactory = new ComponentFactory();
            this.commonComponents = new ArrayList<>(componentFactory.getComponents());
            this.mapIdComponents = new HashMap<>(componentFactory.getComponentsMap());
            componentFactory.getStartingCabins().get(colors.get(i)).insertComponent(player, 2, 3, 0, true);

            this.startingDeck.add(player);
        }

        CardFactory cardFactory = new CardFactoryLearnerMode();
        cardPile.addAll(cardFactory.getCards());
    }

    @Override
    public void shuffleCards() {
        Collections.shuffle(cardPile);
    }

    @Override
    public void startMatch(ModelFacade model) {
        // Do nothing, there aren't specific things to do in learner mode
    }

    @Override
    public Map<String, Integer> getCardPilesWatchMap() {
        throw new RuntimeException("Card piles aren't in learner mode flight");
    }

    @Override
    public int[] getBoardOrderPos() {
        return new int[]{4, 2, 1, 0};
    }

    @Override
    public void moveHourglass(String username, ModelFacade model, Consumer<List<GameEvent>> callback) {
        throw new RuntimeException("Hourglass is not in learner mode flight");
    }

    @Override
    protected int[] getRankingCreditsValues() {
        return new int[]{4, 3, 2, 1};
    }

    @Override
    protected int getRankingMostBeautifulShipReward() {
        return 2;
    }

}
