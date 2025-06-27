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
 */
public class Time {

    /**
     * The current time remaining in the active timer period (in seconds)
     */
    private int timeLeft;

    /**
     * The current position of the hourglass
     */
    private int hourglassPos;

    /**
     * The timer instance used for periodic time updates and countdown management
     */
    private final Timer timer;

    public Time() {
        this.timer = new Timer();
        this.hourglassPos = 3;
    }

    public int getHourglassPos() {
        return hourglassPos;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    /**
     * Rotates the hourglass to allocate a new time period if positions are available.
     */
    private void rotateHourglass() {
        if (hourglassPos > 0) {
            timeLeft = 61;
            hourglassPos--;
        }
    }

    /**
     * Starts a new timer countdown period with automatic progression handling.
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
