package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;

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
    private final boolean learnerMode;

    /**
     * Constructor
     *
     * @param usernames players' usernames in the game
     */
    public GameController(List<String> usernames, boolean learnerMode) {
        this.model = new ModelFacade(usernames, learnerMode);
        this.learnerMode = learnerMode;
    }

    public void startMatch() {
        model.startMatch();
    }

    public void pickComponent(String username, int componentId) {
        if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.pickComponent(username, componentId);
    }

    public void releaseComponent(String username, int componentId) {
        if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.releaseComponent(username, componentId);
    }

    public void reserveComponent(String username, int componentId) {
        if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");
        if (learnerMode) throw new IllegalArgumentException("Match is in learner mode");

        model.reserveComponent(username, componentId);
    }

    public void insertComponent(String username, int componentId, int row, int col, int rotations, boolean weld) {
        if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.insertComponent(username, componentId, row, col, rotations, weld);
    }

    public void moveComponent(String username, int componentId, int row, int col, int rotations) {
        if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.moveComponent(username, componentId, row, col);
        model.rotateComponent(username, componentId, rotations);
    }

    public void rotateComponent(String username, int componentId, int num) {
        if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.rotateComponent(username, componentId, num);
    }

    public List<Card> lookCardPile(String username, int deckIndex) {
        if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (model.isPlayerReady(username))
            throw new RuntimeException("Player is ready"); // TODO non lo so se quando è ready può ancora guardarle

        return model.lookCardPile(username, deckIndex);
    }

    public void moveHourglass(String username) {
        if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        if (learnerMode) throw new IllegalArgumentException("Match is in learner mode");
        model.moveHourglass(username);
    }

    public void setReady(String username) {
        if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        model.setReady(username);
    }

    public void checkShip(String username, List<Integer> toRemove) {
        if (model.getPlayerState(username) != PlayerState.CHECK) throw new IllegalStateException("State is not CHECKING");
        model.checkShip(username, toRemove);
    }

    public void chooseAlien(String username, Map<Integer, AlienType> aliensIds) {
        if (model.getPlayerState(username) != PlayerState.WAIT_ALIEN) throw new IllegalStateException("State is not WAIT_ALIEN");
        model.chooseAlien(username, aliensIds);
    }

    public void chooseShipPart(String username, int partIndex) {
        if (model.getPlayerState(username) != PlayerState.WAIT_SHIP_PART) throw new IllegalStateException("State is not WAIT_SHIP_PART");
        model.chooseShipPart(username, partIndex);
    }

    public void drawCard(String username) {
        if (model.getPlayerState(username) != PlayerState.DRAW_CARD) throw new IllegalStateException("State is not DRAW_CARD");
        model.nextCard();
    }

    public void activateCannons(String username, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) {
        if (model.getPlayerState(username) != PlayerState.WAIT_CANNONS) throw new IllegalStateException("State is not WAIT_CANNONS");
        model.activateCannons(username, batteriesIds, cannonComponentsIds);
    }

    public void activateEngines(String username, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        if (model.getPlayerState(username) != PlayerState.WAIT_ENGINES) throw new IllegalStateException("State is not WAIT_ENGINES");
        model.activateEngines(username, batteriesIds, engineComponentsIds);
    }

    public void activateShield(String username, Integer batteryId) {
        if (model.getPlayerState(username) != PlayerState.WAIT_SHIELD) throw new IllegalStateException("State is not WAIT_SHIELD");
        model.activateShield(username, batteryId);
    }

    public void updateGoods(String username, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        if (model.getPlayerState(username) != PlayerState.WAIT_GOODS && model.getPlayerState(username) != PlayerState.WAIT_REMOVE_GOODS) throw new IllegalStateException("State is not WAIT_GOODS or WAIT_REMOVE_GOODS");
        model.updateGoods(username, cargoHoldsIds, batteriesIds);
    }

    public void removeCrew(String username, List<Integer> cabinsIds) {
        if (model.getPlayerState(username) != PlayerState.WAIT_REMOVE_CREW) throw new IllegalStateException("State is not WAIT_REMOVE_CREW");
        model.removeCrew(username, cabinsIds);
    }

    public void rollDices(String username) {
        if (model.getPlayerState(username) != PlayerState.WAIT_ROLL_DICES) throw new IllegalStateException("State is not WAIT_ROLL_DICES");
        model.rollDices(username);
    }

    public void getBoolean(String username, boolean value) {
        if (model.getPlayerState(username) != PlayerState.WAIT_BOOLEAN) throw new IllegalStateException("State is not WAIT_BOOLEAN");
        model.getBoolean(username, value);
    }

    public void getIndex(String username, int value) {
        if (model.getPlayerState(username) != PlayerState.WAIT_INDEX) throw new IllegalStateException("State is not WAIT_INDEX");
        model.getIndex(username, value);
    }

    public void endFlight(String username) {;
        if (learnerMode) throw new IllegalArgumentException("Match is in learner mode");
        model.endFlight(username);
    }

    public void playerRejoined(String username) {
        // todo
    }

    public void playerLeft(String username) {
        this.model.playerLeft(username);
    }

    // TEST only
    public PlayerState getState(String username) {
        return model.getPlayerState(username);
    }

    public ModelFacade getModel() {
        return model;
    }

}
