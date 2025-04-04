package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.CriteriaType;
import it.polimi.ingsw.model.cards.utils.PenaltyCombatZone;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CombatZoneCard extends Card{

    private final List<SimpleEntry<CriteriaType, PenaltyCombatZone>> warLines;
    private int warLineIndex;
    private int playerIndex;
    private SimpleEntry<SimpleEntry<Character, Optional<PlayerData>>, Double> worst;

    public CombatZoneCard(int level, boolean isLearner, List<SimpleEntry<CriteriaType, PenaltyCombatZone>> warLines) {
        super(level, isLearner);
        this.warLines = warLines;
    }

    @Override
    public boolean startCard(Board board) {
        if (board.getPlayersByPos().size() < 2) {
            endCard(board);
            return true;
        }
        else {
            this.warLineIndex = 0;
            this.playerIndex = 0;
            SimpleEntry<Character, Optional<PlayerData>> temp = new SimpleEntry<>('a', Optional.empty());
            this.worst = new SimpleEntry<>(temp, 0.0);

            for (PlayerData player : board.getPlayersByPos())
                playersState.put(player.getUsername(), PlayerState.WAIT);

            return autoCheckPlayers(board);
        }
    }

    @Override
    protected boolean changeState(Board board, String username) {

        PlayerState actState = playersState.get(username);

        switch (actState) {
            case WAIT_CANNONS, WAIT_ENGINES, WAIT_SHIELD, WAIT_ROLL_DICES -> playersState.put(username, PlayerState.DONE);
            case WAIT_REMOVE_CREW, WAIT_REMOVE_GOODS -> {
                worst.getKey().setValue(Optional.empty());
                playersState.put(username, PlayerState.DONE);
            }
        }

        playerIndex++;
        return autoCheckPlayers(board);

    }

    private boolean autoCheckPlayers(Board board) {
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) {
            PlayerData player = board.getPlayersByPos().get(playerIndex);

            PlayerState newState = warLines.get(warLineIndex).getKey().countCriteria(player, worst);
            playersState.put(player.getUsername(), newState);
            if (newState != PlayerState.DONE)
                return false;
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : board.getPlayersByPos())
            if (playersState.get(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone && worst.getKey().getValue().isPresent()) { // Apply malus
            PlayerState newState = warLines.get(warLineIndex).getValue().resolve(board, worst.getKey().getValue().get());
            playersState.put(worst.getKey().getValue().get().getUsername(), newState);
            if (newState == PlayerState.DONE)
                worst.getKey().setValue(Optional.empty());
        }

        if (hasDone && worst.getKey().getValue().isEmpty()) { // Malus already applied, go to the next line
            warLineIndex++;
            if (warLineIndex >= warLines.size()) {
                endCard(board);
                return true;
            }
            else {
                for (PlayerData player : board.getPlayersByPos())
                    playersState.put(player.getUsername(), PlayerState.WAIT);
                this.playerIndex = 0;
                return autoCheckPlayers(board);
            }
        }
        return false;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Integer value, String username, Board board) {
        if (commandType == PlayerState.WAIT_ENGINES && worst.getValue() > value) {
            PlayerData player = board.getPlayerEntityByUsername(username);
            worst.getKey().setValue(Optional.of(player));
            worst.setValue(value.doubleValue());
        }
        else if (commandType == PlayerState.WAIT_ROLL_DICES) {
            warLines.get(warLineIndex).getValue().doCommandEffects(commandType, value);
        }
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Boolean value, String username, Board board) {
        if (commandType == PlayerState.WAIT_SHIELD) {
            warLines.get(warLineIndex).getValue().doCommandEffects(commandType, value, username, board);
        }
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Double value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_CANNONS && worst.getValue() > value) {
            worst.getKey().setValue(Optional.of(player));
            worst.setValue(value);
        }
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