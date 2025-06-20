package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbandonedStationCard extends Card{

    @JsonProperty private final int crew;
    @JsonProperty private final int days;
    @JsonProperty private final Map<ColorType, Integer> goods;
    private List<PlayerData> players;

    private int playerIndex;
    private boolean shipConquered;

    public AbandonedStationCard(int id, int level, boolean isLearner, int crew, int days, Map<ColorType, Integer> goods) {
        super(id, level, isLearner);
        this.crew = crew;
        this.days = days;
        this.goods = goods;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.playerIndex = 0;
        this.shipConquered = false;
        this.players = new ArrayList<>(board.getPlayersByPos());

        for (PlayerData player : this.players)
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        return autoCheckPlayers(model);
    }

    private boolean autoCheckPlayers(ModelFacade model) {
        for (; playerIndex < this.players.size(); playerIndex++) {
            PlayerData player = this.players.get(playerIndex);

            if (shipConquered)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (player.getShip().getCrew() < crew) // User loses automatically
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else { // User could win
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                return false;
            }
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : this.players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        return hasDone;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN && value) {
            model.setPlayerState(username, PlayerState.WAIT_GOODS);
            shipConquered = true;
            return false;
        }
        else if (commandType == PlayerState.WAIT_BOOLEAN) {
            model.setPlayerState(username, PlayerState.DONE);
            playerIndex++;
            return autoCheckPlayers(model);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_GOODS) {
            model.setPlayerState(username, PlayerState.DONE);

            PlayerData player = board.getPlayerEntityByUsername(username);
            board.movePlayer(player, days * -1);

            playerIndex++;
            return autoCheckPlayers(model);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, this.goods, deltaGood, batteries, username, board);
    }

}
