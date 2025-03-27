package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;

public class AbandonedShipCard extends Card {

    private final int crew;
    private final int credits;
    private final int days;

    private int playerIndex;
    private int conquerorPlayerIndex;
    private List<PlayerData> players;
    private boolean shipConquered;

    public AbandonedShipCard(int level, boolean isLearner, int crew, int credits, int days) {
        super(level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
    }

    @Override
    public boolean startCard(Board board) {
        this.playerIndex = 0;
        this.conquerorPlayerIndex = -1;
        this.players = board.getPlayersByPos();
        this.shipConquered = false;

        for (PlayerData player : board.getPlayersByPos())
            playersState.put(player.getUsername(), PlayerState.WAIT);

        return autoCheckPlayers(board);
    }

    @Override
    protected boolean changeState(Board board, String username) {

        PlayerState actState = playersState.get(username);

        switch (actState) {
            case WAIT_REMOVE_CREW -> {
                playersState.put(username, PlayerState.DONE);
                PlayerData player = board.getPlayerEntityByUsername(username);
                board.movePlayer(player, days * -1);
                player.setCredits(credits + player.getCredits());
            }
            case WAIT_BOOLEAN -> {
                if (conquerorPlayerIndex == players.indexOf(board.getPlayerEntityByUsername(username))) {
                    playersState.put(username, PlayerState.WAIT_REMOVE_CREW);
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
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

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
        for (PlayerData player : players)
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
            conquerorPlayerIndex = players.indexOf(player);
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        super.doSpecificCheck(commandType, cabins, this.crew, username, board);
    }

}
