package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;
import java.util.Map;

public class AbandonedStationCard extends Card{

    private final int crew;
    private final int days;
    private final Map<ColorType, Integer> goods;

    private int playerIndex;
    private int conquerorPlayerIndex;
    private boolean shipConquered;

    public AbandonedStationCard(int level, boolean isLearner, int crew, int days, Map<ColorType, Integer> goods) {
        super(level, isLearner);
        this.crew = crew;
        this.days = days;
        this.goods = goods;
    }

    @Override
    public boolean startCard(Board board) {
        this.playerIndex = 0;
        this.conquerorPlayerIndex = -1;
        this.shipConquered = false;

        for (PlayerData player : board.getPlayersByPos())
            playersState.put(player.getUsername(), PlayerState.WAIT);
        return autoCheckPlayers(board);
    }

    @Override
    protected boolean changeState(Board board, String username) {

        PlayerState actState = playersState.get(username);

        switch (actState) {
            case WAIT_GOODS -> {
                PlayerData player = board.getPlayerEntityByUsername(username);
                playersState.put(username, PlayerState.DONE);
                board.movePlayer(player, days * -1);
            }
            case WAIT_BOOLEAN -> {
                if (conquerorPlayerIndex == board.getPlayersByPos().indexOf(board.getPlayerEntityByUsername(username))) {
                    playersState.put(username, PlayerState.WAIT_GOODS);
                    shipConquered = true;
                }
                else
                    playersState.put(username, PlayerState.DONE);
            }
        }

        playerIndex++;
        return autoCheckPlayers(board);
    }

    private boolean autoCheckPlayers(Board board) {
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) {
            PlayerData player = board.getPlayersByPos().get(playerIndex);

            if (shipConquered)
                playersState.put(player.getUsername(), PlayerState.DONE);
            else if (player.getShip().getCrew() < crew) // User loses automatically
                playersState.put(player.getUsername(), PlayerState.DONE);
            else { // User could win
                playersState.put(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                return false;
            }
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : board.getPlayersByPos())
            if (playersState.get(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone) {
            endCard(board);
            return true;
        }

        return false;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_BOOLEAN && value)
            conquerorPlayerIndex = board.getPlayersByPos().indexOf(player);
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, this.goods, deltaGood, batteries, username, board);
    }

}
