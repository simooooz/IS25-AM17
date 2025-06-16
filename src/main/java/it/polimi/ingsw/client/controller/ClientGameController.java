package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.ClientGameModelAdvancedMode;
import it.polimi.ingsw.client.model.ClientGameModelLearnerMode;
import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.exceptions.IllegalStateException;

import java.util.*;

public class ClientGameController {

    private final ClientGameModel model;

    public ClientGameController(List<String> usernames, boolean learnerMode) {
        this.model = learnerMode ? new ClientGameModelLearnerMode(usernames) : new ClientGameModelAdvancedMode(usernames);
    }

    public void matchStarted() {
        model.matchStarted();
    }

    public void componentPicked(String username, int componentId) {
        model.componentPicked(username, componentId);
    }

    public void componentReleased(String username, int componentId) {
        model.componentReleased(username, componentId);
    }

    public void componentReserved(String username, int componentId) {
        model.componentReserved(username, componentId);
    }

    public void componentInserted(String username, int componentId, int row, int col) {
        model.componentInserted(username, componentId, row, col);
    }

    public void componentMoved(String username, int componentId, int row, int col) {
        model.componentMoved(username, componentId, row, col);
    }

    public void componentRotated(int componentId, int num) {
        model.componentRotated(componentId, num);
    }

    public void componentDestroyed(String username, int componentId) {
        model.componentDestroyed(username, componentId);
    }

    public void cardPileLooked(String username, int deckIndex, List<ClientCard> cards) {
        model.cardPileLooked(username, deckIndex, cards);
    }

    public void cardPileLooked(String username, int deckIndex) {
        model.cardPileLooked(username, deckIndex);
    }

    public void cardPileReleased(String username) {
        model.cardPileReleased(username);
    }

    public void hourglassMoved() {
        model.hourglassMoved();
    }

    public void playersPositionUpdated(List<String> starting, List<AbstractMap.SimpleEntry<String, Integer>> players) {
        model.playersPositionUpdated(starting, players);
    }

    public void cardRevealed(ClientCard card) {
        model.cardRevealed(card);
    }

    public void batteriesUpdated(int id, int batteries) {
        model.batteriesUpdated(id, batteries);
    }

    public void goodsUpdated(int id, List<ColorType> goods) {
        model.goodsUpdated(id, goods);
    }

    public void crewUpdated(int id, int humans, AlienType alien) {
        model.crewUpdated(id, humans, alien);
    }

    public void cardUpdated(ClientCard card) {
        model.cardUpdated(card);
    }

    public void creditsUpdated(String username, Integer credits) {
        model.creditsUpdated(username, credits);
    }

    public void playersStateUpdated(Map<String, PlayerState> newStates) {
        model.playersStateUpdated(newStates);
    }

    public void flightEnded(String username) {
        model.flightEnded(username);
    }

    public void insertComponent(String username, int componentId, int row, int col, int rotations, boolean weld) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.cardPileReleased(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("Player is not building");

        model.insertComponent(username, componentId, row, col, rotations, weld);
    }

    public void moveComponent(String username, int componentId, int row, int col, int rotations) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.cardPileReleased(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("Player is not building");

        model.moveComponent(username, componentId, row, col, rotations);
    }

    public void rotateComponent(String username, int componentId, int num) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.cardPileReleased(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("Player is not building");

        model.rotateComponent(username, componentId, num);
    }

    public ClientGameModel getModel() {
        return model;
    }

}
