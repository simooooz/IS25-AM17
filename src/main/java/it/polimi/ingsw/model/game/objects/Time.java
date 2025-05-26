package it.polimi.ingsw.model.game.objects;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.Timer;
import java.util.TimerTask;

public class Time {

    private int timeLeft;
    private int hourglassPos;
    private final Timer timer;

    public Time() {
        this.timer = new Timer();
        this.hourglassPos = 3;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public int getHourglassPos() {
        return hourglassPos;
    }

    private void rotateHourglass() {
        if (hourglassPos > 0) {
            timeLeft = 60;
            hourglassPos--;
        }
    }

    public void startTimer(ModelFacade model) {
        if (timeLeft != 0) throw new RuntimeException("Time left is not 0");
        rotateHourglass();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (timeLeft == 1) {
                    timer.cancel();
                    if (hourglassPos == 0)
                        for (PlayerData player : model.getBoard().getStartingDeck())
                            model.setReady(player.getUsername());
                }
                timeLeft -= 1;
            }
        }, 1000, 1000);
    }

}
