package it.polimi.ingsw.model.game;

import it.polimi.ingsw.common.dto.BoardDTO;
import it.polimi.ingsw.common.dto.GameStateDTOFactory;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.common.model.events.game.PlayersPositionUpdatedEvent;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.model.exceptions.PlayerNotFoundException;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.model.player.PlayerData;


import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Abstract base class representing the game board and managing core game state.
 * The Board serves as the central hub for managing players, components, cards,
 * and game progression throughout the space adventure experience.
 * <p>
 * The Board class encapsulates several critical game management responsibilities:
 * - Player positioning and movement along the flight path
 * - Component distribution and availability management
 * - Card deck progression and encounter sequencing
 * - Player lifecycle management (active play vs. early completion)
 * - Ranking calculation and reward distribution
 * - Event emission for game state synchronization
 * <p>
 * Player Management:
 * The board maintains two distinct player collections:
 * - Active players: Currently participating in encounters with position tracking
 * - Starting deck players: Those who have completed their journey early
 * <p>
 * Position System:
 * Players are positioned along a flight path with collision avoidance.
 * Movement calculations ensure players don't occupy the same position,
 * automatically adjusting destinations to maintain proper spacing.
 * <p>
 * Component Economy:
 * The board manages the shared component pool that players can acquire,
 * along with ID-based component lookup for efficient game operations.
 * <p>
 * Card Progression:
 * Adventure cards are managed in sequence, with automatic game-ending
 * detection when all cards are resolved or no active players remain.
 * <p>
 * Ranking System:
 * End-game scoring considers multiple factors:
 * - Arrival order bonuses for active players
 * - Goods conversion with type-based multipliers
 * - Starting deck penalties for early completion
 * - Ship beauty rewards for optimal component placement
 * - Component management penalties for discards and reserves
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public abstract class Board {

    /**
     * Map providing ID-based lookup for all components in the game
     */
    protected Map<Integer, Component> mapIdComponents;

    /**
     * List of components currently available in the common pool for player acquisition
     */
    protected List<Component> commonComponents;

    /**
     * List of active players with their current positions on the flight path
     */
    protected final List<SimpleEntry<PlayerData, Integer>> players;

    /**
     * List of players who have completed their journey early and are waiting in the starting area
     */
    protected final List<PlayerData> startingDeck;

    /**
     * The adventure card deck defining the sequence of encounters
     */
    protected final List<Card> cardPile;

    /**
     * Current position in the card pile indicating which card is active
     */
    protected int cardPilePos;

    /**
     * Constructs a new Board with empty collections for players, components, and cards.
     * Initializes the card pile position to -1 indicating no cards have been drawn yet.
     * Subclasses are responsible for populating these collections with appropriate content.
     */
    public Board() {
        this.startingDeck = new ArrayList<>();
        this.players = new ArrayList<>();

        this.cardPile = new ArrayList<>();
        this.cardPilePos = -1;
    }

    /**
     * Retrieves the list of active players with their current flight path positions.
     * <p>
     * Each entry contains a player and their integer position along the flight path.
     * The list is maintained in sorted order by position (highest to lowest) to
     * reflect the current standings in the space race.
     *
     * @return the list of player-position pairs for active participants
     */
    public List<SimpleEntry<PlayerData, Integer>> getPlayers() {
        return players;
    }

    /**
     * Retrieves the list of active players in position order without position data.
     * <p>
     * This method provides a simplified view of active players sorted by their
     * current standings, useful for operations that need player order but not
     * specific position values.
     *
     * @return the list of active players ordered by their flight path positions
     */
    public List<PlayerData> getPlayersByPos() {
        return players.stream().map(SimpleEntry::getKey).collect(Collectors.toList());
    }

    /**
     * Retrieves a player entity by username from all player collections.
     * <p>
     * This method searches both active players and starting deck players to
     * find the specified user, providing a unified lookup mechanism regardless
     * of the player's current status in the game.
     *
     * @param username the username of the player to find
     * @return the PlayerData instance for the specified user
     * @throws PlayerNotFoundException if no player with the given username exists
     */
    public PlayerData getPlayerEntityByUsername(String username) {
        return Stream.concat(players.stream().map(SimpleEntry::getKey), startingDeck.stream())
                .filter(p -> p.getUsername().equals(username))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new);
    }

    /**
     * Retrieves the list of players who have completed their journey early.
     * <p>
     * The starting deck contains players who have ended their flights prematurely
     * due to various game conditions (running out of crew, falling too far behind,
     * voluntary withdrawal, etc.). These players await final scoring but don't
     * participate in active encounters.
     *
     * @return the list of players in the starting deck area
     */
    public List<PlayerData> getStartingDeck() {
        return startingDeck;
    }

    /**
     * Retrieves the component registry map for ID-based component lookup.
     * <p>
     * This map provides efficient access to any component in the game by its
     * unique identifier, supporting various game operations like event processing,
     * component validation, and state synchronization.
     *
     * @return the map of component IDs to component instances
     */
    public Map<Integer, Component> getMapIdComponents() {
        return mapIdComponents;
    }

    /**
     * Retrieves the list of components currently available for player acquisition.
     * <p>
     * The common components pool represents the shared resources that players
     * can pick up and add to their ships during gameplay. This pool changes
     * as players acquire components and release them back to the common area.
     *
     * @return the list of components available in the common pool
     */
    public List<Component> getCommonComponents() {
        return commonComponents;
    }

    /**
     * Retrieves the adventure card deck defining the sequence of encounters.
     * <p>
     * The card pile contains all cards that will be encountered during the
     * adventure, arranged in the order they will be drawn and resolved.
     * The deck structure and composition depend on the specific board implementation.
     *
     * @return the list of cards in the adventure deck
     */
    public List<Card> getCardPile() {
        return cardPile;
    }

    /**
     * Retrieves the current position in the card pile indicating game progression.
     * <p>
     * This index tracks which card is currently active or will be drawn next.
     * A value of -1 indicates no cards have been drawn yet, while values
     * from 0 onwards indicate progression through the adventure sequence.
     *
     * @return the current card pile position index
     */
    public int getCardPilePos() {
        return cardPilePos;
    }

    /**
     * Advances to the next card in the adventure sequence and manages player transitions.
     * <p>
     * This method handles the critical game progression logic:
     * 1. Moves any players who ended early back to the starting deck
     * 2. Advances the card pile position to the next encounter
     * 3. Checks for game-ending conditions (no cards left or no active players)
     * 4. Sets up player states for the next card encounter
     * <p>
     * The method ensures smooth transitions between encounters while managing
     * the lifecycle of players who complete their journeys at different times.
     * Game ending is triggered automatically when appropriate conditions are met.
     *
     * @param model the model facade for game state management and player state updates
     */
    public void pickNewCard(ModelFacade model) {
        for (PlayerData player : getPlayersByPos())
            if (player.hasEndedInAdvance()) {
                model.setPlayerState(player.getUsername(), PlayerState.WAIT);
                moveToStartingDeck(player);
            }

        cardPilePos++;
        if (cardPilePos == cardPile.size() || players.isEmpty()) // All cards are resolved or there are no more players
            model.endGame();
        else { // Change card
            for (PlayerData p : getPlayersByPos())
                model.setPlayerState(p.getUsername(), PlayerState.WAIT);
            model.setPlayerState(getPlayersByPos().getFirst().getUsername(), PlayerState.DRAW_CARD);
        }
    }

    /**
     * Moves a player along the flight path with collision avoidance and position sorting.
     * <p>
     * The movement system implements sophisticated collision avoidance:
     * 1. Calculates the target position based on the movement amount
     * 2. Checks for position conflicts with other players
     * 3. Automatically adjusts the destination to avoid collisions
     * 4. Applies movement incrementally to handle multi-space moves
     * 5. Maintains sorted player order by position after movement
     * 6. Emits position update events for game state synchronization
     * <p>
     * The collision avoidance ensures that no two players occupy the same
     * position, automatically finding the nearest available space in the
     * movement direction. This prevents position conflicts while maintaining
     * the intended movement distance.
     *
     * @param playerData the player to move along the flight path
     * @param position   the number of positions to move (positive for forward, negative for backward)
     */
    public void movePlayer(PlayerData playerData, int position) {
        if (position == 0) return;
        SimpleEntry<PlayerData, Integer> entry = players.stream()
                .filter(e -> e.getKey().equals(playerData))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new);

        for (int d = 0; d < Math.abs(position); d++) {
            int currentPosition = entry.getValue();
            int nextPosition = (position > 0) ? currentPosition + 1 : currentPosition - 1;
            boolean moved = false; // check if we've moved

            while (!moved) {
                boolean positionOccupied = false; // check if the position in occupied

                for (SimpleEntry<PlayerData, Integer> otherEntry : players) {
                    if (!otherEntry.equals(entry) && otherEntry.getValue() == nextPosition) {
                        positionOccupied = true;
                        break;
                    }
                }

                if (!positionOccupied) {
                    entry.setValue(nextPosition);
                    moved = true;
                } else
                    nextPosition = (position > 0) ? nextPosition + 1 : nextPosition - 1;
            }
        }

        players.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        EventContext.emit(new PlayersPositionUpdatedEvent(
                startingDeck.stream().map(PlayerData::getUsername).toList(),
                players.stream().map(e -> new SimpleEntry<>(e.getKey().getUsername(), e.getValue())).toList())
        );
    }

    /**
     * Moves a player from active play to the starting deck area.
     * <p>
     * This transition occurs when players complete their journeys early due to
     * various conditions (crew loss, falling behind, voluntary ending, etc.).
     * The method maintains proper player collection management and emits events
     * to keep all clients synchronized with the player status changes.
     * <p>
     * Players in the starting deck await final scoring but no longer participate
     * in active encounters, representing their withdrawal from the ongoing adventure.
     *
     * @param player the player to move to the starting deck area
     */
    public void moveToStartingDeck(PlayerData player) {
        players.stream()
                .filter(el -> el.getKey().equals(player))
                .findFirst()
                .ifPresent(e -> {
                    players.remove(e);
                    startingDeck.add(player);
                });

        EventContext.emit(new PlayersPositionUpdatedEvent(
                startingDeck.stream().map(PlayerData::getUsername).toList(),
                players.stream().map(e -> new SimpleEntry<>(e.getKey().getUsername(), e.getValue())).toList())
        );
    }

    /**
     * Moves a player from the starting deck to active play on the flight path.
     * <p>
     * This transition typically occurs at the beginning of the game or when
     * players rejoin active play after certain game events. The method assigns
     * an appropriate starting position based on the board's positioning system
     * and maintains proper collection management.
     * <p>
     * The starting position is determined by the board order configuration,
     * ensuring fair and balanced initial placement for all participants.
     *
     * @param player the player to move from starting deck to active play
     * @throws PlayerNotFoundException if the player is not found in the starting deck
     */
    public void moveToBoard(PlayerData player) {
        startingDeck.stream()
                .filter(p -> p.equals(player))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new);
        int pos = getBoardOrderPos()[players.size()];
        players.add(new SimpleEntry<>(player, pos));

        players.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        startingDeck.remove(player);

        EventContext.emit(new PlayersPositionUpdatedEvent(
                startingDeck.stream().map(PlayerData::getUsername).toList(),
                players.stream().map(e -> new SimpleEntry<>(e.getKey().getUsername(), e.getValue())).toList())
        );
    }

    /**
     * Calculates final rankings and distributes credits based on multiple scoring factors.
     * <p>
     * The comprehensive ranking system evaluates players across several dimensions:
     * <p>
     * **Arrival Order Bonuses:**
     * - Active players receive credits based on their finishing order
     * - Higher positions earn more credits, rewarding successful completion
     * <p>
     * **Goods Conversion:**
     * - Goods are converted to credits using type-based multipliers:
     * - Red goods: 4 credits each (most valuable)
     * - Yellow goods: 3 credits each
     * - Green goods: 2 credits each
     * - Blue goods: 1 credit each (least valuable)
     * <p>
     * **Starting Deck Penalty:**
     * - Players who ended early have their goods value halved (rounded up)
     * - Represents the reduced trading opportunities from incomplete journeys
     * <p>
     * **Component Management:**
     * - Penalties applied for discarded components (representing waste)
     * - Penalties applied for unused reserved components (representing hoarding)
     * <p>
     * **Ship Beauty Reward:**
     * - Players with the most aesthetically pleasing ships (fewest exposed connectors)
     * - Rewards optimal component placement and ship design efficiency
     * <p>
     * This multi-faceted scoring system encourages balanced gameplay strategies
     * that consider journey completion, resource management, and ship optimization.
     */
    public void calcRanking() {
        List<PlayerData> players = Stream.concat(
                this.getPlayersByPos().stream(),
                this.getStartingDeck().stream()
        ).toList();

        int[] credits = getRankingCreditsValues();

        Map<ColorType, Integer> CREDIT_MULTIPLIERS = Map.of(
                ColorType.RED, 4,
                ColorType.YELLOW, 3,
                ColorType.GREEN, 2,
                ColorType.BLUE, 1
        );

        IntStream.range(0, players.size())
                .forEach(i -> {
                    PlayerData player = players.get(i);
                    // reward for the order of arrival (only not dropped out players)
                    if (this.getPlayersByPos().contains(player))
                        player.setCredits(player.getCredits() + credits[i]);

                    // handling sale of goods - calculating total goods value first
                    int totalGoodsCredits = 0;
                    for (ColorType good : player.getShip().getGoods().keySet()) {
                        totalGoodsCredits += CREDIT_MULTIPLIERS.get(good) * player.getShip().getGoods().get(good);
                    }

                    // apply starting deck penalty if applicable (divide total by 2 and round up)
                    if (this.getStartingDeck().contains(player)) {
                        totalGoodsCredits = (int) Math.ceil(totalGoodsCredits / 2.0);
                    }

                    // add total goods credits to player's credits
                    player.setCredits(player.getCredits() + totalGoodsCredits);
                    // component leaks
                    player.setCredits(player.getCredits() - player.getShip().getDiscards().size() - player.getShip().getReserves().size());
                });

        // reward for the most beautiful ship
        int[] exposedConnectors = players.stream()
                .mapToInt(p -> p.getShip().countExposedConnectors())
                .toArray();
        players.stream()
                .filter(p -> p.getShip().countExposedConnectors() == Arrays.stream(exposedConnectors).min().orElseThrow())
                .forEach(p -> p.setCredits(p.getCredits() + getRankingMostBeautifulShipReward()));

    }

    /**
     * Creates a data transfer object representation of the current board state.
     * <p>
     * The DTO includes all relevant information about the board's current condition:
     * - Component registry with their current states
     * - Common component pool availability
     * - Player positions and starting deck composition
     * - Card pile progression showing completed cards
     * <p>
     * This DTO is used for client-server communication and UI display purposes,
     * providing a complete snapshot of the game state for synchronization.
     *
     * @return a BoardDTO containing the complete current board state
     */
    public BoardDTO toDto() {
        BoardDTO boardDTO = new BoardDTO();

        boardDTO.mapIdComponents = mapIdComponents.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toDTO()));
        boardDTO.commonComponents = commonComponents.stream()
                .map(Component::getId).toList();
        boardDTO.players = players.stream()
                .map(GameStateDTOFactory::createPlayerPositionDTO).toList();
        boardDTO.startingDeck = startingDeck.stream()
                .map(GameStateDTOFactory::createPlayerDTO).toList();
        boardDTO.cardPile = CardFactory.serializeCardList(cardPile.stream()
                .limit(Math.max(cardPilePos, 0)).toList());

        return boardDTO;
    }

    /**
     * Retrieves the mapping of card pile names to their watch indices.
     * <p>
     * This abstract method allows different board implementations to define
     * their specific card pile organization and monitoring systems.
     *
     * @return a map of card pile names to their corresponding watch indices
     */
    public abstract Map<String, Integer> getCardPilesWatchMap();

    /**
     * Shuffles the card deck according to the board's specific shuffling strategy.
     * <p>
     * Different board implementations may use different shuffling approaches
     * based on their game mode requirements and difficulty settings.
     */
    public abstract void shuffleCards();

    /**
     * Initializes the match and sets up initial game state.
     * <p>
     * This method handles board-specific match initialization procedures
     * that vary depending on the game mode and board configuration.
     *
     * @param model the model facade for game state management
     */
    public abstract void startMatch(ModelFacade model);

    /**
     * Handles hourglass movement for time management during player actions.
     * <p>
     * The hourglass system provides time pressure and pacing control,
     * with implementation details varying by board type and game mode.
     *
     * @param username the username of the player requesting hourglass movement
     * @param model    the model facade for game state management
     * @param callback the callback function to handle time expiration events
     */
    public abstract void moveHourglass(String username, ModelFacade model, Consumer<List<Event>> callback);

    /**
     * Retrieves the board-specific starting position configuration.
     * <p>
     * Different board implementations define different starting position
     * arrangements to create varied gameplay experiences and balance.
     *
     * @return an array of starting positions for player placement
     */
    public abstract int[] getBoardOrderPos();

    /**
     * Retrieves the credit values awarded for different finishing positions.
     * <p>
     * Board implementations define their own reward structures for
     * arrival order bonuses in the final ranking calculation.
     *
     * @return an array of credit values for finishing positions
     */
    protected abstract int[] getRankingCreditsValues();

    /**
     * Retrieves the credit reward for having the most beautiful ship.
     * <p>
     * The ship beauty reward recognizes optimal component placement
     * and efficient ship design, with values varying by board implementation.
     *
     * @return the number of credits awarded for the most beautiful ship
     */
    protected abstract int getRankingMostBeautifulShipReward();

}
