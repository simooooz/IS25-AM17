package it.polimi.ingsw.model.player;

import it.polimi.ingsw.common.model.events.game.CreditsUpdatedEvent;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.game.FlightEndedEvent;
import it.polimi.ingsw.model.game.Board;

/**
 * Represents the data and state of a player in the game.
 * This class encapsulates all player-specific information including
 * their ship, credits, game status, and flight progress.
 *
 * <p>The PlayerData class manages:
 * <ul>
 * <li>Player identification through username</li>
 * <li>Ship ownership and management</li>
 * <li>Credit system and scoring</li>
 * <li>Flight completion status</li>
 * <li>Game connection status</li>
 * <li>Event emission for state changes</li>
 * </ul>
 *
 * <p>Key features:
 * <ul>
 * <li>Automatic event emission when credits are updated</li>
 * <li>Flight completion tracking for early endings</li>
 * <li>Connection status tracking for player disconnections</li>
 * <li>Integration with the board system for game state transitions</li>
 * </ul>
 *
 * <p>Player equality is determined solely by username, allowing for
 * easy identification and comparison of players throughout the game.
 *
 * @author Generated Javadoc
 * @version 1.0
 * @since 1.0
 */
public class PlayerData {

    /**
     * The unique username identifying this player.
     * This field is immutable and serves as the primary identifier.
     */
    private final String username;

    /**
     * The ship owned and controlled by this player.
     * The ship contains the player's constructed components and game progress.
     */
    private Ship ship;

    /**
     * The current credit count for this player.
     * Credits are used for scoring and purchasing game elements.
     */
    private int credits;

    /**
     * Flag indicating whether the player has ended their flight early.
     * When true, the player has chosen to finish before the normal end conditions.
     */
    private boolean endedInAdvance;

    /**
     * Flag indicating whether the player has left the game.
     * This tracks disconnection status for reconnection handling.
     */
    private boolean leftGame;

    /**
     * Constructs a new PlayerData instance with the specified username.
     * Initializes the player with zero credits and sets them as active
     * (not ended in advance and not left the game).
     *
     * <p>The ship must be set separately after construction using
     * the {@link #setShip(Ship)} method.
     *
     * @param username the unique username for this player
     */
    public PlayerData(String username) {
        this.username = username;
        this.credits = 0;
        this.endedInAdvance = false;
    }

    /**
     * Compares this PlayerData with another object for equality.
     * Two PlayerData objects are considered equal if they have the same username,
     * regardless of other field values.
     *
     * <p>This implementation allows for easy player identification and
     * collection operations based solely on username.
     *
     * @param o the object to compare with this PlayerData
     * @return true if the object is a PlayerData with the same username, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData that = (PlayerData) o;
        return username.equals(that.username);
    }

    /**
     * Returns the username of this player.
     *
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the ship owned by this player.
     *
     * @return the player's Ship instance, or null if no ship has been assigned
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Sets the ship for this player.
     * This method is typically called during game initialization
     * when assigning specific ship types (standard, learner, or advanced mode).
     *
     * @param ship the Ship instance to assign to this player
     */
    public void setShip(Ship ship) {
        this.ship = ship;
    }

    /**
     * Returns the current credit count for this player.
     *
     * @return the number of credits the player currently has
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Sets the credit count for this player and emits an update event.
     * This method automatically notifies the game system of the credit change
     * by emitting a CreditsUpdatedEvent.
     *
     * <p>The event emission allows other parts of the system to react to
     * credit changes, such as updating UI displays or triggering game logic.
     *
     * @param credits the new credit amount to set
     */
    public void setCredits(int credits) {
        this.credits = credits;
        EventContext.emit(new CreditsUpdatedEvent(username, credits));
    }

    /**
     * Marks the player as having ended their flight early and emits an event.
     * This method is called when a player chooses to finish their game
     * before the normal end conditions are met.
     *
     * <p>Ending early may have strategic implications, such as:
     * <ul>
     * <li>Securing position on the leaderboard</li>
     * <li>Avoiding potential penalties</li>
     * <li>Influencing other players' remaining time</li>
     * </ul>
     *
     * <p>The FlightEndedEvent is emitted to notify the game system
     * and other players of this action.
     */
    public void endFlight() {
        endedInAdvance = true;
        EventContext.emit(new FlightEndedEvent(username));
    }

    /**
     * Checks if this player has ended their flight early.
     *
     * @return true if the player ended their flight before normal completion, false otherwise
     */
    public boolean hasEndedInAdvance() {
        return endedInAdvance;
    }

    /**
     * Checks if this player has left the game.
     * This status is used for tracking disconnections and managing reconnections.
     *
     * @return true if the player has left the game, false if they are still connected
     */
    public boolean isLeftGame() {
        return leftGame;
    }

    /**
     * Sets the left game status for this player.
     * This method is used to track when players disconnect from or reconnect to the game.
     *
     * <p>Setting this to true indicates the player has disconnected,
     * while setting it to false indicates they have reconnected or are active.
     *
     * @param leftGame true if the player has left the game, false otherwise
     */
    public void setLeftGame(boolean leftGame) {
        this.leftGame = leftGame;
    }

    /**
     * Marks the player as ready and transitions them to the board.
     * This method handles the final preparation steps when a player
     * is ready to begin active gameplay.
     *
     * <p>The method performs the following actions:
     * <ul>
     * <li>Releases any component currently in the player's hand</li>
     * <li>Moves the player to the game board</li>
     * </ul>
     *
     * <p>This transition typically occurs when:
     * <ul>
     * <li>Initial game setup is complete</li>
     * <li>The player has finished their preparation phase</li>
     * <li>All players are ready to begin the main game</li>
     * </ul>
     *
     * @param board the game board to move the player to
     */
    public void setReady(Board board) {
        ship.getHandComponent().ifPresent(c -> c.releaseComponent(board, this));
        board.moveToBoard(this);
    }
}
