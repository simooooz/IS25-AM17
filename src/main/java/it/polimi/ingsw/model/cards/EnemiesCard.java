package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract base class for enemy encounter cards in the game.
 * This class provides the core mechanics for combat encounters where players
 * face hostile enemies and must use their cannon firepower to defeat them.
 * <p>
 * The card evaluates each player's combat capabilities by calculating their
 * free cannon power (single cannons + alien bonus) and potential double cannon
 * power (limited by available batteries). Players automatically win, lose, or
 * are given combat choices based on their firepower compared to enemy strength.
 * <p>
 * Once any player defeats the enemies, all subsequent players automatically
 * succeed. Players who cannot defeat the enemies face penalties defined by
 * concrete subclasses through the abstract defeatedMalus method.
 */
public abstract class EnemiesCard extends Card {

    /**
     * The number of flight days that a player loses if he defeats enemies
     * and decides to take the reward
     */
    @JsonProperty
    protected final int days;

    /**
     * The firepower strength of the enemy forces that must be overcome
     */
    @JsonProperty
    protected final int enemyFirePower;

    /**
     * Flag indicating whether the enemies have been defeated by any player
     */
    @JsonProperty
    protected boolean enemiesDefeated;

    /**
     * List of players participating in the enemy encounter
     */
    protected List<PlayerData> players;

    /**
     * Current index of the player being evaluated in the encounter
     */
    protected int playerIndex;

    /**
     * Constructs a new EnemiesCard with the specified parameters.
     *
     * @param id             the unique identifier of the card
     * @param level          the level of the card
     * @param isLearner      whether this card is for learner mode
     * @param days           the number of flight days this encounter represents
     * @param enemyFirePower the firepower strength of the enemy forces
     */
    public EnemiesCard(int id, int level, boolean isLearner, int days, int enemyFirePower) {
        super(id, level, isLearner);
        this.days = days;
        this.enemyFirePower = enemyFirePower;
    }

    /**
     * Starts the enemy encounter card execution by initializing player states
     * and beginning automatic combat evaluation.
     * <p>
     * Resets the encounter state, initializes all players to waiting state,
     * and begins the automatic evaluation process for each player's combat capabilities.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the encounter is complete, false if players need to take combat actions
     */
    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.enemiesDefeated = false;
        this.playerIndex = 0;
        this.players = new ArrayList<>(board.getPlayersByPos());

        for (PlayerData player : players)
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        return autoCheckPlayers(model, board);
    }

    /**
     * Automatically evaluates each player's combat capabilities against the enemy forces
     * and determines appropriate actions or outcomes.
     * <p>
     * For each player, the method:
     * 1. Calculates free cannon power (single cannons + potential alien bonus)
     * 2. Calculates maximum double cannon power (limited by available batteries)
     * 3. Determines combat outcome based on firepower comparison:
     * - If enemies already defeated: player automatically succeeds
     * - If free cannons exceed enemy power: player can choose to claim victory
     * - If total potential power equals enemy power exactly: player succeeds without choice
     * - If total potential power exceeds enemy power: player must choose cannon usage
     * - If insufficient firepower: player faces defeat penalties
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if all players have been evaluated and the encounter is complete, false if waiting for player actions
     */
    protected boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            double freeCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(cannon -> !cannon.getIsDouble())
                    .mapToDouble(CannonComponent::calcPower).sum();
            if (freeCannonsPower > 0 && player.getShip().getCannonAlien())
                freeCannonsPower += 2;

            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(CannonComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToDouble(Double::doubleValue)
                    .sum();

            if (enemiesDefeated)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower > enemyFirePower) { // User wins automatically
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                enemiesDefeated = true;
                return false;
            } else if (freeCannonsPower == enemyFirePower && doubleCannonsPower == 0)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= enemyFirePower) { // User could win
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_CANNONS);
                return false;
            } else { // User loses automatically
                boolean shouldReturn = defeatedMalus(model, player);
                if (shouldReturn)
                    return false;
            }
        }

        return calcHasDone(model, board);
    }

    /**
     * Calculates whether all players have completed their actions in the enemy encounter.
     * <p>
     * Checks if all participating players have reached the DONE state, indicating
     * that the enemy encounter can be concluded.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if all players are in DONE state, false if any player is still taking actions
     */
    public boolean calcHasDone(ModelFacade model, Board board) {
        boolean hasDone = true;
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        return hasDone;
    }

    /**
     * Abstract method that must be implemented by subclasses to define specific
     * penalties applied to players who are defeated by the enemy forces.
     * <p>
     * This method is called when a player's firepower is insufficient to defeat
     * the enemies, and they must face the consequences of combat failure.
     *
     * @param model  the model facade providing access to game state
     * @param player the player data for the defeated player
     * @return true if the penalty application requires waiting for player actions, false if immediate
     */
    public abstract boolean defeatedMalus(ModelFacade model, PlayerData player);

    /**
     * Processes cannon-based command effects for players engaging in combat.
     * <p>
     * Handles the resolution of cannon attacks:
     * - If cannon power exceeds enemy firepower and enemies not yet defeated: offers victory choice
     * - If cannon power meets or exceeds enemy firepower: marks player as successful
     * - If cannon power is insufficient: applies defeat penalties
     *
     * @param commandType the type of command being executed (must be WAIT_CANNONS)
     * @param value       the double value representing total cannon firepower used
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the encounter progresses automatically, false if waiting for player actions
     * @throws RuntimeException if the command type is not WAIT_CANNONS
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (value > enemyFirePower && !enemiesDefeated) { // Ask if user wants to redeem rewards
                model.setPlayerState(username, PlayerState.WAIT_BOOLEAN);
                enemiesDefeated = true;
            } else if (value >= enemyFirePower) { // Tie or slavers already defeated
                model.setPlayerState(username, PlayerState.DONE);
                playerIndex++;
                return autoCheckPlayers(model, board);
            } else // Player is defeated
                defeatedMalus(model, board.getPlayerEntityByUsername(username));

            return false;
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Handles the effects when a player leaves the game during enemy encounter execution.
     * <p>
     * Manages player list updates and continues combat evaluation when appropriate:
     * - Updates player indices and list to maintain proper iteration
     * - Continues combat evaluation if the current player left
     * - Maintains combat flow for remaining players
     *
     * @param state    the current state of the leaving player
     * @param model    the model facade providing access to game state
     * @param board    the game board containing all players and entities
     * @param username the username of the player leaving the game
     * @return true if enemy encounter execution should continue automatically, false otherwise
     */
    @SuppressWarnings("Duplicates")
    @Override
    public boolean doLeftGameEffects(PlayerState state, ModelFacade model, Board board, String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        int indexOfLeftPlayer = players.indexOf(player);

        if (playerIndex > indexOfLeftPlayer) {
            players.remove(indexOfLeftPlayer);
            playerIndex--;
        } else if (playerIndex == indexOfLeftPlayer) {
            players.remove(indexOfLeftPlayer);
            return autoCheckPlayers(model, board);
        } else
            players.remove(indexOfLeftPlayer);

        return playerIndex == players.size() && calcHasDone(model, board);
    }

}