package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.network.messages.Message;

import java.util.List;
import java.util.Map;

/**
 * It creates via {@link ModelFacade} a game instance
 * and coordinates the interaction between view and model
 */
public class GameController {

    /**
     * {@link ModelFacade} ref: exposed methods for interacting with the model
     */
    private final ModelFacade model;

    /**
     * Constructor
     *
     * @param usernames players' usernames in the game
     */
    public GameController(List<String> usernames) {
        this.model = new ModelFacade(usernames);
    }

    public void startMatch() {
        model.startMatch();
    }

    public void showComponent(String username, int componentId) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.showComponent(componentId);
    }

    public void pickComponent(String username, int componentId) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.pickComponent(username, componentId);
    }

    public void releaseComponent(String username, int componentId) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.releaseComponent(username, componentId);
    }

    public void reserveComponent(String username, int componentId) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.reserveComponent(username, componentId);
    }

    public void insertComponent(String username, int componentId, int row, int col) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.insertComponent(username, componentId, row, col);
    }

    public void moveComponent(String username, int componentId, int row, int col) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.moveComponent(username, componentId, row, col);
    }

    public void rotateComponent(String username, int componentId, int num) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.rotateComponent(username, componentId, num);
    }

    public void lookCardPile(String username, int deckIndex) {
        if (model.getState() != GameState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username))
            throw new RuntimeException("Player is ready"); // TODO non lo so se quando è ready può ancora guardarle

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

    public void checkShip(String username, List<Integer> toRemove) {
        if (model.getState() != GameState.CHECK) throw new IllegalStateException("State is not CHECKING");
        model.checkShip(username, toRemove);
    }

    public void drawCard(String username) {
        if (model.getState() != GameState.DRAW_CARD) throw new IllegalStateException("State is not DRAW_CARD");
        model.nextCard(username);
    }

    public void activateCannons(String username, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) {
        if (model.getState() != GameState.PLAY_CARD) throw new IllegalStateException("State is not PLAY_CARD");
        model.activateCannons(username, batteriesIds, cannonComponentsIds);
    }

    public void activateEngines(String username, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        if (model.getState() != GameState.PLAY_CARD) throw new IllegalStateException("State is not PLAY_CARD");
        model.activateEngines(username, batteriesIds, engineComponentsIds);
    }

    public void activateShield(String username, int batteryId) {
        if (model.getState() != GameState.PLAY_CARD) throw new IllegalStateException("State is not PLAY_CARD");
        model.activateShield(username, batteryId);
    }

    public void updateGoods(String username, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        if (model.getState() != GameState.PLAY_CARD) throw new IllegalStateException("State is not PLAY_CARD");
        model.updateGoods(username, cargoHoldsIds, batteriesIds);
    }

    public void removeCrew(String username, List<Integer> cabinsIds) {
        if (model.getState() != GameState.PLAY_CARD) throw new IllegalStateException("State is not PLAY_CARD");
        model.removeCrew(username, cabinsIds);
    }

    public void rollDices(String username) {
        if (model.getState() != GameState.PLAY_CARD) throw new IllegalStateException("State is not PLAY_CARD");
        model.rollDices(username);
    }

    public void getBoolean(String username, boolean value) {
        if (model.getState() != GameState.PLAY_CARD) throw new IllegalStateException("State is not PLAY_CARD");
        model.getBoolean(username, value);
    }

    public void getIndex(String username, int value) {
        if (model.getState() != GameState.PLAY_CARD) throw new IllegalStateException("State is not PLAY_CARD");
        model.getIndex(username, value);
    }

    public void endFlight(String username) {
        if (model.getState() != GameState.DRAW_CARD || model.getState() != GameState.PLAY_CARD) throw new IllegalStateException("State is not DRAW_CARD or PLAY_CARD");
        model.endFlight(username);
    }

    public void playerRejoined(String username) {
        // todo
    }

    public void playerLeft(String username) {
        this.model.playerLeft(username);
    }

}
