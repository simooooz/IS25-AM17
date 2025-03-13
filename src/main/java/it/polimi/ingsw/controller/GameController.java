package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.game.Board;

import java.util.List;

public class GameController {

    private final Board board;
    private GameState state;

    public GameController(Board board) {
        // Forse riceve gli username dio porco
        this.board = new Board();
    }

    public void showComponent(String username, ...) throws Exception {
        if (state != GameState.BUILDING) throw new Exception();
    }

    public void weldComponent(String username, Component component, int row, int col) throws Exception {
        if (state != GameState.BUILDING) throw new Exception();

        // TODO Check if player is connected
        // Può essere un'idea esporre un metodo getPlayerData che ritorna un playerData da un dato username
        // Verifica che il componente non sia stato inserito in un'altra navicella
        component.insertComponent(, row, col);

    }

    public void rotateComponent(String username, Component component, boolean clockwise) throws Exception {
        if (state != GameState.BUILDING) throw new Exception();

        // TODO Check if player is connected

        component.rotateComponent(clockwise);
    }

    public void drawCard(String username) throws Exception {
        if (state != GameState.PLAY_CARD) throw new Exception();

        // TODO Check if player is connected
        // Check playerByUsername == board.getPlayerByPos.getFirst()
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


}
