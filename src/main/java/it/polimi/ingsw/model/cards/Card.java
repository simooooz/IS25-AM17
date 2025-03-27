package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class Card {

    private final int level;
    private final boolean isLearner;
    Map<String, PlayerState> playersState;

    public Card(int level, boolean isLearner) {
        this.level = level;
        this.isLearner = isLearner;
        this.playersState = new HashMap<>();
    }

    public int getLevel() {
        return level;
    }

    public boolean getIsLearner() {
        return isLearner;
    }

    public Map<String, PlayerState> getPlayersState() {
        return playersState;
    }

    public abstract boolean startCard(Board board);

    protected boolean changeState(Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    protected void endCard(Board board) {
        for (PlayerData player : board.getPlayersByPos())
            if (player.getShip().getCrew() == 0)
                board.moveToStartingDeck(player);

        int leaderPos = board.getPlayers().getFirst().getValue();
        for (SimpleEntry<PlayerData, Integer> entry : board.getPlayers())
            if (leaderPos >= entry.getValue() + 24)
                board.moveToStartingDeck(entry.getKey());

        for (PlayerData player : board.getWantEndFlight())
            board.moveToStartingDeck(player);
        board.getWantEndFlight().clear();
    }

    public GameState changeCardState(Board board, String username) {
        boolean finish = changeState(board, username);
        if (finish) {
            if (board.getCardPilePos() == board.getCardPile().size() -1) return GameState.END;
            return GameState.DRAW_CARD;
        }
        return GameState.PLAY_CARD;
    }

    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> rewards, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        if (commandType != PlayerState.WAIT_GOODS) return;

        for (ColorType good : ColorType.values())
            if (deltaGood.get(good) > rewards.get(good))
                throw new GoodNotValidException("Reward check not valid");

        if (!batteries.isEmpty())
            throw new BatteryComponentNotValidException("Battery component list should be empty");
    }

    public void doSpecificCheck(PlayerState commandType, int number, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        if (commandType != PlayerState.WAIT_REMOVE_GOODS) return;
        Ship ship = board.getPlayerEntityByUsername(username).getShip();

        for (ColorType goodType : ColorType.values()) {
            number += deltaGood.get(goodType);
            if (number > 0 && (ship.getGoods().get(goodType) + deltaGood.get(goodType) != 0))
                throw new GoodNotValidException("There are more valuable goods in the ship");
        }

        if (number == 0 && batteries.isEmpty()) return;
        else if (number == 0) throw new IllegalArgumentException("Battery components list should be empty");

        if (batteries.size() > number)
            throw new BatteryComponentNotValidException("Too many battery components provided");
        else if (batteries.size() < number && ship.getBatteries() >= number)
            throw new BatteryComponentNotValidException("Too few battery components provided");
    }

    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW && cabins.size() != toRemove)
            throw new IllegalArgumentException("Too few cabin components provided");
    }

    public void doSpecificCheck(PlayerState commandType, List<CannonComponent> cannons, String username, Board board) {
        throw new RuntimeException("Method not valid");
    }

    public void doCommandEffects(PlayerState commandType, Integer value, String username, Board board) {
        throw new RuntimeException("Method not valid");
    }

    public void doCommandEffects(PlayerState commandType, Double value, String username, Board board) {
        throw new RuntimeException("Method not valid");
    }

    public void doCommandEffects(PlayerState commandType, Boolean value, String username, Board board) {
        throw new RuntimeException("Method not valid");
    }

}
