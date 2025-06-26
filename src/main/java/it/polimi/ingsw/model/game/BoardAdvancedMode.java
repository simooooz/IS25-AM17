package it.polimi.ingsw.model.game;

import it.polimi.ingsw.common.dto.BoardDTO;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.model.factory.CardFactoryAdvancedMode;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.factory.ComponentFactory;
import it.polimi.ingsw.model.game.objects.Time;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.player.ShipAdvancedMode;

import java.util.*;
import java.util.function.Consumer;

/**
 * Advanced mode implementation of the game board that extends the base Board class.
 * This version includes time management features and enhanced gameplay mechanics
 * such as timed rounds and specialized card handling.
 *
 * <p>The BoardAdvancedMode provides additional functionality over the standard board:
 * <ul>
 * <li>Time-based gameplay with hourglass mechanics</li>
 * <li>Card pile watching system for tracking player interactions</li>
 * <li>Enhanced ship construction with advanced mode ships</li>
 * <li>Modified scoring and ranking systems</li>
 * </ul>
 *
 * @author Generated Javadoc
 * @version 1.0
 * @since 1.0
 */
public class BoardAdvancedMode extends Board {

    /**
     * Time management system that handles the hourglass timer and round timing.
     */
    private final Time timeManagement;

    /**
     * Map tracking which players are watching specific card piles.
     * Key: player username, Value: card pile index being watched.
     */
    private final Map<String, Integer> cardPilesWatchMap;

    /**
     * Constructs a new BoardAdvancedMode with the specified player usernames.
     * Initializes all advanced mode components including time management,
     * specialized ships, and enhanced card factory.
     *
     * <p>During initialization:
     * <ul>
     * <li>Creates time management system</li>
     * <li>Sets up component factory with advanced components</li>
     * <li>Assigns colors to players and creates advanced mode ships</li>
     * <li>Places starting cabins for each player</li>
     * <li>Initializes card pile with advanced mode cards</li>
     * </ul>
     *
     * @param usernames the list of player usernames to initialize the game with
     * @throws IllegalArgumentException if usernames list is null or empty
     */
    public BoardAdvancedMode(List<String> usernames) {
        super();
        this.cardPilesWatchMap = new HashMap<>();
        this.timeManagement = new Time();
        ComponentFactory componentFactory = new ComponentFactory();
        this.commonComponents = new ArrayList<>(componentFactory.getComponents());
        this.mapIdComponents = new HashMap<>(componentFactory.getComponentsMap());
        List<ColorType> colors = Arrays.stream(ColorType.values()).toList();
        for (int i = 0; i < usernames.size(); i++) {
            PlayerData player = new PlayerData(usernames.get(i));

            Ship ship = new ShipAdvancedMode();
            player.setShip(ship);

            componentFactory.getStartingCabins().get(colors.get(i)).insertComponent(player, 2, 3, 0, true);

            this.startingDeck.add(player);
        }

        CardFactory cardFactory = new CardFactoryAdvancedMode();
        cardPile.addAll(cardFactory.getCards());
    }

    /**
     * Moves the hourglass position and manages the timer for the current player's turn.
     * In advanced mode, the hourglass can only be moved by players who have completed
     * their ship construction when the hourglass is at position 1.
     *
     * <p>This method validates that the player is eligible to move the hourglass
     * and then starts the timer for the next phase of the game.
     *
     * @param username the username of the player attempting to move the hourglass
     * @param model    the model facade for game state management
     * @param callback the callback function to handle generated game events
     * @throws IllegalArgumentException if the player cannot rotate the hourglass
     *                                  because they haven't finished building their ship
     */
    @Override
    public void moveHourglass(String username, ModelFacade model, Consumer<List<Event>> callback) {
        if (timeManagement.getHourglassPos() == 1)
            getPlayersByPos().stream()
                    .filter(player -> player.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("You can't rotate hourglass because you haven't finished to build your ship"));

        timeManagement.startTimer(model, callback);
    }

    /**
     * Shuffles the card pile ensuring that the first card is not a level 2 card.
     * This method repeatedly shuffles until a non-level-2 card is at the top
     * of the deck, maintaining game balance in advanced mode.
     *
     * <p>The shuffling continues until the top card's level is not equal to 2,
     * preventing immediate access to high-level cards at the start of rounds.
     */
    @Override
    public void shuffleCards() {
        do {
            Collections.shuffle(cardPile);
        } while (cardPile.getFirst().getLevel() != 2);
    }

    /**
     * Starts a match in advanced mode by initializing the timer system.
     * This method begins the time management for the entire game session.
     *
     * @param model the model facade for managing game state during the match
     */
    @Override
    public void startMatch(ModelFacade model) {
        timeManagement.startTimer(model, (_) -> {
        });
    }

    /**
     * Picks a new card from the card pile, handling the end-of-card effects
     * for the previous card if applicable. In advanced mode, this includes
     * additional processing for card transitions.
     *
     * <p>If there was a previously active card (cardPilePos >= 0), this method
     * triggers its end-card effects before proceeding with the standard
     * card picking logic from the parent class.
     *
     * @param model the model facade for managing game state changes
     */
    @Override
    public void pickNewCard(ModelFacade model) {
        if (cardPilePos >= 0)
            cardPile.get(cardPilePos).endCard(this);
        super.pickNewCard(model);
    }

    /**
     * Returns the map tracking which card piles each player is watching.
     * This feature allows players to monitor specific card piles for
     * strategic planning in advanced mode.
     *
     * @return an unmodifiable view of the card piles watch map where
     * keys are player usernames and values are card pile indices
     */
    @Override
    public Map<String, Integer> getCardPilesWatchMap() {
        return cardPilesWatchMap;
    }

    /**
     * Returns the board order positions for advanced mode gameplay.
     * These positions determine the turn order and player positioning
     * on the advanced mode board.
     *
     * @return an array of integers representing the board order positions [6, 3, 1, 0]
     */
    @Override
    public int[] getBoardOrderPos() {
        return new int[]{6, 3, 1, 0};
    }

    /**
     * Returns the credit values awarded for different ranking positions
     * in advanced mode. These values are used for end-game scoring
     * and determine the credits players receive based on their final ranking.
     *
     * @return an array of credit values for ranking positions [8, 6, 4, 2]
     */
    @Override
    protected int[] getRankingCreditsValues() {
        return new int[]{8, 6, 4, 2};
    }

    /**
     * Returns the reward value for having the most beautiful ship in advanced mode.
     * This bonus is awarded to the player whose ship is deemed most aesthetically
     * pleasing according to the game's criteria.
     *
     * @return the credit reward value (4) for the most beautiful ship
     */
    @Override
    protected int getRankingMostBeautifulShipReward() {
        return 4;
    }

    /**
     * Converts the current board state to a Data Transfer Object (DTO)
     * including advanced mode specific information such as time remaining
     * and hourglass position.
     *
     * <p>This method extends the base board DTO with additional fields:
     * <ul>
     * <li>timeLeft - remaining time for the current phase</li>
     * <li>hourglassPos - current position of the hourglass</li>
     * </ul>
     *
     * @return a BoardDTO containing the complete board state including
     * advanced mode specific timing information
     */
    @Override
    public BoardDTO toDto() {
        BoardDTO dto = super.toDto();
        dto.timeLeft = timeManagement.getTimeLeft();
        dto.hourglassPos = timeManagement.getHourglassPos();
        return dto;
    }
}
