package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.CannonFire;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PiratesCard extends EnemiesCard {

    @JsonProperty private final int credits;
    @JsonProperty private final List<CannonFire> cannonFires;

    @JsonProperty private final List<String> defeatedPlayers;
    @JsonProperty private int cannonIndex;
    @JsonProperty private List<Integer> coords;

    public PiratesCard(int id, int level, boolean isLearner, int piratesFirePower, int credits, int days, List<CannonFire> cannonFires) {
        super(id, level, isLearner, days, piratesFirePower);
        this.credits = credits;
        this.cannonFires = cannonFires;
        this.defeatedPlayers = new ArrayList<>();
        this.coords = new ArrayList<>();
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.cannonIndex = 0;
        return super.startCard(model, board);
    }

    @Override
    public boolean calcHasDone(ModelFacade model, Board board) {
        boolean hasDone = true;
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone && defeatedPlayers.isEmpty())
            return true;
        else if (hasDone) {
            if (cannonIndex >= cannonFires.size())
                return true;
            else {
                for (String username : defeatedPlayers)
                    model.setPlayerState(username, PlayerState.WAIT);
                model.setPlayerState(defeatedPlayers.getFirst(), PlayerState.WAIT_ROLL_DICES);
            }
        }
        return false;
    }

    @Override
    public boolean defeatedMalus(ModelFacade model, PlayerData player) {
        defeatedPlayers.add(player.getUsername());
        model.setPlayerState(player.getUsername(), PlayerState.DONE);
        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ROLL_DICES) {
            this.coords.add(value);
            for (String defeatedPlayerUsername : defeatedPlayers) {
                PlayerData defeatedPlayer = board.getPlayerEntityByUsername(defeatedPlayerUsername);
                PlayerState newState = cannonFires.get(cannonIndex).hit(defeatedPlayer, coords.getLast());
                model.setPlayerState(defeatedPlayerUsername, newState);
            }
            cannonIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (value > enemyFirePower && !enemiesDefeated) { // Ask if user wants to redeem rewards
                model.setPlayerState(username, PlayerState.WAIT_BOOLEAN);
                enemiesDefeated = true;
                return false;
            }
            else if (value >= enemyFirePower) // Tie or pirates already defeated
                model.setPlayerState(username, PlayerState.DONE);
            else { // Player is defeated
                defeatedPlayers.add(username);
                model.setPlayerState(username, PlayerState.DONE);
            }
            playerIndex++;
            return autoCheckPlayers(model,board);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_BOOLEAN) {
            model.setPlayerState(username, PlayerState.DONE);
            if (value) {
                board.movePlayer(player, -1*days);
                player.setCredits(credits + player.getCredits());
            }
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        else if (commandType == PlayerState.WAIT_SHIELD) {
            if (value) // Shield activated
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else { // Not activated => find target and if present calc new state
                Optional<Component> target = cannonFires.get(cannonIndex).getTarget(player.getShip(), coords.getLast());
                target.ifPresent(component -> {
                    PlayerState newState = component.destroyComponent(player); // DONE or WAIT_SHIP_PART
                    model.setPlayerState(player.getUsername(), newState);
                });
            }
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIP_PART) {
            model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

}
