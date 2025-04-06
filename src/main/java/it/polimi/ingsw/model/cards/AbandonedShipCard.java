package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
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
    public boolean startCard(ModelFacade model, Board board) {
        this.playerIndex = 0;
        this.conquerorPlayerIndex = -1;
        this.players = board.getPlayersByPos();
        this.shipConquered = false;

        for (PlayerData player : board.getPlayersByPos())
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);

        return autoCheckPlayers(model, board);
    }

    @Override
    protected boolean changeState(ModelFacade model, Board board, String username) {

        PlayerState actState = model.getPlayerState(username);

        switch (actState) {
            case WAIT_REMOVE_CREW -> {
                model.setPlayerState(username, PlayerState.DONE);
                PlayerData player = board.getPlayerEntityByUsername(username);
                board.movePlayer(player, days * -1);
                player.setCredits(credits + player.getCredits());
            }
            case WAIT_BOOLEAN -> {
                if (conquerorPlayerIndex == players.indexOf(board.getPlayerEntityByUsername(username))) {
                    model.setPlayerState(username, PlayerState.WAIT_REMOVE_CREW);
                    shipConquered = true;
                }
                else
                    model.setPlayerState(username, PlayerState.DONE);
            }
        }

        playerIndex++;
        return autoCheckPlayers(model, board);
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

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
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
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
