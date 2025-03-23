package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.CriteriaType;
import it.polimi.ingsw.model.cards.utils.PenaltyCombatZone;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
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
    public void resolve(Board board) throws Exception {
    }

    public void startCard(Board board) {
        if (board.getPlayersByPos().size() < 2)
            endCard();
        else {
            this.warLineIndex = 0;
            this.playerIndex = 0;
            SimpleEntry<Character, Optional<PlayerData>> temp = new SimpleEntry<>('a', Optional.empty());
            this.worst = new SimpleEntry<>(temp, 0.0);

            for (PlayerData player : board.getPlayersByPos())
                playersState.put(player.getUsername(), CardState.WAIT);

            autoCheckPlayers(board);
        }
    }

    public void changeState(Board board, String username) throws Exception {

        CardState actState = playersState.get(username);

        switch (actState) {
            case WAIT_CANNON, WAIT_ENGINE, WAIT_REMOVE_CREW, WAIT_REMOVE_GOOD, WAIT_SHIELD, WAIT_ROLL_DICE -> playersState.put(username, CardState.DONE);
        }

        playerIndex++;
        autoCheckPlayers(board);

    }

    private void autoCheckPlayers(Board board) {
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) {
            PlayerData player = board.getPlayersByPos().get(playerIndex);

            CardState newState = warLines.get(warLineIndex).getKey().countCriteria(player, worst);
            playersState.put(player.getUsername(), newState);
            if (newState != CardState.DONE)
                return;
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : board.getPlayersByPos())
            if (playersState.get(player.getUsername()) != CardState.DONE)
                hasDone = false;

        if (hasDone && worst.getKey().getValue().isPresent()) { // Apply malus
            CardState newState = warLines.get(warLineIndex).getValue().resolve(board, worst.getKey().getValue().get());
            playersState.put(worst.getKey().getValue().get().getUsername(), newState);
            if (newState == CardState.DONE)
                worst.getKey().setValue(Optional.empty());
        }

        if (hasDone && worst.getKey().getValue().isEmpty()) { // Malus already applied, go to the next line
            warLineIndex++;
            if (warLineIndex >= warLines.size()) {
                endCard();
            }
            else {
                for (PlayerData player : board.getPlayersByPos())
                    playersState.put(player.getUsername(), CardState.WAIT);
                this.playerIndex = 0;
                autoCheckPlayers(board);
            }
        }
    }

    public void endCard() {

    }

    public void doCommandEffects(CardState commandType, Integer value, String username, Board board) {
        if (commandType == CardState.WAIT_ENGINE && worst.getValue() > value) {
            PlayerData player = board.getPlayerEntityByUsername(username);
            worst.getKey().setValue(Optional.of(player));
            worst.setValue(value.doubleValue());
        }
        else if (commandType == CardState.WAIT_ROLL_DICE) {
            warLines.get(warLineIndex).getValue().doCommandEffects(commandType, value);
        }
    }

    public void doCommandEffects(CardState commandType, Boolean value, String username, Board board) {
        if (commandType == CardState.WAIT_SHIELD) {
            warLines.get(warLineIndex).getValue().doCommandEffects(commandType, value, username, board);
        }
    }

    public void doCommandEffects(CardState commandType, Double value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == CardState.WAIT_CANNON && worst.getValue() > value) {
            worst.getKey().setValue(Optional.of(player));
            worst.setValue(value);
        }
    }

}