/**
 * Abstract base class representing a card in the game.
 * Cards define various events and challenges that players encounter during their space journey.
 * Each card has specific effects and may require player interactions to resolve.
 */
package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

/**
 * The card system uses polymorphism to handle different types of encounters uniformly
 * while allowing each card type to implement its specific behavior. Cards support
 * JSON serialization with type information to preserve the specific card implementation
 * when saving/loading game state.
 * <p>
 * Cards follow a common lifecycle:
 * 1. Initialization and player state setup (startCard)
 * 2. Player interaction and command processing (doCommandEffects)
 * 3. Validation of player actions (doSpecificCheck)
 * 4. Game state cleanup and flight termination checks (endCard)
 * <p>
 * The validation system ensures players can only perform actions allowed by their
 * current game state and the specific card's requirements, preventing exploitation
 * and maintaining game balance.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AbandonedShipCard.class, name = "ABANDONED_SHIP"),
        @JsonSubTypes.Type(value = AbandonedStationCard.class, name = "ABANDONED_STATION"),
        @JsonSubTypes.Type(value = CombatZoneCard.class, name = "COMBAT_ZONE"),
        @JsonSubTypes.Type(value = EpidemicCard.class, name = "EPIDEMIC"),
        @JsonSubTypes.Type(value = MeteorSwarmCard.class, name = "METEOR_SWARM"),
        @JsonSubTypes.Type(value = OpenSpaceCard.class, name = "OPEN_SPACE"),
        @JsonSubTypes.Type(value = PiratesCard.class, name = "PIRATES"),
        @JsonSubTypes.Type(value = PlanetCard.class, name = "PLANET"),
        @JsonSubTypes.Type(value = SlaversCard.class, name = "SLAVERS"),
        @JsonSubTypes.Type(value = SmugglersCard.class, name = "SMUGGLERS"),
        @JsonSubTypes.Type(value = StardustCard.class, name = "STARDUST")
})
abstract public class Card {

    /**
     * The unique identifier of this card
     */
    @JsonProperty
    private final int id;

    /**
     * The level of this card
     */
    @JsonProperty
    private final int level;

    /**
     * Whether this card is designed for learner mode
     */
    @JsonProperty
    private final boolean isLearner;

    /**
     * Constructs a new Card with the specified basic properties.
     * These properties are shared by all card types.
     *
     * @param id        the unique identifier of this card
     * @param level     the level of this card indicating difficulty or progression
     * @param isLearner whether this card is designed for learner mode
     */
    public Card(int id, int level, boolean isLearner) {
        this.id = id;
        this.level = level;
        this.isLearner = isLearner;
    }

    /**
     * Retrieves the unique identifier of this card.
     *
     * @return the unique identifier of this card
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the level of this card.
     *
     * @return the level of this card
     */
    public int getLevel() {
        return level;
    }

    /**
     * Retrieves whether this card is designed for learner mode.
     *
     * @return true if this card is for learner mode, false otherwise
     */
    public boolean getIsLearner() {
        return isLearner;
    }

    /**
     * Initiates the card's execution and sets up initial player states.
     * <p>
     * This method is called when the card becomes active and must establish
     * the appropriate player states for the card's specific encounter type.
     * Each card implementation defines how players should be prepared for
     * the upcoming challenges or opportunities.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the card execution is complete, false if player interactions are required
     */
    public abstract boolean startCard(ModelFacade model, Board board);

    /**
     * Performs end-of-card cleanup and checks for flight termination conditions
     * (only for standard mode).
     * <p>
     * This method handles standard game state maintenance that occurs at the
     * conclusion of most card encounters:
     * - Checks for players with insufficient humans
     * - Checks for players who have fallen too far behind the leader
     * - Terminates flights for players who fail these survival conditions
     *
     * @param board the game board containing all players and position information
     */
    public void endCard(Board board) {
        List<PlayerData> players = board.getPlayersByPos();

        for (PlayerData player : players)
            if (player.getShip().getCrew() - (player.getShip().getCannonAlien() ? 1 : 0) - (player.getShip().getEngineAlien() ? 1 : 0) == 0)
                player.endFlight();

        if (!board.getPlayers().isEmpty()) {
            int leaderPos = board.getPlayers().getFirst().getValue();
            for (SimpleEntry<PlayerData, Integer> entry : board.getPlayers())
                if (leaderPos >= entry.getValue() + 24)
                    entry.getKey().endFlight();
        }
    }

    /**
     * Validates goods acquisition commands to ensure players only acquire allowed rewards.
     * <p>
     * This validation method ensures that:
     * - Players only acquire goods that are explicitly offered as rewards
     * - Players don't exceed the quantity limits for available goods
     * - No battery components are used in goods acquisition (which should be free)
     *
     * @param commandType the command type being validated (must be WAIT_GOODS)
     * @param rewards     the map of available rewards by color type and quantity
     * @param deltaGood   the map of goods changes requested by the player
     * @param batteries   the list of battery components (should be empty for goods acquisition)
     * @param username    the username of the player executing the command
     * @param board       the game board containing player information
     * @throws GoodNotValidException             if the player requests unavailable or excessive goods
     * @throws BatteryComponentNotValidException if batteries are provided for goods acquisition
     */
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> rewards, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        if (commandType != PlayerState.WAIT_GOODS) return;

        for (ColorType good : ColorType.values())
            if ((deltaGood.get(good) > 0 && !rewards.containsKey(good)) || (rewards.containsKey(good) && deltaGood.get(good) > rewards.get(good)))
                throw new GoodNotValidException("Reward check not passed, insert only allowed goods");

        if (!batteries.isEmpty())
            throw new BatteryComponentNotValidException("Battery component list should be empty");
    }

    /**
     * Validates goods removal commands to ensure players surrender the correct quantity of goods.
     * <p>
     * This complex validation method ensures that:
     * - Players remove exactly the required number of goods
     * - Players prioritize more valuable goods when forced to surrender items
     * - Battery components are used appropriately when goods are insufficient
     * - The total removal count matches the penalty requirement
     *
     * @param commandType the command type being validated (must be WAIT_REMOVE_GOODS)
     * @param number      the number of goods that must be removed
     * @param deltaGood   the map of goods changes requested by the player
     * @param batteries   the list of battery components used to supplement goods removal
     * @param username    the username of the player executing the command
     * @param board       the game board containing player and ship information
     * @throws GoodNotValidException             if goods removal doesn't follow the prioritization rules
     * @throws IllegalArgumentException          if the total removal count is incorrect
     * @throws BatteryComponentNotValidException if battery usage is inappropriate
     */
    public void doSpecificCheck(PlayerState commandType, int number, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        if (commandType != PlayerState.WAIT_REMOVE_GOODS) return;
        Ship ship = board.getPlayerEntityByUsername(username).getShip();

        for (ColorType goodType : ColorType.values()) {
            number += deltaGood.get(goodType);
            if (number > 0 && (ship.getGoods().get(goodType) + deltaGood.get(goodType) != 0))
                throw new GoodNotValidException("There are more valuable goods in the ship");
        }

        if (number < 0) throw new IllegalArgumentException("Too many goods provided");
        if (number == 0 && batteries.isEmpty()) return;
        else if (number == 0) throw new IllegalArgumentException("Battery components list should be empty");

        if (batteries.size() > number)
            throw new BatteryComponentNotValidException("Too many battery components provided");
        else if (batteries.size() < number && (ship.getBatteries() >= number || ship.getBatteries() != batteries.size()))
            throw new BatteryComponentNotValidException("Too few battery components provided");
    }

    /**
     * Validates crew removal commands to ensure players surrender the correct number of crew members.
     * <p>
     * This validation ensures that players provide the exact number of cabin
     * components corresponding to the crew members they must surrender. The
     * method accounts for ships with limited crew where the player must
     * surrender all available crew members.
     *
     * @param commandType the command type being validated (must be WAIT_REMOVE_CREW)
     * @param cabins      the list of cabin components from which crew will be removed
     * @param toRemove    the number of crew members that must be removed
     * @param username    the username of the player executing the command
     * @param board       the game board containing player and ship information
     * @throws IllegalArgumentException if the number of cabins doesn't match removal requirements
     */
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        int crew = cabins.size();
        int shipCrew = board.getPlayerEntityByUsername(username).getShip().getCrew();
        if (commandType == PlayerState.WAIT_REMOVE_CREW && crew != toRemove && (shipCrew >= toRemove || shipCrew != crew))
            throw new IllegalArgumentException("Too few cabin components provided");
    }

    /**
     * Validates cannon-related commands for specific card implementations.
     * <p>
     * This base implementation provides no validation, allowing card subclasses
     * to override this method when they need to validate cannon selection or usage.
     * Cards like meteor swarms use this to ensure cannons are properly positioned
     * and oriented for defensive actions.
     *
     * @param commandType the command type being validated
     * @param cannons     the list of cannon components involved in the command
     * @param username    the username of the player executing the command
     * @param board       the game board containing player and ship information
     */
    public void doSpecificCheck(PlayerState commandType, List<CannonComponent> cannons, String username, Board board) {
    }

    /**
     * Processes command effects with integer parameters.
     * <p>
     * This method handles player actions that involve integer values, such as
     * dice rolls or planet index. The base implementation
     * throws an exception, requiring card types that support integer commands
     * to override this method with their specific logic.
     *
     * @param commandType the type of command being executed
     * @param value       the integer value associated with the command
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the card execution progresses or completes, false if more interaction is needed
     * @throws RuntimeException if the card type doesn't support integer commands
     */
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    /**
     * Processes command effects with double parameters.
     * <p>
     * This method handles player actions that involve double values, such as
     * cannon firepower calculations. The base implementation throws an exception,
     * requiring card types that support double commands to override this method.
     *
     * @param commandType the type of command being executed
     * @param value       the double value associated with the command
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the card execution progresses or completes, false if more interaction is needed
     * @throws RuntimeException if the card type doesn't support double commands
     */
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    /**
     * Processes command effects with boolean parameters.
     * <p>
     * This method handles player actions that involve boolean choices, such as
     * accepting rewards or activating shields. The base
     * implementation throws an exception, requiring card types that support
     * boolean commands to override this method.
     *
     * @param commandType the type of command being executed
     * @param value       the boolean value indicating the player's choice
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the card execution progresses or completes, false if more interaction is needed
     * @throws RuntimeException if the card type doesn't support boolean commands
     */
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    /**
     * Processes command effects without parameters.
     * <p>
     * This method handles player actions that don't require additional parameters,
     * such as completing ship part replacements or finishing certain types of
     * interactions. The base implementation throws an exception, requiring card
     * types that support parameterless commands to override this method.
     *
     * @param commandType the type of command being executed
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the card execution progresses or completes, false if more interaction is needed
     * @throws RuntimeException if the card type doesn't support parameterless commands
     */
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    /**
     * Handles the effects when a player leaves the game during card execution.
     * <p>
     * This method allows cards to maintain proper game flow when players
     * disconnect or leave during card resolution. The base implementation
     * returns false, indicating no special handling is required, but card
     * subclasses can override this to manage player list updates and
     * continue card execution appropriately.
     *
     * @param state    the current state of the leaving player
     * @param model    the model facade providing access to game state
     * @param board    the game board containing remaining players and entities
     * @param username the username of the player leaving the game
     * @return true if the card execution should continue automatically, false otherwise
     */
    public boolean doLeftGameEffects(PlayerState state, ModelFacade model, Board board, String username) {
        return false;
    }
}