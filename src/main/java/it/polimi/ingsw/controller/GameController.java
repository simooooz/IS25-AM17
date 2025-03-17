package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.*;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {

    private final ModelFacade model;
    private GameState state;
    private final Timer timer;

    public GameController(List<String> usernames) {
        this.model = new ModelFacade(usernames);
        this.timer = new Timer();
    }

    public void startMatch() {
        this.state = GameState.BUILDING;
        startTimer();
    }

    public void showComponent(String username, ...) throws Exception {
        if (state != GameState.BUILDING) throw new Exception();
    }

    public void insertComponent(String username, Component component, int row, int col) throws Exception {
        if (state != GameState.BUILDING) throw new Exception();

        // TODO Check if player is connected
        model.insertComponent(username, component, row, col);

    }

    public void rotateComponent(String username, Component component, boolean clockwise) throws Exception {
        if (state != GameState.BUILDING) throw new Exception();

        // TODO Check if player is connected
        // TODO Check if component is already attached
        model.rotateComponent(component, clockwise);
    }

    public void moveHourglass(String username) throws Exception {
        if (state != GameState.BUILDING) throw new Exception();
        if (model.getTimeLeft() != 0) throw new Exception();

        // TODO Check if player is connected

        // Check if player is ready
        startTimer();
    }

    public void drawCard(String username) throws Exception {
        if (state != GameState.PLAY_CARD) throw new Exception();

        // TODO Check if player is connected
        // Check playerByUsername == board.getPlayerByPos().getFirst()
        board.drawCard();
    }

    public void updateGoods(String username, List<SpecialCargoHoldsComponent> cargoComponents) throws Exception {
        if (state != GameState.WAIT_GOODS) throw new Exception();

        // TODO Check if player is connected

        // Call method to update goods
    }

    public void updateBatteries(String username, List<BatteryComponent> batteryComponents) throws Exception {
        if (state != GameState.WAIT_BATTERIES) throw new Exception();

        // TODO Check if player is connected

        // Call method to update batteries
    }

    public void activateCannons(String username, List<CannonComponent> cannonComponents) throws Exception {
        if (state != GameState.WAIT_CANNONS) throw new Exception();

        // TODO Check if player is connected

        // Call method to activate cannons

        if (!cannonComponents.isEmpty()) state = GameState.WAIT_BATTERIES;
    }

    public void activateEngines(String username, List<EngineComponent> engineComponents) throws Exception {
        if (state != GameState.WAIT_ENGINES) throw new Exception();

        // TODO Check if player is connected

        // Call method to activate engines

        if (!engineComponents.isEmpty()) state = GameState.WAIT_BATTERIES;
    }

    private void startTimer() {
        model.rotateHourglass();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (model.getTimeLeft() == 1) {
                    timer.cancel();
                    if (model.getHourglassPos() == 0)
                        state = GameState. //
                }
                model.decrementTimeLeft();
            }
        }, 1000, 1000);
    }

}
