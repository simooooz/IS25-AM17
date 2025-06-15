package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.*;

public class SmugglersCard extends EnemiesCard {

    @JsonProperty private final int penalty;
    @JsonProperty private final Map<ColorType, Integer> reward;

    public SmugglersCard(int id, int level, boolean isLearner, int smugglersFirePower, int penalty, Map<ColorType, Integer> reward, int days) {
        super(id, level, isLearner, days, smugglersFirePower);
        this.penalty = penalty;
        this.reward = reward;
    }

    @Override
    public boolean defeatedMalus(ModelFacade model, PlayerData player) {
        model.setPlayerState(player.getUsername(), PlayerState.WAIT_REMOVE_GOODS);
        return true;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN) {
            if (value) {
                PlayerData player = board.getPlayerEntityByUsername(username);
                board.movePlayer(player, -1 * this.days);
                model.setPlayerState(username, PlayerState.WAIT_GOODS);
                return false;
            }
            else {
                model.setPlayerState(username, PlayerState.DONE);
                playerIndex++;
                return autoCheckPlayers(model, board);
            }
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_GOODS || commandType == PlayerState.WAIT_REMOVE_GOODS) {
            model.setPlayerState(username, PlayerState.DONE);
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, int number, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, penalty, deltaGood, batteries, username, board);
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, this.reward, deltaGood, batteries, username, board);
    }

}
