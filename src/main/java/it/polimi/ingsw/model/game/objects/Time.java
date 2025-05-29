package it.polimi.ingsw.model.game.objects;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
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
            timeLeft = 15;
            hourglassPos--;
        }
    }

    public void startTimer(ModelFacade model) {
        if (timeLeft != 0) throw new RuntimeException("Time left is not 0");
        rotateHourglass();

        TimerTask currentTask = new TimerTask() {
            public void run() {
                if (timeLeft == 1) {
                    this.cancel();
                    if (hourglassPos == 0) {
                        List<PlayerData> players = new ArrayList<>(model.getBoard().getStartingDeck());
                        for (PlayerData player : players)
                            model.setReady(player.getUsername());
                    }
                }
                timeLeft -= 1;
            }
        };
        this.timer.scheduleAtFixedRate(currentTask, 1000, 1000);
    }

}
