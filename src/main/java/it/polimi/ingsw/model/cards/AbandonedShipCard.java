package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class AbandonedShipCard extends Card {

    @JsonProperty private final int crew;
    @JsonProperty private final int credits;
    @JsonProperty private final int days;

    private int playerIndex;
    private List<PlayerData> players;
    private boolean shipConquered;

    public AbandonedShipCard(int id, int level, boolean isLearner, int crew, int credits, int days) {
        super(id, level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.playerIndex = 0;
        this.players = new ArrayList<>(board.getPlayersByPos());
        this.shipConquered = false;

        for (PlayerData player : board.getPlayersByPos())
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);

        return autoCheckPlayers(model);
    }

    private boolean autoCheckPlayers(ModelFacade model) {
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

        return hasDone;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN && value) {
            model.setPlayerState(username, PlayerState.WAIT_REMOVE_CREW);
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
        if (commandType == PlayerState.WAIT_REMOVE_CREW) {
            model.setPlayerState(username, PlayerState.DONE);

            PlayerData player = board.getPlayerEntityByUsername(username);
            board.movePlayer(player, days * -1);
            player.setCredits(credits + player.getCredits());

            playerIndex++;
            return autoCheckPlayers(model);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        super.doSpecificCheck(commandType, cabins, this.crew, username, board);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean doLeftGameEffects(PlayerState state, ModelFacade model, Board board, String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        int indexOfLeftPlayer = players.indexOf(player);

        if (playerIndex > indexOfLeftPlayer) {
            players.remove(indexOfLeftPlayer);
            playerIndex--;
        } else if (playerIndex == indexOfLeftPlayer) {
            players.remove(indexOfLeftPlayer);
            return autoCheckPlayers(model);
        } else
            players.remove(indexOfLeftPlayer);

        return false;
    }
}