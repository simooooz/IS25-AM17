package it.polimi.ingsw.model.game;

import it.polimi.ingsw.common.model.events.Event;
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

/**
 * Learner mode implementation of the game board that extends the base Board class.
 * This simplified version is designed for new players to learn the game mechanics
 * without the complexity of advanced features like time management, card pile watching and aliens.
 */
public class BoardLearnerMode extends Board {

    /**
     * @param usernames the list of player usernames to initialize the game with
     * @throws IllegalArgumentException if usernames list is null or empty
     */
    public BoardLearnerMode(List<String> usernames) {
        super();

        ComponentFactory componentFactory = new ComponentFactory();
        this.commonComponents = new ArrayList<>(componentFactory.getComponents());
        this.mapIdComponents = new HashMap<>(componentFactory.getComponentsMap());

        List<ColorType> colors = Arrays.stream(ColorType.values()).toList();
        for (int i = 0; i < usernames.size(); i++) {
            PlayerData player = new PlayerData(usernames.get(i));

            Ship ship = new ShipLearnerMode();
            player.setShip(ship);

            componentFactory.getStartingCabins().get(colors.get(i)).insertComponent(player, 2, 3, 0, true);

            this.startingDeck.add(player);
        }

        CardFactory cardFactory = new CardFactoryLearnerMode();
        cardPile.addAll(cardFactory.getCards());
    }

    /**
     * Shuffles the card pile using simple randomization without any restrictions.
     * Unlike advanced mode, learner mode does not impose constraints on which
     * cards can appear at the top of the deck after shuffling.
     */
    @Override
    public void shuffleCards() {
        Collections.shuffle(cardPile);
    }

    /**
     * Starts a match in learner mode with minimal initialization.
     * Since learner mode focuses on simplicity, no special setup or
     * timing mechanisms are required when starting a match.
     *
     * @param model the model facade for managing game state (unused in learner mode)
     */
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

    /**
     * Hourglass functionality is not supported in learner mode.
     *
     * @param username the username of the player (unused)
     * @param model    the model facade (unused)
     * @param callback the callback function (unused)
     * @throws RuntimeException always thrown as hourglass mechanics are not available
     *                          in learner mode
     */
    @Override
    public void moveHourglass(String username, ModelFacade model, Consumer<List<Event>> callback) {
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
