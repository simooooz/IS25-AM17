package it.polimi.ingsw.model.game.objects;

import java.util.Timer;
import java.util.TimerTask;

public class Time {

    private int timeLeft;
    private final Timer timer;
    private int hourglassPos;

    public Time() {
        this.timer = new Timer();
        this.hourglassPos = 4;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public int getHourglassPos() {
        return hourglassPos;
    }

    public void decrementHourglassPos() {
        if (hourglassPos > 1) {
            hourglassPos--;
        }
    }

    public void startTimer() {
        decrementHourglassPos();
        timeLeft = 60;
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (timeLeft == 1) {
                    timer.cancel();
                }
                timeLeft--;
            }
        }, 1000, 1000);
    }

}
