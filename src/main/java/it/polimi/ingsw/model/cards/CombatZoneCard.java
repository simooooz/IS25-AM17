package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.CriteriaType;
import it.polimi.ingsw.model.cards.utils.PenaltyCombatZone;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CombatZoneCard extends Card {

    @JsonProperty private final List<SimpleEntry<CriteriaType, PenaltyCombatZone>> warLines;
    @JsonProperty private int warLineIndex;
    private int playerIndex;
    @JsonProperty private final SimpleEntry<SimpleEntry<Character, Optional<PlayerData>>, Double> worst;

    public CombatZoneCard(int id, int level, boolean isLearner, List<SimpleEntry<CriteriaType, PenaltyCombatZone>> warLines) {
        super(id, level, isLearner);
        this.warLines = warLines;

        SimpleEntry<Character, Optional<PlayerData>> temp = new SimpleEntry<>('a', Optional.empty());
        this.worst = new SimpleEntry<>(temp, 0.0);
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        if (board.getPlayersByPos().size() < 2)
            return true;
        else {
            this.warLineIndex = 0;
            this.playerIndex = 0;

            for (PlayerData player : board.getPlayersByPos())
                model.setPlayerState(player.getUsername(), PlayerState.WAIT);

            return autoCheckPlayers(model, board);
        }
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) {
            PlayerData player = board.getPlayersByPos().get(playerIndex);

            PlayerState newState = warLines.get(warLineIndex).getKey().countCriteria(player, worst);
            model.setPlayerState(player.getUsername(), newState);
            if (newState != PlayerState.DONE)
                return false;
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : board.getPlayersByPos())
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone && worst.getKey().getValue().isPresent()) { // Apply malus
            PlayerState newState = warLines.get(warLineIndex).getValue().resolve(model, board, worst.getKey().getValue().get());
            model.setPlayerState(worst.getKey().getValue().get().getUsername(), newState);
            if (newState == PlayerState.DONE)
                worst.getKey().setValue(Optional.empty());
        }

        if (hasDone && worst.getKey().getValue().isEmpty()) { // Malus already applied, go to the next line
            warLineIndex++;
            if (warLineIndex >= warLines.size())
                return true;
            else {
                for (PlayerData player : board.getPlayersByPos())
                    model.setPlayerState(player.getUsername(), PlayerState.WAIT);
                this.playerIndex = 0;
                return autoCheckPlayers(model, board);
            }
        }
        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ENGINES) {
            model.setPlayerState(username, PlayerState.DONE);

            if (worst.getKey().getValue().isEmpty() || worst.getValue() > value) { // Update worst
                PlayerData player = board.getPlayerEntityByUsername(username);
                worst.getKey().setValue(Optional.of(player));
                worst.setValue(value.doubleValue());
            }

            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        else if (commandType == PlayerState.WAIT_ROLL_DICES) {
            warLines.get(warLineIndex).getValue().doCommandEffects(commandType, value, model, board, username);

            if (model.getPlayerState(username) == PlayerState.DONE) // If nested doCommandEffect has put state to DONE
                worst.getKey().setValue(Optional.empty());

            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIELD) {
            warLines.get(warLineIndex).getValue().doCommandEffects(commandType, value, model, board, username);

            if (model.getPlayerState(username) == PlayerState.DONE) // If nested doCommandEffect has put state to DONE
                worst.getKey().setValue(Optional.empty());

            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            model.setPlayerState(username, PlayerState.DONE);

            if (worst.getKey().getValue().isEmpty() || worst.getValue() > value) { // Update worst
                PlayerData player = board.getPlayerEntityByUsername(username);
                worst.getKey().setValue(Optional.of(player));
                worst.setValue(value);
            }

            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW || commandType == PlayerState.WAIT_REMOVE_GOODS) {
            worst.getKey().setValue(Optional.empty());
            model.setPlayerState(username, PlayerState.DONE);

            return autoCheckPlayers(model, board);
        }
        else if (commandType == PlayerState.WAIT_SHIP_PART) {
            warLines.get(warLineIndex).getValue().doCommandEffects(commandType, model, board, username);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW) {
            int num = warLines.get(warLineIndex).getValue().getPenaltyNumber();
            super.doSpecificCheck(commandType, cabins, num, username, board);
        }
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, int number, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        if (commandType == PlayerState.WAIT_REMOVE_GOODS) {
            int num = warLines.get(warLineIndex).getValue().getPenaltyNumber();
            super.doSpecificCheck(commandType, num, deltaGood, batteries, username, board);
        }
    }

}