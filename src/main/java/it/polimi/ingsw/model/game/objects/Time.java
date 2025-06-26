package it.polimi.ingsw.model.game.objects;

import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Game time management system implementing an hourglass-based timer mechanism.
 * This class manages the passage of time during gameplay, providing structured
 * time limits for player actions and automatic game progression when time expires.
 * <p>
 * The time system operates on an hourglass metaphor with the following mechanics:
 * - Each hourglass position represents a time allocation period
 * - Players have 10 seconds per time period to complete their actions
 * - The hourglass can be rotated multiple times (initially 3 positions available)
 * - When all hourglass positions are exhausted, players are automatically marked as ready
 * <p>
 * The system serves multiple important functions:
 * - Prevents games from stalling due to inactive or indecisive players
 * - Creates urgency and maintains game pacing
 * - Provides fair time allocation across all players
 * - Automatically progresses the game when time limits are reached
 * - Manages event processing and state synchronization during time transitions
 * <p>
 * Time management is particularly crucial in multiplayer scenarios where one
 * player's delay can impact the entire game experience. The automatic progression
 * ensures that games maintain momentum while still providing reasonable time
 * for strategic decision-making.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class Time {

    /**
     * The current time remaining in the active timer period (in seconds)
     */
    private int timeLeft;

    /**
     * The current position of the hourglass (remaining time allocations available)
     */
    private int hourglassPos;

    /**
     * The timer instance used for periodic time updates and countdown management
     */
    private final Timer timer;

    /**
     * Constructs a new Time management system with default hourglass configuration.
     * <p>
     * Initializes the time system with:
     * - A fresh Timer instance for countdown management
     * - 3 hourglass positions providing multiple time allocation opportunities
     * - No active time countdown (timeLeft starts at 0)
     * <p>
     * The system is ready to begin timing when startTimer() is called.
     */
    public Time() {
        this.timer = new Timer();
        this.hourglassPos = 3;
    }

    /**
     * Retrieves the current hourglass position indicating remaining time allocations.
     * <p>
     * The hourglass position represents how many more times players can request
     * additional time for their actions. Each position allows for a 10-second
     * time period, and the position decreases each time the hourglass is rotated.
     *
     * @return the number of hourglass positions remaining (0-3)
     */
    public int getHourglassPos() {
        return hourglassPos;
    }

    /**
     * Retrieves the current time remaining in the active countdown period.
     * <p>
     * This value represents the seconds remaining in the current time allocation.
     * When it reaches 1, the timer period ends and either a new period begins
     * (if hourglass positions remain) or automatic game progression occurs.
     *
     * @return the number of seconds remaining in the current time period
     */
    public int getTimeLeft() {
        return timeLeft;
    }

    /**
     * Rotates the hourglass to allocate a new time period if positions are available.
     * <p>
     * The hourglass rotation process:
     * - Checks if any hourglass positions remain
     * - If available, allocates a new 10-second time period
     * - Decrements the hourglass position counter
     * - If no positions remain, no time is allocated
     * <p>
     * This method is called automatically when starting a new timer period
     * and represents the consumption of one hourglass time allocation.
     */
    private void rotateHourglass() {
        if (hourglassPos > 0) {
            timeLeft = 61;
            hourglassPos--;
        }
    }

    /**
     * Starts a new timer countdown period with automatic progression handling.
     * <p>
     * The timer startup process:
     * 1. Validates that no timer is currently active (timeLeft must be 0)
     * 2. Rotates the hourglass to allocate time for the new period
     * 3. Creates a TimerTask that decrements time every second
     * 4. Handles automatic game progression when all time is exhausted
     * <p>
     * When the timer reaches completion and no hourglass positions remain:
     * - Clears the event context to prepare for batch processing
     * - Identifies all active players (those who haven't ended early)
     * - Automatically marks all active players as ready
     * - Collects and clears all pending events
     * - Invokes the callback with the collected events for processing
     * <p>
     * This automatic progression ensures that games continue moving forward
     * even when players don't complete their actions within the time limit,
     * preventing game stalls and maintaining engagement for all participants.
     *
     * @param model    the model facade providing access to game state and player management
     * @param callback the consumer function to handle collected events when time expires
     * @throws RuntimeException if a timer is already active (timeLeft is not 0)
     */
    public void startTimer(ModelFacade model, Consumer<List<Event>> callback) {
        if (timeLeft != 0) throw new RuntimeException("Time left is not 0");
        rotateHourglass();

        TimerTask currentTask = new TimerTask() {
            public void run() {
                if (timeLeft == 1) {
                    this.cancel();
                    if (hourglassPos == 0) {
                        EventContext.clear();

                        List<PlayerData> players = new ArrayList<>(model.getBoard().getStartingDeck())
                                .stream()
                                .filter(p -> !p.hasEndedInAdvance())
                                .toList();

                        for (PlayerData player : players)
                            model.setReady(player.getUsername());

                        List<Event> events = EventContext.getAndClear();
                        callback.accept(events);
                    }
                }
                timeLeft -= 1;
            }
        };
        this.timer.scheduleAtFixedRate(currentTask, 1000, 1000);
    }

}
