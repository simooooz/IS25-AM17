package it.polimi.ingsw.model.game.objects;

import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class Time {

    private int timeLeft;
    private int hourglassPos;
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

    private void rotateHourglass() {
        if (hourglassPos > 0) {
            timeLeft = 10;
            hourglassPos--;
        }
    }

    public void startTimer(ModelFacade model, Consumer<List<GameEvent>> callback) {
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

                        List<GameEvent> events = EventContext.getAndClear();
                        callback.accept(events);
                    }
                }
                timeLeft -= 1;
            }
        };
        this.timer.scheduleAtFixedRate(currentTask, 1000, 1000);
    }

}
