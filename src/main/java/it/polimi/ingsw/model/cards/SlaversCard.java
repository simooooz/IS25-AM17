package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.*;

public class SlaversCard extends Card {

    private final int crew;
    private final int credits;
    private final int days;
    private final int slaversFirePower;

    private List<PlayerData> players;
    private boolean slaversDefeated;
    private int playerIndex;

    public SlaversCard(int level, boolean isLearner, int crew, int credits, int days, int slaversFirePower) {
        super(level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
        this.slaversFirePower = slaversFirePower;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.slaversDefeated = false;
        this.playerIndex = 0;
        this.players = new ArrayList<>(board.getPlayersByPos());

        for (PlayerData player : players)
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        return autoCheckPlayers(model, board);
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            double freeCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(cannon -> !cannon.getIsDouble())
                    .mapToDouble(CannonComponent::calcPower).sum();
            if (freeCannonsPower > 0 && player.getShip().getCannonAlien())
                freeCannonsPower += 2;

            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(cannon -> cannon.getDirection() == DirectionType.NORTH ? 2 : 1).sum();

            if (slaversDefeated)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower > slaversFirePower) { // User wins automatically
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                slaversDefeated = true;
                return false;
            }
            else if (freeCannonsPower == slaversFirePower && doubleCannonsPower == 0)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= slaversFirePower) { // User could win
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_CANNONS);
                return false;
            }
            else { // User loses automatically
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_REMOVE_CREW);
                return false;
            }
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone) {
            if (!model.isLearnerMode())
                endCard(board);
            return true;
        }
        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (value > slaversFirePower && !slaversDefeated) { // Ask if user wants to redeem rewards
                model.setPlayerState(username, PlayerState.WAIT_BOOLEAN);
                slaversDefeated = true;
            }
            else if (value >= slaversFirePower) { // Tie or slavers already defeated
                model.setPlayerState(username, PlayerState.DONE);
                playerIndex++;
                return autoCheckPlayers(model, board);
            }
            else // Player is defeated
                model.setPlayerState(username, PlayerState.WAIT_REMOVE_CREW);

            return false;
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN) {
            model.setPlayerState(username, PlayerState.DONE);
            if (value) {
                PlayerData player = board.getPlayerEntityByUsername(username);
                board.movePlayer(player, -1 * days);
                player.setCredits(credits + player.getCredits());
            }
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW) {
            model.setPlayerState(username, PlayerState.DONE);
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        super.doSpecificCheck(commandType, cabins, this.crew, username, board);
    }

}