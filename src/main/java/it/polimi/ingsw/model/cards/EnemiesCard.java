package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class EnemiesCard extends Card {

    @JsonProperty protected final int days;
    @JsonProperty protected final int enemyFirePower;
    @JsonProperty protected boolean enemiesDefeated;

    protected List<PlayerData> players;
    protected int playerIndex;

    public EnemiesCard(int id, int level, boolean isLearner, int days, int enemyFirePower) {
        super(id, level, isLearner);
        this.days = days;
        this.enemyFirePower = enemyFirePower;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.enemiesDefeated = false;
        this.playerIndex = 0;
        this.players = new ArrayList<>(board.getPlayersByPos());

        for (PlayerData player : players)
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        return autoCheckPlayers(model, board);
    }

    protected boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            double freeCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(cannon -> !cannon.getIsDouble())
                    .mapToDouble(CannonComponent::calcPower).sum();
            if (freeCannonsPower > 0 && player.getShip().getCannonAlien())
                freeCannonsPower += 2;

            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(CannonComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToDouble(Double::doubleValue)
                    .sum();

            if (enemiesDefeated)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower > enemyFirePower) { // User wins automatically
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                enemiesDefeated = true;
                return false;
            }
            else if (freeCannonsPower == enemyFirePower && doubleCannonsPower == 0)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= enemyFirePower) { // User could win
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_CANNONS);
                return false;
            }
            else { // User loses automatically
                boolean shouldReturn = defeatedMalus(model, player);
                if (shouldReturn)
                    return false;
            }
        }

        return calcHasDone(model, board);
    }

    public boolean calcHasDone(ModelFacade model, Board board) {
        boolean hasDone = true;
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        return hasDone;
    }

    public abstract boolean defeatedMalus(ModelFacade model, PlayerData player);

    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (value > enemyFirePower && !enemiesDefeated) { // Ask if user wants to redeem rewards
                model.setPlayerState(username, PlayerState.WAIT_BOOLEAN);
                enemiesDefeated = true;
            }
            else if (value >= enemyFirePower) { // Tie or slavers already defeated
                model.setPlayerState(username, PlayerState.DONE);
                playerIndex++;
                return autoCheckPlayers(model, board);
            }
            else // Player is defeated
                defeatedMalus(model, board.getPlayerEntityByUsername(username));

            return false;
        }
        throw new RuntimeException("Command type not valid");
    }

}