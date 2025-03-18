package it.polimi.ingsw.model.game.objects;

import java.util.Timer;
import java.util.TimerTask;

public class Time {

    private int timeLeft;
    private int hourglassPos;

    public Time() {
        this.hourglassPos = 4;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public int getHourglassPos() {
        return hourglassPos;
    }

    public void rotateHourglass() {
        if (hourglassPos > 1) {
            timeLeft = 60;
            hourglassPos--;
        }
    }

    public void decrementTimeLeft() {
        timeLeft -= 1;
    }

}
