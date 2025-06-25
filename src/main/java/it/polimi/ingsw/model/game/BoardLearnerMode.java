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

/**
 * Learner mode implementation of the game board that extends the base Board class.
 * This simplified version is designed for new players to learn the game mechanics
 * without the complexity of advanced features like time management and card pile watching.
 *
 * <p>The BoardLearnerMode provides a streamlined gameplay experience:
 * <ul>
 * <li>Simplified card shuffling without level restrictions</li>
 * <li>No time-based mechanics or hourglass functionality</li>
 * <li>No card pile watching system</li>
 * <li>Reduced scoring values for a more forgiving learning environment</li>
 * <li>Specialized learner mode ships with simplified mechanics</li>
 * </ul>
 *
 * <p>This mode is ideal for:
 * <ul>
 * <li>New players learning the basic game mechanics</li>
 * <li>Tutorial or training sessions</li>
 * <li>Players who prefer a more relaxed gaming experience</li>
 * </ul>
 *
 * @author Generated Javadoc
 * @version 1.0
 * @since 1.0
 */
public class BoardLearnerMode extends Board {

    /**
     * Constructs a new BoardLearnerMode with the specified player usernames.
     * Initializes all learner mode components including simplified ships
     * and learner-specific card factory.
     *
     * <p>During initialization:
     * <ul>
     * <li>Sets up component factory with standard components</li>
     * <li>Assigns colors to players and creates learner mode ships</li>
     * <li>Places starting cabins for each player at position (2,3,0)</li>
     * <li>Initializes card pile with learner mode cards</li>
     * </ul>
     *
     * <p>The initialization process is similar to other board modes but uses
     * specialized learner components to provide a simplified gaming experience.
     *
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
     *
     * <p>This simplified shuffling mechanism makes the game more predictable
     * and easier for new players to understand, removing strategic complications
     * related to card positioning.
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
     * <p>This method intentionally performs no operations as learner mode
     * is designed to be straightforward without complex initialization procedures.
     *
     * @param model the model facade for managing game state (unused in learner mode)
     */
    @Override
    public void startMatch(ModelFacade model) {
        // Do nothing, there aren't specific things to do in learner mode
    }

    /**
     * Card pile watching is not supported in learner mode.
     * This feature is disabled to keep the gameplay simple and focused
     * on basic mechanics rather than advanced strategic elements.
     *
     * @return this method does not return normally
     * @throws RuntimeException always thrown as card piles watching is not available
     *                          in learner mode
     */
    @Override
    public Map<String, Integer> getCardPilesWatchMap() {
        throw new RuntimeException("Card piles aren't in learner mode flight");
    }

    /**
     * Returns the board order positions for learner mode gameplay.
     * These simplified positions provide a more straightforward turn order
     * and player positioning system suitable for learning players.
     *
     * @return an array of integers representing the board order positions [4, 2, 1, 0]
     */
    @Override
    public int[] getBoardOrderPos() {
        return new int[]{4, 2, 1, 0};
    }

    /**
     * Hourglass functionality is not supported in learner mode.
     * Time-based mechanics are disabled to provide a pressure-free
     * learning environment where players can take their time to understand
     * the game mechanics.
     *
     * @param username the username of the player (unused)
     * @param model    the model facade (unused)
     * @param callback the callback function (unused)
     * @throws RuntimeException always thrown as hourglass mechanics are not available
     *                          in learner mode
     */
    @Override
    public void moveHourglass(String username, ModelFacade model, Consumer<List<GameEvent>> callback) {
        throw new RuntimeException("Hourglass is not in learner mode flight");
    }

    /**
     * Returns the reduced credit values awarded for different ranking positions
     * in learner mode. These lower values create a more forgiving scoring system
     * that encourages experimentation and learning rather than competitive play.
     *
     * <p>The reduced scoring helps new players focus on understanding game
     * mechanics rather than worrying about maximizing points.
     *
     * @return an array of credit values for ranking positions [4, 3, 2, 1]
     */
    @Override
    protected int[] getRankingCreditsValues() {
        return new int[]{4, 3, 2, 1};
    }

    /**
     * Returns the reduced reward value for having the most beautiful ship
     * in learner mode. This lower bonus maintains the aesthetic aspect of
     * the game while keeping scoring modest and encouraging for new players.
     *
     * <p>The reduced reward helps maintain game balance while still recognizing
     * creative ship construction efforts.
     *
     * @return the credit reward value (2) for the most beautiful ship in learner mode
     */
    @Override
    protected int getRankingMostBeautifulShipReward() {
        return 2;
    }
}
