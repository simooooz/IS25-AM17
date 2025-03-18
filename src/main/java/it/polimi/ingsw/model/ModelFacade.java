package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;

public class ModelFacade {

    private final Game game;

    public ModelFacade(List<String> usernames) {
        this.game = new Game(usernames);
    }

    public GameState getGameState() {
        return game.getState();
    }

    public void setGameState(GameState state) {
        game.setState(state);
    }

    public List<PlayerData> getPlayersByPos() {
        return game.getBoard().getPlayersByPos();
    }

    public void rotateHourglass() {
        game.getBoard().getTimeManagment().rotateHourglass();
    }

    public int getTimeLeft() {
        return game.getBoard().getTimeManagment().getTimeLeft();
    }

    public void decrementTimeLeft() {
        game.getBoard().getTimeManagment().decrementTimeLeft();
    }

    public Card drawCard() throws Exception {
        return game.getBoard().drawCard();
    }

    public void resolveCard(Card card) throws Exception {
        card.resolve(game, game.getBoard().getCurrentPlayer());
    }

    public void insertComponent(String username,  Component component, int row, int col) throws Exception {
        Ship ship = game.getBoard().getPlayer(username).getShip();
        component.insertComponent(ship, row, col);
    }

    public void rotateComponent(Component component, boolean clockwise) throws Exception {
        component.rotateComponent(clockwise);
    }

    // UPDATE BATTERIES, CABIN
    // ACTIVATE CANNONS AND ENGINES

}
