package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.Pair;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.WarLine;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CombatZoneCard extends Card {

    @JsonProperty private final List<WarLine> warLines;
    @JsonProperty private int warLineIndex;
    @JsonProperty private final Pair<Optional<String>, Double> worst;

    private int playerIndex;
    private List<PlayerData> players;

    public CombatZoneCard(int id, int level, boolean isLearner, List<WarLine> warLines) {
        super(id, level, isLearner);
        this.warLines = warLines;
        this.worst = new Pair<>(Optional.empty(), 0.0);
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        if (board.getPlayersByPos().size() < 2)
            return true;
        else {
            this.warLineIndex = 0;
            this.playerIndex = 0;
            this.players = new ArrayList<>(board.getPlayersByPos());

            for (PlayerData player : players)
                model.setPlayerState(player.getUsername(), PlayerState.WAIT);

            return autoCheckPlayers(model, board);
        }
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            PlayerState newState = warLines.get(warLineIndex).getCriteriaType().countCriteria(player, worst);
            model.setPlayerState(player.getUsername(), newState);
            if (newState != PlayerState.DONE)
                return false;
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone && worst.getKey().isPresent()) { // Apply malus
            PlayerState newState = warLines.get(warLineIndex).getPenalty().resolve(model, board, worst.getKey().get());
            model.setPlayerState(worst.getKey().get(), newState);

            // Update players because flight days could be changed (due to penalty)
            this.players = new ArrayList<>(board.getPlayersByPos().stream().filter(oldP -> players.contains(oldP)).toList());

            if (newState == PlayerState.DONE)
                worst.setKey(Optional.empty());
        }

        if (hasDone && worst.getKey().isEmpty()) { // Malus already applied, go to the next line
            warLineIndex++;
            if (warLineIndex >= warLines.size())
                return true;
            else {
                for (PlayerData player : players)
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

            if (worst.getKey().isEmpty() || worst.getValue() > value) { // Update worst
                worst.setKey(Optional.of(username));
                worst.setValue(value.doubleValue());
            }

            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        else if (commandType == PlayerState.WAIT_ROLL_DICES) {
            warLines.get(warLineIndex).getPenalty().doCommandEffects(commandType, value, model, board, username);

            if (model.getPlayerState(username) == PlayerState.DONE) // If nested doCommandEffect has put state to DONE
                worst.setKey(Optional.empty());

            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIELD) {
            warLines.get(warLineIndex).getPenalty().doCommandEffects(commandType, value, model, board, username);

            if (model.getPlayerState(username) == PlayerState.DONE) // If nested doCommandEffect has put state to DONE
                worst.setKey(Optional.empty());

            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            model.setPlayerState(username, PlayerState.DONE);

            if (worst.getKey().isEmpty() || worst.getValue() > value) { // Update worst
                worst.setKey(Optional.of(username));
                worst.setValue(value);
            }

            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW || commandType == PlayerState.WAIT_REMOVE_GOODS) {
            worst.setKey(Optional.empty());
            model.setPlayerState(username, PlayerState.DONE);

            return autoCheckPlayers(model, board);
        }
        else if (commandType == PlayerState.WAIT_SHIP_PART) {
            warLines.get(warLineIndex).getPenalty().doCommandEffects(commandType, model, board, username);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW) {
            int num = warLines.get(warLineIndex).getPenalty().getPenaltyNumber();
            super.doSpecificCheck(commandType, cabins, num, username, board);
        }
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, int number, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        if (commandType == PlayerState.WAIT_REMOVE_GOODS) {
            int num = warLines.get(warLineIndex).getPenalty().getPenaltyNumber();
            super.doSpecificCheck(commandType, num, deltaGood, batteries, username, board);
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean doLeftGameEffects(PlayerState state, ModelFacade model, Board board, String username) {
        if ( // Player was the worst
            state == PlayerState.WAIT_REMOVE_CREW || state == PlayerState.WAIT_REMOVE_GOODS ||
            state == PlayerState.WAIT_ROLL_DICES || state == PlayerState.WAIT_SHIP_PART ||
            state == PlayerState.WAIT_SHIELD
        )
            worst.setKey(Optional.empty());

        PlayerData player = board.getPlayerEntityByUsername(username);
        int indexOfLeftPlayer = players.indexOf(player);

        if (playerIndex > indexOfLeftPlayer) {
            players.remove(playerIndex);
            playerIndex--;
        }
        else if (playerIndex == indexOfLeftPlayer) {
            players.remove(playerIndex);
            return autoCheckPlayers(model, board);
        }
        else
            players.remove(playerIndex);

        return false;
    }

}