package it.polimi.ingsw.common.model.enums;

/**
 * Enumeration representing the various states a player can be in during a game.
 * These states control the game flow and determine what actions a player can perform.
 */
public enum PlayerState {

    // ==================== BUILDING PHASE STATES ====================

    /**
     * Player is actively building their spaceship by placing components.
     * During this state, the player can select and place ship components from the common pool.
     */
    BUILD,

    /**
     * Player is examining the adventure card pile to see upcoming challenges.
     * This allows strategic planning for ship construction.
     */
    LOOK_CARD_PILE,

    /**
     * Player is performing a final check of their completed ship.
     * This state allows players to verify connections and ship integrity.
     */
    CHECK,

    // ==================== FLIGHT PHASE WAITING STATES ====================

    /**
     * Player is choosing if they want to put aliens in their ship.
     */
    WAIT_ALIEN,

    /**
     * Player is waiting to select which ship parts to keep (ship broken in more parts).
     */
    WAIT_SHIP_PART,

    /**
     * Player has to draw an adventure card.
     */
    DRAW_CARD,

    /**
     * Player has completed their game phase.
     */
    END,

    // ==================== GENERAL WAITING STATES ====================

    /**
     * Generic waiting state when the player cannot take actions.
     */
    WAIT,

    /**
     * Player could activate double cannons via batteries.
     */
    WAIT_CANNONS,

    /**
     * Player could activate double engine via batteries.
     */
    WAIT_ENGINES,

    /**
     * Player has to load goods onto their ship.
     */
    WAIT_GOODS,

    /**
     * Player must remove goods from their ship due to penalty.
     */
    WAIT_REMOVE_GOODS,

    /**
     * Player has to roll dice.
     */
    WAIT_ROLL_DICES,

    /**
     * Player must remove crew members from their ship.
     */
    WAIT_REMOVE_CREW,

    /**
     * Player could activate a shield via battery.
     */
    WAIT_SHIELD,

    /**
     * Player must make a boolean (yes/no) decision.
     * Used for various game choices that require a simple true/false response.
     */
    WAIT_BOOLEAN,

    /**
     * Player must select an index.
     * This is used when the player needs to choose from a numbered list.
     */
    WAIT_INDEX,

    /**
     * Player has completed all required actions for the current phase.
     * The player is ready to proceed to the next game phase or turn.
     */
    DONE
}
