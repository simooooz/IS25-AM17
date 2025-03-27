package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {

    private final ModelFacade model;

    public GameController(List<String> usernames) {
        this.model = new ModelFacade(usernames);
    }

    public void startMatch() {
        model.startMatch();
    }

    public void showComponent(String username, Component component) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.showComponent(component);
    }

    public void pickComponent(String username, Component component) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.pickComponent(username, component);
    }

    public void releaseComponent(String username, Component component) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.releaseComponent(username, component);
    }

    public void reserveComponent(String username, Component component) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.reserveComponent(username, component);
    }

    public void insertComponent(String username, Component component, int row, int col) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.insertComponent(username, component, row, col);
    }

    public void moveComponent(String username, Component component, int row, int col) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.moveComponent(username, component, row, col);
    }

    public void rotateComponent(String username, Component component, boolean clockwise) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.rotateComponent(username, component, clockwise);
    }

    public void lookCardPile(String username, int deckIndex) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready"); // TODO non lo so se quando è ready può ancora guardarle

        model.lookCardPile(username, deckIndex);
    }

    public void moveHourglass(String username) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        model.moveHourglass(username);
    }

    public void setReady(String username) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        model.setReady(username);
    }

    public void checkShip(String username, List<Component> toRemove) {
        if (model.getState() != GameState.CHECK) throw new IllegalStateException("State is not CHECKING");
        model.checkShip(username, toRemove);
    }

    public void drawCard(String username) {
        if (model.getState() != GameState.DRAW_CARD) throw new IllegalStateException("State is not DRAW_CARD");
        model.nextCard(username);
    }

    public void activateCannons(String username, List<BatteryComponent> batteries, List<CannonComponent> cannonComponents) {
        if (model.getState() != GameState.PLAY_CARD) throw new java.lang.IllegalStateException("State is not PLAY_CARD");
        model.activateCannons(username, batteries, cannonComponents);
    }

    public void activateEngines(String username, List<BatteryComponent> batteries, List<EngineComponent> engineComponents) {
        if (model.getState() != GameState.PLAY_CARD) throw new java.lang.IllegalStateException("State is not PLAY_CARD");
        model.activateEngines(username, batteries, engineComponents);
    }

    public void activateShield(String username, BatteryComponent battery) {
        if (model.getState() != GameState.PLAY_CARD) throw new java.lang.IllegalStateException("State is not PLAY_CARD");
        model.activateShield(username, battery);
    }

    public void updateGoods(String username, Map<SpecialCargoHoldsComponent, List<ColorType>> cargoHolds, List<BatteryComponent> batteries) {
        if (model.getState() != GameState.PLAY_CARD) throw new java.lang.IllegalStateException("State is not PLAY_CARD");
        model.updateGoods(username, cargoHolds, batteries);
    }

    public void removeCrew(String username, List<CabinComponent> cabins) {
        if (model.getState() != GameState.PLAY_CARD) throw new java.lang.IllegalStateException("State is not PLAY_CARD");
        model.removeCrew(username, cabins);
    }

    public void rollDices(String username) {
        if (model.getState() != GameState.PLAY_CARD) throw new java.lang.IllegalStateException("State is not PLAY_CARD");
        model.rollDices(username);
    }

    public void getBoolean(String username, boolean value) {
        if (model.getState() != GameState.PLAY_CARD) throw new java.lang.IllegalStateException("State is not PLAY_CARD");
        model.getBoolean(username, value);
    }

    public void getIndex(String username, int value) {
        if (model.getState() != GameState.PLAY_CARD) throw new java.lang.IllegalStateException("State is not PLAY_CARD");
        model.getIndex(username, value);
    }

    public void endFlight(String username) {
        if (model.getState() != GameState.DRAW_CARD || model.getState() != GameState.PLAY_CARD) throw new java.lang.IllegalStateException("State is not DRAW_CARD or PLAY_CARD");
        model.endFlight(username);
    }

    public Map<PlayerData, Integer> calcWinner() {
        if (model.getState() != GameState.END) throw new IllegalStateException("State is not END");

        Map<PlayerData, Integer> points = new HashMap<>();


        return points;
    }

}
