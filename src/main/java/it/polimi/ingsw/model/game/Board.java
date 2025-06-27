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
     * @return the list of player-position pairs for active participants
     */
    public List<SimpleEntry<PlayerData, Integer>> getPlayers() {
        return players;
    }

    /**
     * Retrieves the list of active players in position order without position data.
     * <p>
     *
     * @return the list of active players ordered by their flight path positions
     */
    public List<PlayerData> getPlayersByPos() {
        return players.stream().map(SimpleEntry::getKey).collect(Collectors.toList());
    }

    /**
     * Retrieves a player entity by username from all player collections.
     * <p>
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
     *
     * @return the map of component IDs to component instances
     */
    public Map<Integer, Component> getMapIdComponents() {
        return mapIdComponents;
    }

    /**
     * Retrieves the list of components currently available for player acquisition.
     * <p>
     *
     * @return the list of components available in the common pool
     */
    public List<Component> getCommonComponents() {
        return commonComponents;
    }

    /**
     * Retrieves the adventure card deck defining the sequence of encounters.
     * <p>
     *
     * @return the list of cards in the adventure deck
     */
    public List<Card> getCardPile() {
        return cardPile;
    }

    /**
     * Retrieves the current position in the card pile indicating game progression.
     * <p>
     *
     * @return the current card pile position index
     */
    public int getCardPilePos() {
        return cardPilePos;
    }

    /**
     * Advances to the next card in the adventure sequence and manages player transitions.
     * <p>
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
     *
     * @return a BoardDTO containing the complete current board state
     */
    public BoardDTO toDto(ModelFacade model) {
        BoardDTO boardDTO = new BoardDTO();

        boardDTO.mapIdComponents = mapIdComponents.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toDTO()));
        boardDTO.commonComponents = commonComponents.stream()
                .map(Component::getId).toList();
        boardDTO.players = players.stream()
                .map(GameStateDTOFactory::createPlayerPositionDTO).toList();
        boardDTO.startingDeck = startingDeck.stream()
                .map(GameStateDTOFactory::createPlayerDTO).toList();

        if (model.getPlayersState().containsValue(PlayerState.DRAW_CARD))
            boardDTO.cardPile = CardFactory.serializeCardList(cardPile.stream()
                .limit(Math.max(cardPilePos, 0)).toList());
        else
            boardDTO.cardPile = CardFactory.serializeCardList(cardPile.stream()
                    .limit(cardPilePos+1).toList());

        return boardDTO;
    }

    /**
     * Retrieves the mapping of card pile names to their watch indices.
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
     *
     * @param model the model facade for game state management
     */
    public abstract void startMatch(ModelFacade model);

    /**
     * Handles hourglass movement for time management during player actions.
     * <p>
     *
     * @param username the username of the player requesting hourglass movement
     * @param model    the model facade for game state management
     * @param callback the callback function to handle time expiration events
     */
    public abstract void moveHourglass(String username, ModelFacade model, Consumer<List<Event>> callback);

    /**
     * Retrieves the board-specific starting position configuration.
     * <p>
     *
     * @return an array of starting positions for player placement
     */
    public abstract int[] getBoardOrderPos();

    /**
     * Retrieves the credit values awarded for different finishing positions.
     * <p>
     * @return an array of credit values for finishing positions
     */
    protected abstract int[] getRankingCreditsValues();

    /**
     * Retrieves the credit reward for having the most beautiful ship.
     * <p>
     *
     * @return the number of credits awarded for the most beautiful ship
     */
    protected abstract int getRankingMostBeautifulShipReward();

}
